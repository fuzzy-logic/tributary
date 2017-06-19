package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.*;
import com.codepoetics.fluvius.api.functional.F3;

public interface PlaceOrderStep extends F3<CustomerId, ProductBasket, PaymentReference, OrderReference> {
}
