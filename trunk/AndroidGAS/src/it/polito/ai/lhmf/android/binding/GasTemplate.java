package it.polito.ai.lhmf.android.binding;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;

public class GasTemplate extends AbstractOAuth2ApiBinding implements Gas{
	public GasTemplate(String accessToken){
		super(accessToken);
	}
}
