package com.buildit.tributary.rest.protocol;

import com.buildit.tributary.rest.protocol.ImmutableGreetingRequest;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as= ImmutableGreetingRequest.class)
public interface GreetingRequest {
  String title();
  String firstName();
  String secondName();
}
