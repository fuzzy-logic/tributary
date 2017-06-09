package com.buildit.tributary.domain.greeting;

import org.immutables.value.Value;

@Value.Immutable
public interface Addressee {
  String title();
  String firstName();
  String lastName();
}
