package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.CustomerId;
import com.buildit.tributary.domain.purchases.PaymentAmount;
import com.buildit.tributary.domain.purchases.ProductBasket;

import com.codepoetics.fluvius.api.functional.F2;

public interface ReserveStockStep extends F2<ProductBasket, CustomerId, PaymentAmount> {
}
