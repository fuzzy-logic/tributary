package com.buildit.tributary.application.greeting.flows;

import com.buildit.tributary.domain.greeting.Addressee;
import com.buildit.tributary.domain.greeting.Greeting;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.scratchpad.Keys;

public final class GreetingFlowKeys {

  public static final Key<String> message = Keys.named("message");
  public static final Key<Addressee> addressee = Keys.named("addressee");
  public static final Key<Greeting> greeting = Keys.named("greeting");
}
