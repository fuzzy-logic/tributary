package com.buildit.tributary.application.greeting.spring;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.visitors.Visitors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisitorConfiguration {

  @Bean
  public FlowVisitor<Action> getVisitor() {
    return Visitors.mutationChecking(Visitors.logging(Visitors.getDefault()));
  }
}
