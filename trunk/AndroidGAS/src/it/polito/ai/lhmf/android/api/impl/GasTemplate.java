package it.polito.ai.lhmf.android.api.impl;

import java.net.URI;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.LogOperations;
import it.polito.ai.lhmf.android.api.MessageOperations;
import it.polito.ai.lhmf.android.api.NotifyOperations;
import it.polito.ai.lhmf.android.api.OrderOperations;
import it.polito.ai.lhmf.android.api.ProductOperations;
import it.polito.ai.lhmf.android.api.PuchaseOperations;
import it.polito.ai.lhmf.android.api.UserOperations;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.web.client.RestTemplate;

public class GasTemplate extends AbstractOAuth2ApiBinding implements Gas{
	private LogOperations logOperations;
	private MessageOperations messageOperations;
	private NotifyOperations notifyOperations;
	private OrderOperations orderOperations;
	private ProductOperations productOperations;
	private PuchaseOperations puchaseOperations;
	private UserOperations userOperations;
	
	public GasTemplate(String accessToken){
		super(accessToken);
		
		//TODO construct templates
		userOperations = new UserTemplate(getRestTemplate());
		
	}

	@Override
	public MessageOperations messageOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NotifyOperations notifyOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PuchaseOperations purchaseOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductOperations productOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderOperations orderOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserOperations userOperations() {
		return userOperations;
	}

	@Override
	public LogOperations logOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout() {
		getRestTemplate().postForObject(URI.create(baseApiUrl + "logout"), null, Void.class);
	}
}
