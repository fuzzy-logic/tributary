package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.PaymentAmount;
import com.buildit.tributary.domain.purchases.ProductBasket;

import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.functional.Returning;

public interface ReserveStockStep extends Returning<PaymentAmount> {

  @StepMethod
  PaymentAmount reserveStock(
      ProductBasket productBasket,
      @KeyName("customerId") String customerId);

}
