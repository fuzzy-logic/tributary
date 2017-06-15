package com.buildit.tributary.application.purchases.flows;

import com.buildit.tributary.domain.purchases.CustomerId;
import com.buildit.tributary.domain.purchases.ProductBasket;
import com.buildit.tributary.domain.purchases.PurchaseOutcome;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.scratchpad.Keys;

public class PurchaseFlowKeys {

  public static final Key<CustomerId> customerId = Keys.named("customerId");
  public static final Key<ProductBasket> productBasket = Keys.named("productBasket");
  public static final Key<PurchaseOutcome> purchaseOutcome = Keys.named("purchaseOutcome");

}
