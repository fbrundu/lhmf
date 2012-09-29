package it.polito.ai.lhmf.android.api.impl;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.OrderOperations;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.Product;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class OrderTemplate implements OrderOperations {
	private RestTemplate template;

	public OrderTemplate(RestTemplate restTemplate) {
		template = restTemplate;
	}
	
	@Override
	public Product[] getOrderProducts(Integer idOrder) {
		try {
			Product[] res = template.getForObject(Gas.baseApiUrl + "getorderproducts?idOrder={id}", Product[].class, idOrder);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer[] getBoughtAmounts(Integer orderId, Integer[] productIds) {
		if(productIds.length > 0){
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < productIds.length; i++){
				sb.append(productIds[i].intValue());
				if(i != productIds.length - 1)
					sb.append(',');
			}
			try {
				Integer[] res = template.getForObject(Gas.baseApiUrl + "getboughtamounts?idOrder={id}&productIds={pIds}", Integer[].class, orderId, sb.toString());
				return res;
			} catch(RestClientException e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public Order[] getAvailableOrdersForPurchase() {
		try {
			Order[] res = template.getForObject(Gas.baseApiUrl + "getavailableordersforpurchase", Order[].class);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

}
