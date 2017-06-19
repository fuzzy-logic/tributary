package com.buildit.tributary.domain.purchases.steps;

import com.buildit.tributary.domain.Status;
import com.buildit.tributary.domain.purchases.ProductBasket;
import com.codepoetics.fluvius.api.functional.F1;

public interface ReleaseStockStep extends F1<ProductBasket, Status> {
}
