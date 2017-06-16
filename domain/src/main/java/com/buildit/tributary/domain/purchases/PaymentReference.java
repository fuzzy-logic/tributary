package com.buildit.tributary.domain.purchases;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@JsonSerialize
public interface PaymentReference {
  UUID reference();
}
