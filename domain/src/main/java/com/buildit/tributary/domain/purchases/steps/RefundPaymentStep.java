package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.Status;
import com.buildit.tributary.domain.purchases.PaymentReference;
import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.OperationName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.functional.Returning;

@OperationName("Refund payment")
public interface RefundPaymentStep extends Returning<Status> {

  @StepMethod
  Status refundPayment(PaymentReference paymentReference);

}
