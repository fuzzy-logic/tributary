package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.BillingDetails;
import com.buildit.tributary.domain.purchases.CustomerId;
import com.codepoetics.fluvius.api.functional.F1;

public interface GetBillingDetailsStep extends F1<CustomerId, BillingDetails> {
}
