package com.buildit.tributary.rest.spring;

import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.history.FlowEventRepository;
import com.codepoetics.fluvius.api.logging.FlowLogger;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.compilation.Compilers;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class CompilerConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger("Flow logging");

  @Bean
  public FlowCompiler flowCompiler(FlowEventRepository<JsonNode> repository) {
    return Compilers.builder()
        .loggingTo(FLOW_LOGGER)
        .mutationChecking()
        .tracingWith(repository)
        .build();
  }

  private static final FlowLogger FLOW_LOGGER = new FlowLogger() {

    @Override
    public void logOperationStarted(UUID uuid, String s, Scratchpad scratchpad) {
      LOGGER.info("Started flow {} step {} with scratchpad {}", uuid, s, scratchpad);
    }

    @Override
    public void logOperationCompleted(UUID uuid, String s, Key<?> key, Object o) {
      LOGGER.info("Completed flow {} step {}, writing result {} to {}", uuid, s, o, key);
    }

    @Override
    public void logOperationException(UUID uuid, String s, Throwable throwable) {
      LOGGER.warn("Failed flow {} step {}: {}", uuid, s, throwable);
    }

    @Override
    public void logConditionStarted(UUID uuid, String s, Scratchpad scratchpad) {

    }

    @Override
    public void logConditionCompleted(UUID uuid, String s, boolean b) {

    }

    @Override
    public void logConditionException(UUID uuid, String s, Throwable throwable) {

    }
  };
}
