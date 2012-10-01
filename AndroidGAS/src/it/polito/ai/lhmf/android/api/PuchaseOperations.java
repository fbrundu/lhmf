package it.polito.ai.lhmf.android.api;

import it.polito.ai.lhmf.model.Purchase;

import java.util.List;

public interface PuchaseOperations {

	Integer newPurchase(Integer idOrder, List<Integer> ids,
			List<Integer> amounts);

	Purchase[] getActivePurchases();
	
	Float getPurchaseCost(Integer idPurchase);

}
