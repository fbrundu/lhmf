package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.model.NotifyInterface;
import it.polito.ai.lhmf.orm.Notify;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AndoridNotifyController {
	
	@Autowired
	private NotifyInterface notifyInterface;
	
	@RequestMapping(value = "/androidApi/newnotifies", method = RequestMethod.GET)
	public @ResponseBody
	List<Notify> getMyNotifies(Principal principal){
		try
		{
			return notifyInterface.getNewNotifiesByUsername(principal.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
