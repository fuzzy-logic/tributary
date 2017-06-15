package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.*;
import com.codepoetics.fluvius.api.functional.F2;
import com.codepoetics.fluvius.api.services.ServiceCallResult;

public interface MakePaymentStep extends F2<BillingDetails, PaymentAmount, ServiceCallResult<PaymentReference>> {
}
