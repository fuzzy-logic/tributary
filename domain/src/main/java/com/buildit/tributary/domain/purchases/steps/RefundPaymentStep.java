package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.Status;
import com.buildit.tributary.domain.purchases.PaymentReference;
import com.codepoetics.fluvius.api.functional.F1;

public interface RefundPaymentStep extends F1<PaymentReference, Status> {
}
