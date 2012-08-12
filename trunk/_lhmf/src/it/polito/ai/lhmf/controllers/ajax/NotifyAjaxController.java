package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.orm.Notify;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NotifyAjaxController
{
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.SUPPLIER
			+ ", " + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getmynotifies", method = RequestMethod.GET)
	public @ResponseBody
	List<Notify> getMyNotifies(
			HttpServletRequest request)
	{
		return null;
	}
}
