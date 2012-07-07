package it.polito.ai.lhmf;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LogController {
	@RequestMapping("/log")
	public ModelAndView logPage(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="min", required=false) Long minTs, @RequestParam(value="max", required=false) Long maxTs)
	{
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		model.addAttribute("user", user);
		
		/* se non e' admin, ritorna UNAUTHORIZED */ //TODO è giusto mandare gli errori così???
		if ((Integer) request.getSession().getAttribute("member_type") != 3)
			try {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			} catch (IOException e) {
				e.printStackTrace();
			}
		else{
			model.addAttribute("firstPage", "log");
			if(minTs != null && maxTs != null){
				model.addAttribute("minLog", minTs);
				model.addAttribute("maxLog", maxTs);
			}
			return new ModelAndView("log_admin");
		}
		return null;
	}
}
