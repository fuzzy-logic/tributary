package com.buildit.tributary.purchases.flows;

import com.buildit.tributary.application.purchases.flows.PurchaseFlowConfiguration;
import com.buildit.tributary.domain.purchases.steps.*;
import com.codepoetics.fluvius.flows.Flows;
import org.junit.Test;
import org.mockito.Mockito;

public class PurchaseFlowTest {

  private final ReserveStockStep reserveStockStep = Mockito.mock(ReserveStockStep.class);
  private final MakePaymentStep makePaymentStep = Mockito.mock(MakePaymentStep.class);
  private final OrderNotificationStep orderNotificationStep = Mockito.mock(OrderNotificationStep.class);
  private final PlaceOrderStep placeOrderStep = Mockito.mock(PlaceOrderStep.class);
  private final RefundPaymentStep refundPaymentStep = Mockito.mock(RefundPaymentStep.class);
  private final ReleaseStockStep releaseStockStep = Mockito.mock(ReleaseStockStep.class);
  private final GetBillingDetailsStep getBillingDetailsStep = Mockito.mock(GetBillingDetailsStep.class);

  private final PurchaseFlowConfiguration configuration = new PurchaseFlowConfiguration(
      reserveStockStep,
      getBillingDetailsStep,
      releaseStockStep,
      makePaymentStep,
      placeOrderStep,
      refundPaymentStep,
      orderNotificationStep
  );

  @Test
  public void prettyPrint() {
    System.out.println(Flows.prettyPrint(configuration.purchaseFlow()));
  }
}
