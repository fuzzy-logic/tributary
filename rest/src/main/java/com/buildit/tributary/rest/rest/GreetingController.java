package com.buildit.tributary.rest.rest;

import com.buildit.tributary.application.greeting.services.api.GreetingService;
import com.buildit.tributary.rest.protocol.GreetingRequest;
import com.buildit.tributary.rest.protocol.ImmutableGreetingResponse;
import com.buildit.tributary.rest.protocol.GreetingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class GreetingController {

  private final GreetingService greetingService;

  @Autowired
  public GreetingController(GreetingService greetingService) {
    this.greetingService = greetingService;
  }

  @PostMapping(path="/greeting", consumes = "application/json", produces = "application/json")
  public @ResponseBody GreetingResponse greet(@RequestBody GreetingRequest greetingRequest) {
    return ImmutableGreetingResponse.builder()
        .greeting(greetingService.greet(
            greetingRequest.title(),
            greetingRequest.firstName(),
            greetingRequest.secondName()
        ))
        .build();
  }
}
