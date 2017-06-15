package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.BillingDetails;
import com.buildit.tributary.domain.purchases.CustomerId;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.services.ServiceCallResult;

public interface GetBillingDetailsStep extends F1<CustomerId, ServiceCallResult<BillingDetails>> {
}
