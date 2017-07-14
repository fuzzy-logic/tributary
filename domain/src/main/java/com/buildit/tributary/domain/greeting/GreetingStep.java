package com.buildit.tributary.domain.greeting;

import com.codepoetics.fluvius.api.functional.SingleParameterStep;

/**
 * Generates an on-brand greeting
 */
public interface GreetingStep extends SingleParameterStep<Addressee, Greeting> {
  Greeting apply(Addressee addressee);
}
