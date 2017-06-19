package com.buildit.tributary.rest.rest;

import com.buildit.tributary.application.greeting.services.api.GreetingService;
import com.buildit.tributary.rest.protocol.GreetingRequest;
import com.buildit.tributary.rest.protocol.ImmutableGreetingResponse;
import com.buildit.tributary.rest.protocol.GreetingResponse;
import com.codepoetics.fluvius.api.FlowResultCallback;
import com.codepoetics.fluvius.api.history.FlowHistoryRepository;
import com.codepoetics.fluvius.json.history.FlowHistoryView;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
public class GreetingController {

  private final GreetingService greetingService;
  private final FlowHistoryRepository<JsonNode> historyRepository;

  @Autowired
  public GreetingController(GreetingService greetingService, FlowHistoryRepository<JsonNode> historyRepository) {
    this.greetingService = greetingService;
    this.historyRepository = historyRepository;
  }

  @PostMapping(path="/greeting", consumes = "application/json", produces = "application/json")
  public @ResponseBody GreetingResponse greet(@RequestBody GreetingRequest greetingRequest) throws Exception {
    return ImmutableGreetingResponse.builder()
        .greeting(greetingService.greet(
            greetingRequest.title(),
            greetingRequest.firstName(),
            greetingRequest.secondName()
        ))
        .build();
  }

  @PostMapping(path="/greeting/traced", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Void> greetTraced(@RequestBody GreetingRequest greetingRequest) {
    UUID flowId = UUID.randomUUID();

    Runnable runnable = greetingService.greetAsync(
        flowId,
        greetingRequest.title(),
        greetingRequest.firstName(),
        greetingRequest.secondName(),
        new FlowResultCallback<String>() {
          @Override
          public void onSuccess(UUID uuid, String s) {

          }

          @Override
          public void onFailure(UUID uuid, Throwable throwable) {
            throwable.printStackTrace();
          }
        }
    );

    new Thread(runnable).start();
    return ResponseEntity.created(URI.create("/tracing/" + flowId)).build();
  }

  @GetMapping(path="tracing/{id}", produces = "application/json")
  public @ResponseBody FlowHistoryView getHistory(@PathVariable("id") UUID flowId) {
    return FlowHistoryView.from(historyRepository.getFlowHistory(flowId));
  }
}
