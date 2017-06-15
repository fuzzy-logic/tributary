package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.*;
import com.codepoetics.fluvius.api.functional.F3;
import com.codepoetics.fluvius.api.services.ServiceCallResult;

public interface PlaceOrderStep extends F3<CustomerId, ProductBasket, PaymentReference, ServiceCallResult<OrderReference>> {
}
