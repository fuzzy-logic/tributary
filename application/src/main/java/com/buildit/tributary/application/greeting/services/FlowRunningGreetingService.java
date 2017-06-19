package com.buildit.tributary.application.greeting.services;

import com.buildit.tributary.application.greeting.services.api.GreetingService;
import com.buildit.tributary.domain.greeting.ImmutableAddressee;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowResultCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.buildit.tributary.application.greeting.flows.GreetingFlowKeys.addressee;

@Service
public class FlowRunningGreetingService implements GreetingService {

  private final FlowExecution<String> greetingMessageExecution;

  @Autowired
  public FlowRunningGreetingService(@Qualifier("greetingMessageExecution") FlowExecution<String> greetingMessageExecution) {
    this.greetingMessageExecution = greetingMessageExecution;
  }

  @Override
  public String greet(String title, String forename, String surname) throws Exception {
    return greetingMessageExecution.run(
          addressee.of(
              ImmutableAddressee.builder()
                .title(title)
                .firstName(forename)
                .lastName(surname)
                .build()));
  }

  @Override
  public Runnable greetAsync(UUID flowId, String title, String forename, String surname, FlowResultCallback<String> callback) {
    return greetingMessageExecution.asAsync(flowId, callback, addressee.of(
        ImmutableAddressee.builder()
            .title(title)
            .firstName(forename)
            .lastName(surname)
            .build()));
  }
}
