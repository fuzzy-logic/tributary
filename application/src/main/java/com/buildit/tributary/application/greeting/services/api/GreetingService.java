package com.buildit.tributary.application.greeting.services.api;

import com.codepoetics.fluvius.api.FlowResultCallback;

import java.util.UUID;

public interface GreetingService {
  String greet(String title, String forename, String surname);

  Runnable greetAsync(UUID flowId, String title, String forename, String surname, FlowResultCallback<String> callback);
}
