package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.model.LogInterface;
import it.polito.ai.lhmf.orm.Log;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LogAjaxController
{
	@Autowired
	private LogInterface logInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getlogs", method = RequestMethod.GET)
	public @ResponseBody
	List<Log> getLogs(HttpServletRequest request,
			@RequestParam(value = "start") long start,
			@RequestParam(value = "end") long end)
	{
		List<Log> logs = null;
		logs = logInterface.getLogs(start, end);
		return logs;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getlogsamount", method = RequestMethod.GET)
	public @ResponseBody
	Long getLogsAmount(HttpServletRequest request,
			@RequestParam(value = "start") long start,
			@RequestParam(value = "end") long end)
	{
		return logInterface.getLogsAmount(start, end);
	}
}
