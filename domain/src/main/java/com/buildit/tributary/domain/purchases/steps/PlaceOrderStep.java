package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.*;
import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.functional.Returning;

public interface PlaceOrderStep extends Returning<OrderReference> {

  @StepMethod
  OrderReference placeOrder(
      @KeyName("customerId") String customerId,
      ProductBasket productBasket,
      PaymentReference paymentReference
  );

}
