package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.binding.GASConnectionFactory;
import it.polito.ai.lhmf.android.binding.Gas;

import org.springframework.security.crypto.encrypt.AndroidEncryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AndroidGASActivity extends Activity {
	//TODO retrieve ConnectionRepository and GASConnectionFactory from custom extended Application
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        Context ctx = getApplicationContext();
        SQLiteOpenHelper repositoryHelper = new SQLiteConnectionRepositoryHelper(ctx);
        ConnectionFactoryRegistry connectionFactoryRegistry = new ConnectionFactoryRegistry();
        String appId="androidGas";
        final GASConnectionFactory factory = new GASConnectionFactory(appId);
        connectionFactoryRegistry.addConnectionFactory(factory);
        TextEncryptor enc = AndroidEncryptors.noOpText();
        
        final ConnectionRepository repo = new SQLiteConnectionRepository(repositoryHelper, connectionFactoryRegistry, enc);
        
        String redirectUri = "http://10.0.2.2:8080/_lhmf/";
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri(redirectUri);
        
        OAuth2Operations oauth = factory.getOAuthOperations();
        String authorizeUrl = oauth.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, params);
        
        WebView wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient() {
        	
        	@Override
        	public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		Uri uri = Uri.parse(url);

        		String uriFragment = uri.getFragment();

        		if (uriFragment != null && uriFragment.startsWith("access_token=")) {
	        		try {
						// split to get the two different parameters
						String[] params = uriFragment.split("&");
		
						// split to get the access token parameter and value
						String[] accessTokenParam = params[0].split("=");
		
						// get the access token value
						String accessToken = accessTokenParam[1];
		
						// create the connection and persist it to the repository
						AccessGrant accessGrant = new AccessGrant(accessToken);
						Connection<Gas> connection = factory.createConnection(accessGrant);
		
						try {
							repo.addConnection(connection);
						} catch (DuplicateConnectionException e) {
						// connection already exists in repository!
						}
	        		} catch (Exception e) {
	        		// don't do anything if the parameters are not what is expected
	        		}
	        		
	        		//Uscire dalla webview
	        		
	        		/* Esempio: ottenere le api dal repo (che sarà nel contesto applicativo) */
	        		Gas api = repo.findPrimaryConnection(Gas.class).getApi();
        		}
        		else if (uri.getQueryParameter("error") != null) {
	        		CharSequence errorReason = uri.getQueryParameter("error_description").replace("+", " ");
	        		Toast.makeText(getApplicationContext(), errorReason, Toast.LENGTH_LONG).show();
	        		//Uscire dalla webview
        		}
        	}
        });
        setContentView(wv);
        
        wv.loadUrl(authorizeUrl); 
    }
}