package com.buildit.tributary.purchases.flows;

import com.buildit.tributary.application.purchases.flows.PurchaseFlowConfiguration;
import com.buildit.tributary.application.purchases.flows.PurchaseFlowKeys;
import com.buildit.tributary.domain.Status;
import com.buildit.tributary.domain.purchases.*;
import com.buildit.tributary.domain.purchases.steps.*;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.history.FlowHistoryRepository;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.history.History;
import com.codepoetics.fluvius.json.history.FlowHistoryView;
import com.codepoetics.fluvius.json.history.JsonEventDataSerialiser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class PurchaseFlowTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final FlowHistoryRepository<JsonNode> repository = History.createInMemoryRepository(
      JsonEventDataSerialiser.using(mapper));

  private final FlowCompiler compiler = Compilers.builder()
      .loggingToConsole()
      .recordingTo(repository)
      .build();

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
  public void prettyPrint() throws Exception {
    System.out.println(Flows.prettyPrint(configuration.purchaseFlow()));
    UUID flowId = UUID.randomUUID();

    configureSuccess();

    PurchaseOutcome outcome = compiler.compile(configuration.purchaseFlow()).run(
        flowId,
        PurchaseFlowKeys.customerId.of(ImmutableCustomerId.builder().id(UUID.randomUUID()).build()),
        PurchaseFlowKeys.productBasket.of(ImmutableProductBasket.builder().build())
    );

    System.out.println(mapper.writeValueAsString(FlowHistoryView.from(repository.getFlowHistory(flowId))));

    assertTrue(outcome.purchaseSucceeded());
  }

  private void configureSuccess() throws Exception {
    reserveStockSucceeds();
    getBillingDetailsSucceeds();
    makePaymentSucceeds();
    placeOrderSucceeds();
    notificationSucceeds();
  }

  private void reserveStockSucceeds() throws Exception {
    stubSuccess(reserveStockStep.apply(any(ProductBasket.class), any(CustomerId.class)),
        ImmutablePaymentAmount.builder()
            .amount(new BigDecimal("101.01"))
            .build());
  }

  private void getBillingDetailsSucceeds() throws Exception {
    stubSuccess(getBillingDetailsStep.apply(any(CustomerId.class)), ImmutableBillingDetails.builder().build());
  }

  private void makePaymentSucceeds() throws Exception {
    stubSuccess(makePaymentStep.apply(any(BillingDetails.class), any(PaymentAmount.class)),
      ImmutablePaymentReference.builder()
          .reference(UUID.randomUUID())
          .build());
  }

  private void placeOrderSucceeds() throws Exception {
    stubSuccess(placeOrderStep.apply(any(CustomerId.class), any(ProductBasket.class), any(PaymentReference.class)),
        ImmutableOrderReference.builder()
            .reference(UUID.randomUUID())
            .build());
  }

  private void notificationSucceeds() throws Exception {
    stubSuccess(orderNotificationStep.apply(any(Scratchpad.class)), Status.COMPLETE);
  }

  private <T> void stubSuccess(T stubSeed, T result) {
    when(stubSeed).thenReturn(result);
  }
}
