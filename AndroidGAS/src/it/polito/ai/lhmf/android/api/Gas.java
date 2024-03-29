package it.polito.ai.lhmf.android.api;

import org.springframework.social.ApiBinding;

public interface Gas extends ApiBinding{
	public static final String baseUrl = "http://gasproject.net:8080/_lhmf/";
	public static final String baseApiUrl = baseUrl + "androidApi/";
	
	NotifyOperations notifyOperations();
	
	PuchaseOperations purchaseOperations();
	
	ProductOperations productOperations();
	
	OrderOperations orderOperations();
	
	UserOperations userOperations();
	
	void logout();
}
