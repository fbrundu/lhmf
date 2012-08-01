package it.polito.ai.lhmf.android.api;


import org.springframework.social.connect.support.OAuth2ConnectionFactory;

public class GASConnectionFactory extends OAuth2ConnectionFactory<Gas>{
	public GASConnectionFactory(String clientId){
		super("gas", new GasServiceProvider(clientId), null);
	}
}
