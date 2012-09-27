package it.polito.ai.lhmf.android.api;

import it.polito.ai.lhmf.model.Product;

public interface OrderOperations {
	Product[] getOrderProducts(Integer orderId);

	Integer[] getBoughtAmounts(Integer orderId, Integer[] productIds);
}
