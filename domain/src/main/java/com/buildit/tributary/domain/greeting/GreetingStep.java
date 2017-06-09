package com.buildit.tributary.domain.greeting;

import com.codepoetics.fluvius.api.functional.F1;

/**
 * Generates an on-brand greeting
 */
public interface GreetingStep extends F1<Addressee, Greeting> {
  Greeting apply(Addressee addressee);
}
