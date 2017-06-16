package com.buildit.tributary.domain.purchases;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize
public interface PurchaseOutcome {
  boolean purchaseSucceeded();
  Optional<String> failureReason();
  Optional<OrderReference> orderReference();
}
