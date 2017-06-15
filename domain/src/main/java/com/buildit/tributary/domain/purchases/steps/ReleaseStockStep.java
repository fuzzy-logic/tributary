package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.purchases.ProductBasket;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.services.ServiceCallResult;

public interface ReleaseStockStep extends F1<ProductBasket, ServiceCallResult<Void>> {
}
