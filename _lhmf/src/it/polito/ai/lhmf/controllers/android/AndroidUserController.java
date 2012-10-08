package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AndroidUserController {
	@Autowired
	private MemberInterface memberInterface;
	
	@Autowired
	private TokenStore tokenStore;
	
	//FIXME il vecchio metodo ritorna 0 (utente normale) se l'utente non esiste!
	@RequestMapping(value = "/androidApi/member/role", method = RequestMethod.GET)
	public @ResponseBody
	Integer getMemberRole(Principal principal)
	{
		return memberInterface.getMemberTypeFromMember(principal.getName());
	}
	
	@RequestMapping(value = "/androidApi/logout", method = RequestMethod.POST)
	public void logout(OAuth2Authentication auth){
		OAuth2AccessToken token = tokenStore.getAccessToken(auth);
		tokenStore.removeAccessToken(token.getValue());
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/androidApi/getmember", method = RequestMethod.GET)
	public @ResponseBody
	Member getMemberRole(@RequestParam("idMember") Integer idMember)
	{
		return memberInterface.getMember(idMember);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/androidApi/getmemberstoactivate", method = RequestMethod.GET)
	public @ResponseBody
	List<Member> getMembersToActivate()
	{
		return memberInterface.getMembersToActivate();
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/androidApi/activatemember", method = RequestMethod.POST)
	public @ResponseBody Integer memberActivation(HttpServletRequest request,
			@RequestParam(value = "idMember", required = true) Integer idMember)
	{
		try
		{
			return memberInterface.activateMember(idMember);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
}
