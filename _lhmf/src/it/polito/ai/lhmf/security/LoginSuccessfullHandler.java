package it.polito.ai.lhmf.security;

import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class LoginSuccessfullHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	private MemberInterface mInt;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		boolean isAdmin = false, isResp = false, isNormal = false, isSupplier = false;
		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		for(GrantedAuthority a : roles){
			if(a.getAuthority().equals(MyUserDetailsService.UserRoles.ADMIN))
				isAdmin = true;
			else if(a.getAuthority().equals(MyUserDetailsService.UserRoles.RESP))
				isResp = true;
			else if(a.getAuthority().equals(MyUserDetailsService.UserRoles.NORMAL))
				isNormal = true;
			else if(a.getAuthority().equals(MyUserDetailsService.UserRoles.SUPPLIER))
				isSupplier = true;
		}
		HttpSession session = request.getSession();
		
		if(isAdmin)
			session.setAttribute("member_type", MemberTypes.USER_ADMIN);
		else if(isSupplier)
			session.setAttribute("member_type", MemberTypes.USER_SUPPLIER);
		else if(isResp)
			session.setAttribute("member_type", MemberTypes.USER_RESP);
		else if(isNormal)
			session.setAttribute("member_type", MemberTypes.USER_NORMAL);
		else
			session.setAttribute("member_type", -1);
		
		String userName = authentication.getName();
		session.setAttribute("username", userName);
		Member member = mInt.getMember(userName);
		if(member != null)
			session.setAttribute("user", member.getName());
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	public void setMemberInterface(MemberInterface mInt){
		this.mInt = mInt;
	}
}
