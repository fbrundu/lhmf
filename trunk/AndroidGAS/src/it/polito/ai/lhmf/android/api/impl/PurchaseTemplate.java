package it.polito.ai.lhmf.android.api.impl;

import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.PuchaseOperations;
import it.polito.ai.lhmf.model.Purchase;

public class PurchaseTemplate implements PuchaseOperations {
	private RestTemplate template;
	
	public PurchaseTemplate(RestTemplate restTemplate) {
		template = restTemplate;
	}

	@Override
	public Integer newPurchase(Integer idOrder, List<Integer> ids,
			List<Integer> amounts) {
		if(idOrder != null && ids.size() > 0 && ids.size() == amounts.size()){
			StringBuilder idsBuilder = new StringBuilder();;
			StringBuilder amountsBuilder = new StringBuilder();;
			for(int i = 0; i < ids.size(); i++){
				idsBuilder.append(ids.get(i));
				amountsBuilder.append(amounts.get(i));
				
				if(i != ids.size() - 1){
					idsBuilder.append(',');
					amountsBuilder.append(',');
				}
			}
			
			MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
			value.add("idOrder", idOrder.toString());
			value.add("idProducts", idsBuilder.toString());
			value.add("amountProducts", amountsBuilder.toString());
			
			try {
				Integer res = template.postForObject(Gas.baseApiUrl + "setnewpurchase", value, Integer.class);
				return res;
			} catch (RestClientException e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public Purchase[] getActivePurchases() {
		try{
			Purchase[] ret = template.getForObject(Gas.baseApiUrl + "getactivepurchase", Purchase[].class);
			
			for(int i = 0; i < ret.length; i++){
				Float cost = getPurchaseCost(ret[i].getIdPurchase());
				if(cost == null)
					return null;
				ret[i].setTotCost(cost);
			}
			
			return ret;
			
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Float getPurchaseCost(Integer idPurchase) {
		try {
			Float ret = null;
			ret = template.getForObject(Gas.baseApiUrl + "getpurchasecost?idPurchase={id}", Float.class, idPurchase);
			return ret;
		} catch (RestClientException e) {
			e.printStackTrace();
			return null;
		}
	}

}
