package it.polito.ai.lhmf.android.api;

import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.Product;
import it.polito.ai.lhmf.model.Supplier;

import java.util.List;

public interface OrderOperations {
	Product[] getOrderProducts(Integer orderId);

	Integer[] getBoughtAmounts(Integer orderId, Integer[] productIds);
	
	Order[] getAvailableOrdersForPurchase();
	
	Float[] getOrdersProgresses(Integer[] orderIds);
	
	Float getOrderProgress(Integer idOrder);
	
	Supplier[] getMySuppliers();
	
	Integer newOrder(Integer idSupplier, List<Integer> ids, String orderName, Long dateClose);

	Order[] getRespActiveOrders();
}
