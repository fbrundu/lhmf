package it.polito.ai.lhmf.android.api.impl;

import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.PuchaseOperations;
import it.polito.ai.lhmf.model.PurchaseProduct;
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

	@Override
	public PurchaseProduct[] getPurchaseProducts(Integer idPurchase) { 
		try{
			PurchaseProduct[] ret = template.getForObject(Gas.baseApiUrl + "getpurchaseproducts?idPurchase={id}", PurchaseProduct[].class, idPurchase);
			return ret;
			
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer newPurchaseProduct(Integer idPurchase, Integer idProduct,
			Integer amount) {
		MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
		value.add("idPurchase", idPurchase.toString());
		value.add("idProduct", idProduct.toString());
		value.add("amount", amount.toString());
		
		try{
			Integer ret = template.postForObject(Gas.baseApiUrl + "newpurchaseproduct", value, Integer.class);
			return ret;
			
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer updatePurchaseProduct(Integer idPurchase, Integer idProduct,
			Integer amount) {
		MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
		value.add("idPurchase", idPurchase.toString());
		value.add("idProduct", idProduct.toString());
		value.add("amount", amount.toString());
		
		try{
			Integer ret = template.postForObject(Gas.baseApiUrl + "updatepurchaseproduct", value, Integer.class);
			return ret;
			
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer removePurchaseProduct(Integer idPurchase, Integer idProduct) {
		MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
		value.add("idPurchase", idPurchase.toString());
		value.add("idProduct", idProduct.toString());
		
		try{
			Integer ret = template.postForObject(Gas.baseApiUrl + "delpurchaseproduct", value, Integer.class);
			return ret;
			
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Purchase getMyPurchase(Integer idOrder) {
		try {
			Purchase p = template.getForObject(Gas.baseApiUrl + "getmypurchase?idOrder={id}", Purchase.class, idOrder);
			
			if(p != null){
				Boolean failed = template.getForObject(Gas.baseApiUrl + "ispurchasefailed?idPurchase={id}", Boolean.class, p.getIdPurchase());
				if(failed != null){
					p.setFailed(failed);
					if(failed == false){
						Float cost = template.getForObject(Gas.baseApiUrl + "getcompletedpurchasecost?idPurchase={id}", Float.class, p.getIdPurchase());
						if(cost != null){
							p.setTotCost(cost);
						}
						else
							return null;
					}
					return p;
				}
				else
					return null;
			}
			else
				return null;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Purchase[] getPurchasesWithDeliveryDate() {
		try{
			Purchase[] ret = template.getForObject(Gas.baseApiUrl + "getpurchaseswithdelivery", Purchase[].class);
			
			for(int i = 0; i < ret.length; i++){
				Float cost = template.getForObject(Gas.baseApiUrl + "getcompletedpurchasecost?idPurchase={id}", Float.class, ret[i].getIdPurchase());
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

}
