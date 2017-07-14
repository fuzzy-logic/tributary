package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.Status;
import com.buildit.tributary.domain.purchases.ProductBasket;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.functional.Returning;

public interface ReleaseStockStep extends Returning<Status> {

  @StepMethod
  Status releaseStock(ProductBasket productBasket);

}
