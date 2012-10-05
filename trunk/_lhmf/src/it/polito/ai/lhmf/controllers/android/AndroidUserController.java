package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.model.MemberInterface;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AndroidUserController {
	@Autowired
	private MemberInterface memberInterface;
	
	@Autowired
	private TokenStore tokenStore;
	
	//FIXME il vecchio metodo ritorna 0 (utente normale) se l'utente non esiste!
	@RequestMapping("/androidApi/member/role")
	public @ResponseBody
	Integer getMemberRole(Principal principal)
	{
		return memberInterface.getMemberTypeFromMember(principal.getName());
	}
	
	@RequestMapping("/androidApi/logout")
	public void logout(OAuth2Authentication auth){
		OAuth2AccessToken token = tokenStore.getAccessToken(auth);
		tokenStore.removeAccessToken(token.getValue());
	}
}
