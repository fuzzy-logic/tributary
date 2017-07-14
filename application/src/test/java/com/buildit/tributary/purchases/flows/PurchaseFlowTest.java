package com.buildit.tributary.purchases.flows;

import com.buildit.tributary.application.purchases.flows.PurchaseFlowConfiguration;

import com.buildit.tributary.domain.Status;
import com.buildit.tributary.domain.purchases.*;
import com.buildit.tributary.domain.purchases.steps.*;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.history.FlowEventRepository;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.history.FlowEventRepositories;
import com.codepoetics.fluvius.json.history.FlowHistoryView;
import com.codepoetics.fluvius.json.history.JsonEventDataSerialiser;
import com.codepoetics.fluvius.tracing.TraceMaps;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class PurchaseFlowTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final FlowEventRepository<JsonNode> repository = FlowEventRepositories.createInMemory(
      JsonEventDataSerialiser.using(mapper));

  private final FlowCompiler compiler = Compilers.builder()
      .loggingToConsole()
      .tracingWith(repository)
      .build();

  private final ReserveStockStep reserveStockStep = Mockito.mock(ReserveStockStep.class);
  private final MakePaymentStep makePaymentStep = Mockito.mock(MakePaymentStep.class);
  private final OrderNotificationStep orderNotificationStep = Mockito.mock(OrderNotificationStep.class);
  private final PlaceOrderStep placeOrderStep = Mockito.mock(PlaceOrderStep.class);
  private final RefundPaymentStep refundPaymentStep = Mockito.mock(RefundPaymentStep.class);
  private final ReleaseStockStep releaseStockStep = Mockito.mock(ReleaseStockStep.class);
  private final GetBillingDetailsStep getBillingDetailsStep = Mockito.mock(GetBillingDetailsStep.class);

  private final PurchaseFlowConfiguration configuration = new PurchaseFlowConfiguration(
      compiler,
      reserveStockStep,
      getBillingDetailsStep,
      releaseStockStep,
      makePaymentStep,
      placeOrderStep,
      refundPaymentStep,
      orderNotificationStep
  );

  @Before
  public void initialiseMocks() throws Exception {
    when(releaseStockStep.releaseStock(any(ProductBasket.class))).thenReturn(Status.COMPLETE);
    when (refundPaymentStep.refundPayment(any(PaymentReference.class))).thenReturn(Status.COMPLETE);
  }

  @Test
  public void happyPath() throws Exception {
    Flow<PurchaseOutcome> flow = configuration.purchaseFlow();
    System.out.println(Flows.prettyPrint(flow));

    UUID flowId = UUID.randomUUID();
    configureSuccess();

    PurchaseOutcome outcome = configuration.purchaseFlowRunner(flow).run(
        UUID.randomUUID().toString(),
        ImmutableProductBasket.builder().build()
    ).run(flowId);

    assertTrue(outcome.purchaseSucceeded());

    System.out.println(mapper.writeValueAsString(FlowHistoryView.from(flowId, TraceMaps.getTraceMap(flow), repository.getEvents(flowId))));
  }

  @Test
  public void paymentFails() throws Exception {
    Flow<PurchaseOutcome> flow = configuration.purchaseFlow();

    UUID flowId = UUID.randomUUID();

    reserveStockSucceeds();
    getBillingDetailsSucceeds();
    makePaymentFails("Your money's no good here, Mr Torrance");
    placeOrderSucceeds();
    notificationSucceeds();

      PurchaseOutcome outcome = configuration.purchaseFlowRunner(flow).run(
          UUID.randomUUID().toString(),
          ImmutableProductBasket.builder().build())
      .run(flowId);

      assertFalse(outcome.purchaseSucceeded());
      assertTrue(outcome.failureReason().isPresent());
      assertEquals("Your money's no good here, Mr Torrance", outcome.failureReason().get());
  }

  private void configureSuccess() throws Exception {
    reserveStockSucceeds();
    getBillingDetailsSucceeds();
    makePaymentSucceeds();
    placeOrderSucceeds();
    notificationSucceeds();
  }

  private void reserveStockSucceeds() throws Exception {
    stubSuccess(reserveStockStep.reserveStock(any(ProductBasket.class), any(String.class)),
        ImmutablePaymentAmount.builder()
            .amount(new BigDecimal("101.01"))
            .build());
  }

  private void getBillingDetailsSucceeds() throws Exception {
    stubSuccess(getBillingDetailsStep.getBillingDetails(any(String.class)), ImmutableBillingDetails.builder().build());
  }

  private void makePaymentSucceeds() throws Exception {
    stubSuccess(makePaymentStep.makePayment(any(BillingDetails.class), any(PaymentAmount.class)),
      ImmutablePaymentReference.builder()
          .reference(UUID.randomUUID())
          .build());
  }

  private void makePaymentFails(String failureMessage) throws Exception {
    when(makePaymentStep.makePayment(any(BillingDetails.class), any(PaymentAmount.class))).thenThrow(new IllegalStateException(failureMessage));
  }

  private void placeOrderSucceeds() throws Exception {
    stubSuccess(placeOrderStep.placeOrder(any(String.class), any(ProductBasket.class), any(PaymentReference.class)),
        ImmutableOrderReference.builder()
            .reference(UUID.randomUUID())
            .build());
  }

  private void notificationSucceeds() throws Exception {
    stubSuccess(orderNotificationStep.sendOrderNotification(
        any(String.class),
        any(PaymentReference.class),
        any(PaymentAmount.class)), Status.COMPLETE);
  }

  private <T> void stubSuccess(T stubSeed, T result) {
    when(stubSeed).thenReturn(result);
  }
}
