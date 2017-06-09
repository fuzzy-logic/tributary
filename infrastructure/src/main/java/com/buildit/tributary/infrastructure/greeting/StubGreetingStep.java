package com.buildit.tributary.infrastructure.greeting;

import com.buildit.tributary.domain.greeting.Addressee;
import com.buildit.tributary.domain.greeting.Greeting;
import com.buildit.tributary.domain.greeting.GreetingStep;
import com.buildit.tributary.domain.greeting.ImmutableGreeting;
import org.springframework.stereotype.Component;

@Component
public class StubGreetingStep implements GreetingStep {
  @Override
  public Greeting apply(Addressee addressee) {
    return ImmutableGreeting.builder()
        .message("Hello, " + addressee.title() + " " + addressee.firstName() + " " + addressee.lastName())
        .build();
  }
}
