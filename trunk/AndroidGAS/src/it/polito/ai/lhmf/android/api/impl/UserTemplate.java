package it.polito.ai.lhmf.android.api.impl;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.UserOperations;
import it.polito.ai.lhmf.model.Member;

import java.net.URI;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class UserTemplate implements UserOperations{
	private RestTemplate template; 
	
	
	public UserTemplate(RestTemplate restTemplate) {
		template = restTemplate;
	}


	@Override
	public Integer getMemberType() {
		try {
			return template.getForObject(URI.create(Gas.baseApiUrl + "member/role"), Integer.class);
		} catch (RestClientException e){
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public Member getMember(Integer memberId) {
		try{
			return template.getForObject(Gas.baseApiUrl + "getmember?idMember={id}", Member.class, memberId);
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public Member[] getMembersToActivate() {
		try {
			Member[] ret = null;
			ret = template.getForObject(Gas.baseApiUrl + "getmemberstoactivate", Member[].class);
			return ret;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Integer activateMember(Integer idMember) {
		if(idMember == null)
			return null;
		MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
		value.add("idMember", idMember.toString());
		try {
			return template.postForObject(Gas.baseApiUrl + "activatemember", value, Integer.class);
		} catch (RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

}
