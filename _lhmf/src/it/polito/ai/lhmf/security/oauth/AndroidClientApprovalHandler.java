package it.polito.ai.lhmf.security.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.TokenServicesUserApprovalHandler;

public class AndroidClientApprovalHandler extends TokenServicesUserApprovalHandler{
	private String autoApproveClient;
	
	public void setAutoApproveClient(String autoApproveClient) {
		this.autoApproveClient = autoApproveClient;
	}
	
	@Override
	public boolean isApproved(AuthorizationRequest authorizationRequest,
			Authentication userAuthentication) {
		return super.isApproved(authorizationRequest, userAuthentication)
				|| (authorizationRequest.getResponseTypes().contains("token") && 
				   autoApproveClient.equals(authorizationRequest.getClientId()));
	}

}
