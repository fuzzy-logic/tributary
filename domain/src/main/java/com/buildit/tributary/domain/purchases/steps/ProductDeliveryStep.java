package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.DeliveryReference;
import com.buildit.tributary.domain.purchases.OrderReference;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.services.ServiceCallResult;

public interface ProductDeliveryStep extends F1<OrderReference, ServiceCallResult<DeliveryReference>> {
}
