package it.polito.ai.lhmf.android.binding;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;

public class GasServiceProvider extends AbstractOAuth2ServiceProvider<Gas>{
	public GasServiceProvider(String clientId){
		super(new OAuth2Template(clientId, "",
				"http://192.168.0.2:8080/_lhmf/oauth/authorize",
				"http://192.168.0.2:8080/_lhmf/oauth/token"));
	}

	@Override
	public Gas getApi(String accessToken) {
		return new GasTemplate(accessToken);
	}
}
