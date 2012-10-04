package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.MessageInterface;
import it.polito.ai.lhmf.model.NotifyInterface;
import it.polito.ai.lhmf.orm.Member;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SSEAjaxController
{
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private MessageInterface messageInterface;
	@Autowired
	private NotifyInterface notifyInterface;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping("/ajax/getnews")
	public void sseNews(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		response.setContentType("text/event-stream; charset=utf-8");
		response.setHeader("cache-control", "no-cache");
		// response.setHeader("connection", "keep-alive");
		PrintWriter pw;
		try
		{
			Member m = memberInterface.getMember((String) request.getSession()
					.getAttribute("username"));
			if (m != null)
			{
				pw = response.getWriter();
				Long mesUnreadCount, notUnreadCount;
				mesUnreadCount = messageInterface
						.getUnreadCount((String) request.getSession()
								.getAttribute("username"));
				notUnreadCount = notifyInterface
						.getUnreadCount((String) request.getSession()
								.getAttribute("username"));
				if (mesUnreadCount > 0 || notUnreadCount > 0)
				{
					pw.write("retry: 10000\n");
					pw.write("event: mess\n");
					pw.write("data: " + mesUnreadCount + "\n\n");
					pw.write("event: nots\n");
					pw.write("data: " + notUnreadCount + "\n\n");
					pw.flush();
				}
				pw.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
