package it.polito.ai.lhmf.android.api.util;

import it.polito.ai.lhmf.android.api.GASConnectionFactory;
import it.polito.ai.lhmf.android.api.Gas;

import org.springframework.security.crypto.encrypt.AndroidEncryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class GasConnectionHolder {
	
	private ConnectionRepository repo;

	private GASConnectionFactory factory;
	
	public GasConnectionHolder(Context ctx){
        SQLiteOpenHelper repositoryHelper = new SQLiteConnectionRepositoryHelper(ctx);
        ConnectionFactoryRegistry connectionFactoryRegistry = new ConnectionFactoryRegistry();
        String appId="androidGas";
        factory = new GASConnectionFactory(appId);
        connectionFactoryRegistry.addConnectionFactory(factory);
        TextEncryptor enc = AndroidEncryptors.noOpText();
        
        repo = new SQLiteConnectionRepository(repositoryHelper, connectionFactoryRegistry, enc);
	}

	public OAuth2Operations getOAuthOperations() {
		return factory.getOAuthOperations();
	}

	public boolean createConnection(String accessToken) {
		if(repo.findPrimaryConnection(Gas.class) == null){
			AccessGrant accessGrant = new AccessGrant(accessToken);
			Connection<Gas> connection = factory.createConnection(accessGrant);
			try {
				repo.addConnection(connection);		
			} catch (DuplicateConnectionException e) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public Connection<Gas> getConnection() {
		return repo.findPrimaryConnection(Gas.class);
	}
	
	public void destroy() {
		repo.removeConnections("gas");
	}
}
