package com.buildit.tributary.domain.greeting;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize
public interface Addressee {
  String title();
  String firstName();
  String lastName();
}
