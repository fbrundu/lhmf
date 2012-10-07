package it.polito.ai.lhmf.android.api;

import it.polito.ai.lhmf.model.PurchaseProduct;
import it.polito.ai.lhmf.model.Purchase;

import java.util.List;

public interface PuchaseOperations {

	Integer newPurchase(Integer idOrder, List<Integer> ids,
			List<Integer> amounts);

	Purchase[] getActivePurchases();
	
	Purchase getMyPurchase(Integer idOrder);
	
	Float getPurchaseCost(Integer idPurchase);
	
	PurchaseProduct[] getPurchaseProducts(Integer idPurchase);

	Integer newPurchaseProduct(Integer idPurchase, Integer idProduct, Integer amount);

	Integer updatePurchaseProduct(Integer idPurchase, Integer idProduct, Integer amount);
	
	Integer removePurchaseProduct(Integer idPurchase, Integer idProduct);

}
