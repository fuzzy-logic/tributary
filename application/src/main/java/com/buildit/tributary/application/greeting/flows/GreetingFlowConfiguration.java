package com.buildit.tributary.application.greeting.flows;

import com.buildit.tributary.domain.greeting.Addressee;
import com.buildit.tributary.domain.greeting.Greeting;
import com.buildit.tributary.domain.greeting.GreetingStep;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.flows.Flows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.buildit.tributary.application.greeting.flows.GreetingFlowKeys.addressee;
import static com.buildit.tributary.application.greeting.flows.GreetingFlowKeys.greeting;
import static com.buildit.tributary.application.greeting.flows.GreetingFlowKeys.message;

@Configuration
public class GreetingFlowConfiguration {

  private final GreetingStep greetingStep;

  @Autowired
  public GreetingFlowConfiguration(GreetingStep greetingStep) {
    this.greetingStep = greetingStep;
  }

  @Bean
  public Flow<String> greetingMessageFlow(
      @Qualifier("greetingFlow") Flow<Greeting> greetingFlow,
      @Qualifier("extractMessageFlow") Flow<String> extractMessageFlow) {
    return greetingFlow.then(extractMessageFlow);
  }

  @Bean
  public Flow<Greeting> greetingFlow() {
    return Flows.obtaining(greeting).from(addressee).using(greetingStep);
  }

  @Bean
  public Flow<String> extractMessageFlow() {
    return Flows.obtaining(message).from(greeting).using(new F1<Greeting, String>() {
      @Override
      public String apply(Greeting input) {
        return input.message();
      }
    });
  }
}
