package it.polito.ai.lhmf.android.api.impl;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.OrderOperations;
import it.polito.ai.lhmf.model.Product;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
		try {
			MultiValueMap<String, Object> value = new LinkedMultiValueMap<String, Object>();
			value.add("idOrder", orderId);
			value.add("productIds", productIds);
			Integer[] res = template.postForObject(Gas.baseApiUrl + "getboughtamounts", value, Integer[].class);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

}
