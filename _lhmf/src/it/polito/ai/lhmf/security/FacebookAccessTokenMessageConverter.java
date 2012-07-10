package it.polito.ai.lhmf.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * This message converter handles facebook's successful access token responses in text/plain format.
 * @author Luca Moretto
 *
 */
public class FacebookAccessTokenMessageConverter extends AbstractHttpMessageConverter<OAuth2AccessToken>{

	public FacebookAccessTokenMessageConverter() {
		super(MediaType.TEXT_PLAIN);
	}
	
	@Override
	protected OAuth2AccessToken readInternal(Class<? extends OAuth2AccessToken> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputMessage.getBody()));
		String line = reader.readLine();
		Map<String, String> params = new HashMap<String, String>();
		String[] splitted = line.split("&");
		if(splitted.length == 2){
			for(String param : splitted){
				String[] resplitted = param.split("=");
				if(resplitted.length == 2){
					if(resplitted[0].equals("access_token"))
						params.put(OAuth2AccessToken.ACCESS_TOKEN, resplitted[1]);
					else if(resplitted[0].equals("expires"))
						params.put(OAuth2AccessToken.EXPIRES_IN, resplitted[1]);
					else
						throw new HttpMessageNotReadableException("Facebook response format invalid: " + line);
				}
				else
					throw new HttpMessageNotReadableException("Facebook response format invalid: " + line);
			}
		}
		else{
			throw new HttpMessageNotReadableException("Facebook response format invalid: " + line);
		}
		return OAuth2AccessToken.valueOf(params);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return OAuth2AccessToken.class.equals(clazz);
	}

	@Override
	protected void writeInternal(OAuth2AccessToken arg0, HttpOutputMessage arg1)
			throws IOException, HttpMessageNotWritableException {
		throw new UnsupportedOperationException(
				"This converter is only used for converting from externally aqcuired form data");
	}

}
