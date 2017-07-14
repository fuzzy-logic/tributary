package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.*;
import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.OperationName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.functional.DoubleParameterStep;
import com.codepoetics.fluvius.api.functional.Returning;

public interface MakePaymentStep extends Returning<PaymentReference> {

  @StepMethod
  PaymentReference makePayment(
      BillingDetails billingDetails,
      PaymentAmount paymentAmount);

}
