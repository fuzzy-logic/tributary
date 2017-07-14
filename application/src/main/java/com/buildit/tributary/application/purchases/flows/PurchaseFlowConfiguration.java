package com.buildit.tributary.application.purchases.flows;

import com.buildit.tributary.domain.Status;
import com.buildit.tributary.domain.purchases.*;
import com.buildit.tributary.domain.purchases.steps.*;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.functional.Returning;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.wrapping.FlowExecutionProxyFactory;
import com.codepoetics.fluvius.api.wrapping.FlowWrapperFactory;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Keys;
import com.codepoetics.fluvius.wrapping.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PurchaseFlowConfiguration {

  private final Key<PurchaseOutcome> purchaseOutcomeKey;
  private final FlowWrapperFactory factory;
  private final FlowExecutionProxyFactory proxyFactory;
  private final Flow<PaymentAmount> reserveStockFlow;
  private final Flow<BillingDetails> getBillingDetailsFlow;
  private final Flow<Status> releaseStockFlow;
  private final Flow<PaymentReference> makePaymentFlow;
  private final Flow<OrderReference> placeOrderFlow;
  private final Flow<Status> refundPaymentFlow;
  private final Flow<Status> notificationFlow;

  @Autowired
  public PurchaseFlowConfiguration(
      FlowCompiler compiler,
      ReserveStockStep reserveStockStep,
      GetBillingDetailsStep getBillingDetailsStep,
      ReleaseStockStep releaseStockStep, MakePaymentStep makePaymentStep,
      PlaceOrderStep placeOrderStep,
      RefundPaymentStep refundPaymentStep,
      OrderNotificationStep notificationStep) {
    KeyProvider provider = Keys.createProvider();

    this.factory = Wrappers.createWrapperFactory(provider);
    this.proxyFactory = Wrappers.createProxyFactory(compiler, provider);

    this.purchaseOutcomeKey = provider.getKey("purchaseOutcome", PurchaseOutcome.class);
    this.reserveStockFlow = factory.flowFor(reserveStockStep);
    this.getBillingDetailsFlow = factory.flowFor(getBillingDetailsStep);
    this.releaseStockFlow = factory.flowFor(releaseStockStep);
    this.makePaymentFlow = factory.flowFor(makePaymentStep);
    this.placeOrderFlow = factory.flowFor(placeOrderStep);
    this.refundPaymentFlow = factory.flowFor(refundPaymentStep);
    this.notificationFlow = factory.flowFor(notificationStep);
  }

  @Bean
  public PurchaseFlowRunner purchaseFlowRunner(Flow<PurchaseOutcome> purchaseFlow) {
    return proxyFactory.proxyFor(PurchaseFlowRunner.class, purchaseFlow);
  }

  @Bean
  public Flow<PurchaseOutcome> purchaseFlow() {
    return reserveStockFlow.branchOnResult()
        .onFailure(failureFlow(reserveStockFlow, "Insufficient stock available"))
        .otherwise(continueToBillingFlow());
  }


  private Flow<PurchaseOutcome> continueToBillingFlow() {
    return getBillingDetailsFlow.branchOnResult()
        .onFailure(releaseStockFlow
            .then(failureFlow(getBillingDetailsFlow, "Unable to retrieve billing details")))
        .otherwise(continueToPaymentFlow());
  }

  private Flow<PurchaseOutcome> continueToPaymentFlow() {
    return makePaymentFlow.branchOnResult()
        .onFailure(
            releaseStockFlow
                .then(failureFlow(makePaymentFlow, "Payment failed")))
        .otherwise(continueToOrderFlow());
  }

  private Flow<PurchaseOutcome> continueToOrderFlow() {
    return placeOrderFlow.branchOnResult()
        .onFailure(
            releaseStockFlow
              .then(refundPaymentFlow)
              .then(failureFlow(placeOrderFlow, "Unable to place order")))
        .otherwise(continueToNotificationFlow());
  }

  private Flow<PurchaseOutcome> continueToNotificationFlow() {
    return notificationFlow
        .then(factory.flowFor(new OutcomeOKStep()));
  }

  private <T> Flow<PurchaseOutcome> failureFlow(final Flow<T> failedFlow, String reason) {
    return Flows.from(failedFlow.getProvidedKey()).to(purchaseOutcomeKey).using(
        "Fail because " + reason,
        new ScratchpadFunction<PurchaseOutcome>() {
          @Override
          public PurchaseOutcome apply(Scratchpad scratchpad) {
            return ImmutablePurchaseOutcome.builder()
                .purchaseSucceeded(false)
                .failureReason(scratchpad.getFailureReason(failedFlow.getProvidedKey()).getMessage())
                .build();
          }
        });
  }

  public static class OutcomeOKStep implements Returning<PurchaseOutcome> {

    @StepMethod
    public PurchaseOutcome getOutcome(OrderReference myOrderReference) {
      return ImmutablePurchaseOutcome.builder()
          .purchaseSucceeded(true)
          .orderReference(myOrderReference)
          .build();
    }

  }
}
