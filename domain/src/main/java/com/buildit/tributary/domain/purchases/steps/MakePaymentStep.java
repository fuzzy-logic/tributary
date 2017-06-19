package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.*;
import com.codepoetics.fluvius.api.functional.F2;

public interface MakePaymentStep extends F2<BillingDetails, PaymentAmount, PaymentReference> {
}
