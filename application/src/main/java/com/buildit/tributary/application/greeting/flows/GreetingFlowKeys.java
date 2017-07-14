package com.buildit.tributary.application.greeting.flows;

import com.buildit.tributary.domain.greeting.Addressee;
import com.buildit.tributary.domain.greeting.Greeting;
import com.codepoetics.fluvius.api.scratchpad.Key;

public final class GreetingFlowKeys {

  public static final Key<String> message = Key.named("message");
  public static final Key<Addressee> addressee = Key.named("addressee");
  public static final Key<Greeting> greeting = Key.named("greeting");

}
