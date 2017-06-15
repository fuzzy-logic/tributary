package com.buildit.tributary.application.purchases.flows;

import com.buildit.tributary.domain.purchases.*;
import com.buildit.tributary.domain.purchases.steps.*;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.services.ServiceCallResult;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.codepoetics.fluvius.services.ServiceCalls.afterServiceCall;

@Configuration
public class PurchaseFlowConfiguration {

  private static final Key<ServiceCallResult<PaymentAmount>> reserveStockResult = Keys.named("reserveStockResult");
  private static final Key<ServiceCallResult<BillingDetails>> billingDetailsResult = Keys.named("billingDetailsResult");
  private static final Key<ServiceCallResult<Void>> releaseStockResult = Keys.named("releaseStockResult");
  private static final Key<ServiceCallResult<PaymentReference>> makePaymentResult = Keys.named("makePaymentResult");
  private static final Key<ServiceCallResult<OrderReference>> placeOrderResult = Keys.named("placeOrderResult");
  private static final Key<ServiceCallResult<Void>> refundPaymentResult = Keys.named("refundPaymentResult");
  private static final Key<ServiceCallResult<Void>> notificationResult = Keys.named("notificationResult");

  private static final Key<PaymentAmount> paymentAmount = Keys.named("paymentAmount");
  private static final Key<BillingDetails> billingDetails = Keys.named("billingDetails");
  private static final Key<PaymentReference> paymentReference = Keys.named("paymentReference");
  private static final Key<OrderReference> orderReference = Keys.named("orderReference");


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

  private static <T> Flow<PurchaseOutcome> failureFlow(Key<ServiceCallResult<T>> key, String reason) {
    return Flows.obtaining(PurchaseFlowKeys.purchaseOutcome).from(key).using(
        "Fail because " + reason,
        new F1<ServiceCallResult<T>, PurchaseOutcome>() {
      @Override
      public PurchaseOutcome apply(ServiceCallResult<T> serviceCallResult) {
        return null;
      }
    });
  }

  @Bean
  public Flow<PurchaseOutcome> purchaseFlow() {
    return reserveStockFlow()
        .then(afterServiceCall(reserveStockResult, paymentAmount)
            .onSuccess(continueToBillingFlow())
            .otherwise(failureFlow(reserveStockResult, "Insufficient stock available")));
  }

  private Flow<ServiceCallResult<PaymentAmount>> reserveStockFlow() {
    return Flows.obtaining(reserveStockResult).from(PurchaseFlowKeys.productBasket, PurchaseFlowKeys.customerId).using("Reserve stock", reserveStockStep);
  }

  private Flow<PurchaseOutcome> continueToBillingFlow() {
    return getBillingDetailsFlow()
        .then(afterServiceCall(billingDetailsResult, billingDetails)
            .onSuccess(continueToPaymentFlow())
            .otherwise(releaseStockFlow()
                .then(failureFlow(billingDetailsResult,"Unable to retrieve billing details"))));
  }

  private Flow<ServiceCallResult<BillingDetails>> getBillingDetailsFlow() {
    return Flows.obtaining(billingDetailsResult).from(PurchaseFlowKeys.customerId).using("Get customer billing details", getBillingDetailsStep);
  }

  private Flow<PurchaseOutcome> continueToPaymentFlow() {
    return Flows.obtaining(makePaymentResult).from(billingDetails, paymentAmount).using("Make payment", makePaymentStep)
        .then(afterServiceCall(makePaymentResult, paymentReference)
            .onSuccess(continueToOrderFlow())
            .otherwise(releaseStockFlow()
                .then(failureFlow(makePaymentResult,"Payment failed"))));
  }

  private Flow<PurchaseOutcome> continueToOrderFlow() {
    return placeOrderFlow()
        .then(afterServiceCall(placeOrderResult, orderReference)
            .onSuccess(continueToNotificationFlow())
            .otherwise(refundPaymentFlow()
                .then(releaseStockFlow())
                .then(failureFlow(placeOrderResult, "Unable to place order"))));
  }

  private Flow<ServiceCallResult<OrderReference>> placeOrderFlow() {
    return Flows.obtaining(placeOrderResult).from(PurchaseFlowKeys.customerId, PurchaseFlowKeys.productBasket, paymentReference).using("Place order", placeOrderStep);
  }

  private Flow<PurchaseOutcome> continueToNotificationFlow() {
    return notificationFlow()
        .then(outcomeOkFlow());
  }

  private Flow<PurchaseOutcome> outcomeOkFlow() {
    return Flows.obtaining(PurchaseFlowKeys.purchaseOutcome).from(orderReference).using("Purchase completed ok", new F1<OrderReference, PurchaseOutcome>() {
      @Override
      public PurchaseOutcome apply(OrderReference orderReference1) {
        return null;
      }
    });
  }

  private Flow<ServiceCallResult<Void>> notificationFlow() {
    return Flows.from(PurchaseFlowKeys.customerId, PurchaseFlowKeys.productBasket, paymentReference, orderReference).to(notificationResult)
        .using("Send notification", notificationStep);
  }

  private Flow<ServiceCallResult<Void>> refundPaymentFlow() {
    return Flows.obtaining(refundPaymentResult).from(paymentReference).using("Refund payment", refundPaymentStep);
  }

  private Flow<ServiceCallResult<Void>> releaseStockFlow() {
    return Flows.obtaining(releaseStockResult).from(PurchaseFlowKeys.productBasket).using("Release stock", releaseStockStep);
  }
}
