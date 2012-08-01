package it.polito.ai.lhmf.android.api.impl;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.UserOperations;

import java.net.URI;

import org.springframework.web.client.RestTemplate;

public class UserTemplate implements UserOperations{
	private RestTemplate template; 
	
	
	public UserTemplate(RestTemplate restTemplate) {
		template = restTemplate;
	}


	@Override
	public Integer getMemberType() {
		return template.getForObject(URI.create(Gas.baseApiUrl + "member/role"), Integer.class);
	}

}
