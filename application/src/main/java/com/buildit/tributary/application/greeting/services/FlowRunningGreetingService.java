package com.buildit.tributary.application.greeting.services;

import com.buildit.tributary.application.greeting.services.api.GreetingService;
import com.buildit.tributary.domain.greeting.ImmutableAddressee;
import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.flows.Flows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.buildit.tributary.application.greeting.flows.GreetingFlowKeys.addressee;

@Service
public class FlowRunningGreetingService implements GreetingService {

  private final Flow<String> greetingMessageFlow;
  private final FlowVisitor<Action> visitor;

  @Autowired
  public FlowRunningGreetingService(@Qualifier("greetingMessageFlow") Flow<String> greetingMessageFlow, FlowVisitor<Action> visitor) {
    this.greetingMessageFlow = greetingMessageFlow;
    this.visitor = visitor;
  }

  @Override
  public String greet(String title, String forename, String surname) {
    return Flows.run(
        greetingMessageFlow,
          visitor,
          addressee.of(
              ImmutableAddressee.builder()
                .title(title)
                .firstName(forename)
                .lastName(surname)
                .build()));
  }
}
