package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.model.LogInterface;
import it.polito.ai.lhmf.orm.Log;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
}
