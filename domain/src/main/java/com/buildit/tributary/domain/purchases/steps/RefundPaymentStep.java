package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.PaymentReference;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.services.ServiceCallResult;

public interface RefundPaymentStep extends F1<PaymentReference, ServiceCallResult<Void>> {
}
