package it.polito.ai.lhmf.android.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.NotifyOperations;
import it.polito.ai.lhmf.model.Notify;

public class NotifyTemplate implements NotifyOperations {
	private RestTemplate template;

	public NotifyTemplate(RestTemplate restTemplate) {
		template = restTemplate;
	}
	
	@Override
	public List<Notify> getNewNotifies() {
		try {
			Notify[] result = template.getForObject(Gas.baseApiUrl + "newnotifies", Notify[].class);
			if(result == null)
				return null;
			
			List<Notify> ret = new ArrayList<Notify>();
			for(Notify n : result)
				ret.add(n);
			return ret;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}
}
