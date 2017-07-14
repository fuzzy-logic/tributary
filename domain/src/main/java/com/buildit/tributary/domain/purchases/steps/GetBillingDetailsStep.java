package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.BillingDetails;
import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.OperationName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.functional.Returning;
import com.codepoetics.fluvius.api.functional.SingleParameterStep;

public interface GetBillingDetailsStep extends Returning<BillingDetails> {

  @StepMethod
  BillingDetails getBillingDetails(@KeyName("customerId") String customerId);

}
