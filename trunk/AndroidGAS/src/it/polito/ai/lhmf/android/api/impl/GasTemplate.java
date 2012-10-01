package it.polito.ai.lhmf.android.api.impl;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.LogOperations;
import it.polito.ai.lhmf.android.api.MessageOperations;
import it.polito.ai.lhmf.android.api.NotifyOperations;
import it.polito.ai.lhmf.android.api.OrderOperations;
import it.polito.ai.lhmf.android.api.ProductOperations;
import it.polito.ai.lhmf.android.api.PuchaseOperations;
import it.polito.ai.lhmf.android.api.UserOperations;

import java.net.URI;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.web.client.RestClientException;

public class GasTemplate extends AbstractOAuth2ApiBinding implements Gas{
	private LogOperations logOperations;
	private MessageOperations messageOperations;
	private NotifyOperations notifyOperations;
	private OrderOperations orderOperations;
	private ProductOperations productOperations;
	private PuchaseOperations purchaseOperations;
	private UserOperations userOperations;
	
	public GasTemplate(String accessToken){
		super(accessToken);
		
		//TODO construct templates
		userOperations = new UserTemplate(getRestTemplate());
		productOperations = new ProductsTemplate(getRestTemplate());
		orderOperations = new OrderTemplate(getRestTemplate());
		purchaseOperations = new PurchaseTemplate(getRestTemplate());
		
	}

	@Override
	public MessageOperations messageOperations() {
		return messageOperations;
	}

	@Override
	public NotifyOperations notifyOperations() {
		return notifyOperations;
	}

	@Override
	public PuchaseOperations purchaseOperations() {
		return purchaseOperations;
	}

	@Override
	public ProductOperations productOperations() {
		return productOperations;
	}

	@Override
	public OrderOperations orderOperations() {
		return orderOperations;
	}

	@Override
	public UserOperations userOperations() {
		return userOperations;
	}

	@Override
	public LogOperations logOperations() {
		return logOperations;
	}

	@Override
	public void logout() {
		try {
			getRestTemplate().postForObject(URI.create(baseApiUrl + "logout"), null, Void.class);
		} catch(RestClientException e){
			throw e;
		}
	}
}
