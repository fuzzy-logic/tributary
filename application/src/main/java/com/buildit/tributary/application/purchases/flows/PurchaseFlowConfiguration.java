package com.buildit.tributary.application.purchases.flows;

import com.buildit.tributary.domain.Status;
import com.buildit.tributary.domain.purchases.*;
import com.buildit.tributary.domain.purchases.steps.*;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.functional.RecoveryFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.codepoetics.fluvius.flows.Flows.onSuccess;

@Configuration
public class PurchaseFlowConfiguration {

  private static final Key<PaymentAmount> paymentAmount = Keys.named("paymentAmount");
  private static final Key<BillingDetails> billingDetails = Keys.named("billingDetails");
  private static final Key<PaymentReference> paymentReference = Keys.named("paymentReference");
  private static final Key<OrderReference> orderReference = Keys.named("orderReference");
  private static final Key<Status> releaseStockResult = Keys.named("releaseStockResult");
  private static final Key<Status> refundPaymentResult = Keys.named("refundPaymentResult");
  private static final Key<Status> notificationResult = Keys.named("notificationResult");

  private final ReserveStockStep reserveStockStep;
  private final GetBillingDetailsStep getBillingDetailsStep;
  private final ReleaseStockStep releaseStockStep;
  private final MakePaymentStep makePaymentStep;
  private final PlaceOrderStep placeOrderStep;
  private final RefundPaymentStep refundPaymentStep;
  private final OrderNotificationStep notificationStep;

  @Autowired
  public PurchaseFlowConfiguration(
      ReserveStockStep reserveStockStep,
      GetBillingDetailsStep getBillingDetailsStep,
      ReleaseStockStep releaseStockStep, MakePaymentStep makePaymentStep,
      PlaceOrderStep placeOrderStep,
      RefundPaymentStep refundPaymentStep,
      OrderNotificationStep notificationStep) {
    this.reserveStockStep = reserveStockStep;
    this.getBillingDetailsStep = getBillingDetailsStep;
    this.releaseStockStep = releaseStockStep;
    this.makePaymentStep = makePaymentStep;
    this.placeOrderStep = placeOrderStep;
    this.refundPaymentStep = refundPaymentStep;
    this.notificationStep = notificationStep;
  }

  private static <T> Flow<PurchaseOutcome> failureFlow(Key<T> key, String reason) {
    return Flows.recoverFrom(key).to(PurchaseFlowKeys.purchaseOutcome).using(
        "Fail because " + reason,
        new RecoveryFunction<PurchaseOutcome>() {
      @Override
      public PurchaseOutcome apply(Exception failure) {
        return ImmutablePurchaseOutcome.builder().purchaseSucceeded(false).failureReason(failure.getMessage()).build();
      }
    });
  }

  @Bean
  public Flow<PurchaseOutcome> purchaseFlow() {
    return reserveStockFlow()
        .then(onSuccess(paymentAmount, continueToBillingFlow())
            .otherwise(failureFlow(paymentAmount, "Insufficient stock available")));
  }

  private Flow<PaymentAmount> reserveStockFlow() {
    return Flows.obtaining(paymentAmount).from(PurchaseFlowKeys.productBasket, PurchaseFlowKeys.customerId).using("Reserve stock", reserveStockStep);
  }

  private Flow<PurchaseOutcome> continueToBillingFlow() {
    return getBillingDetailsFlow()
        .then(onSuccess(billingDetails, continueToPaymentFlow())
            .otherwise(releaseStockFlow()
                .then(failureFlow(billingDetails,"Unable to retrieve billing details"))));
  }

  private Flow<BillingDetails> getBillingDetailsFlow() {
    return Flows.obtaining(billingDetails).from(PurchaseFlowKeys.customerId).using("Get customer billing details", getBillingDetailsStep);
  }

  private Flow<PurchaseOutcome> continueToPaymentFlow() {
    return Flows.obtaining(paymentReference).from(billingDetails, paymentAmount).using("Make payment", makePaymentStep)
        .then(onSuccess(paymentReference, continueToOrderFlow())
            .otherwise(releaseStockFlow()
                .then(failureFlow(paymentReference,"Payment failed"))));
  }

  private Flow<PurchaseOutcome> continueToOrderFlow() {
    return placeOrderFlow()
        .then(onSuccess(orderReference, continueToNotificationFlow())
            .otherwise(refundPaymentFlow()
                .then(releaseStockFlow())
                .then(failureFlow(orderReference,"Unable to place order"))));
  }

  private Flow<OrderReference> placeOrderFlow() {
    return Flows.obtaining(orderReference).from(PurchaseFlowKeys.customerId, PurchaseFlowKeys.productBasket, paymentReference).using("Place order", placeOrderStep);
  }

  private Flow<PurchaseOutcome> continueToNotificationFlow() {
    return notificationFlow()
        .then(outcomeOkFlow());
  }

  private Flow<PurchaseOutcome> outcomeOkFlow() {
    return Flows.obtaining(PurchaseFlowKeys.purchaseOutcome).from(orderReference).using("Purchase completed ok", new F1<OrderReference, PurchaseOutcome>() {
      @Override
      public PurchaseOutcome apply(OrderReference myOrderReference) {
        return ImmutablePurchaseOutcome.builder().purchaseSucceeded(true).orderReference(myOrderReference).build();
      }
    });
  }

  private Flow<Status> notificationFlow() {
    return Flows.from(PurchaseFlowKeys.customerId, PurchaseFlowKeys.productBasket, paymentReference, orderReference).to(notificationResult)
        .using("Send notification", notificationStep);
  }

  private Flow<Status> refundPaymentFlow() {
    return Flows.obtaining(refundPaymentResult).from(paymentReference).using("Refund payment", refundPaymentStep);
  }

  private Flow<Status> releaseStockFlow() {
    return Flows.obtaining(releaseStockResult).from(PurchaseFlowKeys.productBasket).using("Release stock", releaseStockStep);
  }
}
