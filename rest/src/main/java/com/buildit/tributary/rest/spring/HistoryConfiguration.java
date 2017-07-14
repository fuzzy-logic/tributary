package com.buildit.tributary.rest.spring;

import com.codepoetics.fluvius.api.history.FlowEventRepository;
import com.codepoetics.fluvius.history.FlowEventRepositories;
import com.codepoetics.fluvius.json.history.JsonEventDataSerialiser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HistoryConfiguration {

  @Bean
  public FlowEventRepository<JsonNode> flowHistoryRepository(ObjectMapper mapper) {
    return FlowEventRepositories.createInMemory(JsonEventDataSerialiser.using(mapper));
  }
}
