package it.polito.ai.lhmf.android.api.impl;

import java.util.ArrayList;
import java.util.List;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.OrderOperations;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.OrderProduct;
import it.polito.ai.lhmf.model.Product;
import it.polito.ai.lhmf.model.Purchase;
import it.polito.ai.lhmf.model.Supplier;

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
	public Product[] getProducts(Integer idOrder) {
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

	@Override
	public Float[] getOrdersProgresses(Integer[] orderIds) {
		if(orderIds.length > 0){
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < orderIds.length; i++){
				sb.append(orderIds[i].intValue());
				if(i != orderIds.length - 1)
					sb.append(',');
			}
			try {
				Float[] res = template.getForObject(Gas.baseApiUrl + "getordersprogresses?orderIds={oIds}", Float[].class, sb.toString());
				return res;
			} catch(RestClientException e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	@Override
	public Float getOrderProgress(Integer idOrder) {
		try {
			Float res = template.getForObject(Gas.baseApiUrl + "getorderprogress?idOrder={idOrder}", Float.class, idOrder);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Supplier[] getMySuppliers() {
		try{
			Supplier[] res = template.getForObject(Gas.baseApiUrl + "getmysuppliers", Supplier[].class);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer newOrder(Integer idSupplier, List<Integer> productIds, String orderName, Long dateClose) {
		if(idSupplier != null && productIds != null && productIds.size() > 0 && orderName != null && dateClose != null){
			StringBuilder idsBuilder = new StringBuilder();;
			for(int i = 0; i < productIds.size(); i++){
				idsBuilder.append(productIds.get(i));
				
				if(i != productIds.size() - 1)
					idsBuilder.append(',');
			}
			
			MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
			value.add("idSupplier", idSupplier.toString());
			value.add("dataCloseTime", dateClose.toString());
			value.add("idString", idsBuilder.toString());
			value.add("orderName", orderName);
			
			try {
				Integer res = template.postForObject(Gas.baseApiUrl + "setneworder", value, Integer.class);
				return res;
			} catch (RestClientException e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public Order[] getRespActiveOrders() {
		try {
			Order[] res = template.getForObject(Gas.baseApiUrl + "getactiveorderresp", Order[].class);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Order[] getRespCompletedOrders() {
		try {
			Order[] res = template.getForObject(Gas.baseApiUrl + "getcompletedordersresp", Order[].class);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Order[] getRespShippedOrders() {
		try {
			Order[] res = template.getForObject(Gas.baseApiUrl + "getdeliveredorderresp", Order[].class);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public OrderProduct[] getOrderProducts(Integer orderId) {
		try {
			OrderProduct[] res = template.getForObject(Gas.baseApiUrl + "getorderorderproducts?idOrder={id}", OrderProduct[].class, orderId);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer setDeliveryDate(Integer idOrder, Long dateDelivery) {
		if(dateDelivery != null && idOrder != null){
			try {
				MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
				value.add("idOrder", idOrder.toString());
				value.add("dateDelivery", dateDelivery.toString());
				
				Integer res = template.postForObject(Gas.baseApiUrl + "setdeliverydate", value, Integer.class);
				return res;
				
			} catch(RestClientException e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public Integer setPurchaseShipped(Integer idPurchase) {
		if(idPurchase != null){
			try {
				MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
				value.add("idPurchase", idPurchase.toString());
				
				Integer res = template.postForObject(Gas.baseApiUrl + "setship", value, Integer.class);
				return res;
			} catch (RestClientException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public Purchase[] getOrderCompletedPurchases(Integer idOrder) {
		if(idOrder != null){
			try {
				Purchase[] ret = template.getForObject(Gas.baseApiUrl + "getpurchasefromorder?idOrder={id}", Purchase[].class, idOrder);
				if(ret != null){
					for(Purchase p : ret){
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
						}
						else
							return null;
					}
				}
				return ret;
			} catch(RestClientException e){
				e.printStackTrace();
				return null;
			}
		}
		else
			return null;
	}

	@Override
	public Order getOrder(Integer orderId) {
		try {
			Order order = template.getForObject(Gas.baseApiUrl + "getorder?idOrder={id}", Order.class, orderId);
			return order;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Order[] getSupplierActiveOrders() {
		try {
			Order[] res = template.getForObject(Gas.baseApiUrl + "getactiveordersupplier", Order[].class);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Order[] getSupplierCompletedOrders() {
		try {
			Order[] res = template.getForObject(Gas.baseApiUrl + "getcompletedordersupplier", Order[].class);
			if(res != null){
				for(Order o : res){
					Float orderCost = template.getForObject(Gas.baseApiUrl + "getcompletedordercost?idOrder={id}", Float.class, o.getIdOrder());
					if(orderCost != null){
						o.setCost(orderCost);
					}
					else
						return null;
				}
				return res;
			}
			else
				return null;
			
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public OrderProduct[] getOrderProductsSupplier(Integer idOrder) {
		OrderProduct[] tmp = getOrderProducts(idOrder);
		if(tmp != null && tmp.length > 0){
			List<OrderProduct> ret = new ArrayList<OrderProduct>();
			for(OrderProduct op : tmp)
				if(op.isFailed() == false)
					ret.add(op);
			
			return ret.toArray(new OrderProduct[0]);
		}
		else
			return null;
	}

}
