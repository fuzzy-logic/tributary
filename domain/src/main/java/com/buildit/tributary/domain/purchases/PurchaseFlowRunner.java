package com.buildit.tributary.domain.purchases;

import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.functional.Returning;
import com.codepoetics.fluvius.api.wrapping.FlowRunner;

public interface PurchaseFlowRunner extends Returning<PurchaseOutcome> {

  FlowRunner<PurchaseOutcome> run(
      @KeyName("customerId") String customerId,
      ProductBasket productBasket);

}
