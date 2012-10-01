package it.polito.ai.lhmf.android.api;

import java.util.List;

public interface PuchaseOperations {

	Integer newPurchase(Integer idOrder, List<Integer> ids,
			List<Integer> amounts);

}
