package com.buildit.tributary.application.greeting.services;

import com.buildit.tributary.application.greeting.services.api.GreetingService;
import com.buildit.tributary.domain.greeting.Greeting;
import com.buildit.tributary.domain.greeting.ImmutableAddressee;
import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.flows.Flows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static com.buildit.tributary.application.greeting.flows.GreetingFlowKeys.addressee;

@Service
public class FlowRunningGreetingService implements GreetingService {

  private final Flow<String> greetingFlow;
  private final FlowVisitor<Action> visitor;

  @Autowired
  public FlowRunningGreetingService(Flow<String> greetingFlow, FlowVisitor<Action> visitor) {
    this.greetingFlow = greetingFlow;
    this.visitor = visitor;
  }

  @Override
  public String greet(String title, String forename, String surname) {
    return Flows.run(
          greetingFlow,
          visitor,
          addressee.of(
              ImmutableAddressee.builder()
                .title(title)
                .firstName(forename)
                .lastName(surname)
                .build()));
  }
}
