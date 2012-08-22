package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.NotifyInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Notify;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NotifyAjaxController
{
	@Autowired
	private MemberInterface memberInterface;

	@Autowired
	private NotifyInterface notifyInterface;

	@RequestMapping(value = "/ajax/getmynotifies", method = RequestMethod.GET)
	public @ResponseBody
	List<Notify> getMyNotifies(HttpServletRequest request)
			throws InvalidParametersException
	{
		Member m = memberInterface.getMember((String) request.getSession()
				.getAttribute("username"));
		if (m != null)
			return notifyInterface.getNotifiesByIdMember(m.getIdMember());
		else
			return null;
	}
}
