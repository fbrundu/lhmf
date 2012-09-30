package it.polito.ai.lhmf.android.api;

import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.Product;

public interface OrderOperations {
	Product[] getOrderProducts(Integer orderId);

	Integer[] getBoughtAmounts(Integer orderId, Integer[] productIds);
	
	Order[] getAvailableOrdersForPurchase();
	
	Float[] getOrdersProgresses(Integer[] orderIds);
	
	Float getOrderProgress(Integer idOrder);
}