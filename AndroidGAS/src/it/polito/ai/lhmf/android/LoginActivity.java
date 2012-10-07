package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.service.GasNetworkService;

import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private GasConnectionHolder holder;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        String redirectUri = "http://gasproject.net:8080/_lhmf/android/loginSuccess";
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri(redirectUri);
        
        holder = new GasConnectionHolder(getApplicationContext());
        
        OAuth2Operations oauth = holder.getOAuthOperations();
        String authorizeUrl = oauth.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, params);
        
        WebView wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient() {
        	
        	@Override
        	public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		setTitle(url);
        		
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
						holder.createConnection(accessToken);
		
						if(!isServiceRunning()){
							Intent intent = new Intent(getApplicationContext(), GasNetworkService.class);
							startService(intent);
						}
						
						NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						nm.cancel(GasNetworkService.LOGIN_REQUIRED_NOTIFICATION);
						
						Intent main = new Intent(getApplicationContext(), MainActivity.class);
						startActivity(main);
						finish();
	        		} catch (Exception e) {
	        		// don't do anything if the parameters are not what is expected
	        		}
        		}
        		else if (uri.getQueryParameter("error") != null) {
	        		CharSequence errorReason = uri.getQueryParameter("error_description").replace("+", " ");
	        		Toast.makeText(getApplicationContext(), errorReason, Toast.LENGTH_LONG).show();
        		}
        	}
        });
        
        wv.setWebChromeClient(new WebChromeClient(){
        	@Override
        	public void onProgressChanged(WebView view, int newProgress) {
        		setProgress(newProgress * 100);
        	}
        });
        
        setContentView(wv);
        
        wv.loadUrl(authorizeUrl); 
    }
    
    private boolean isServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (GasNetworkService.class.getName().equals(serviceInfo.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}