package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.MessageInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessageAjaxController
{
	@Autowired
	private MemberInterface memberInterface;

	@Autowired
	private MessageInterface messageInterface;

	@RequestMapping(value = "/ajax/getmymessages", method = RequestMethod.GET)
	public @ResponseBody
	List<Message> getMyMessages(HttpServletRequest request)
			throws InvalidParametersException
	{
		Member m = memberInterface.getMember((String) request.getSession()
				.getAttribute("username"));
		if (m != null)
			return messageInterface.getMessagesByIdMember(m.getIdMember());
		else
			return null;
	}

	@RequestMapping("/ajax/newmessages")
	public void newMessages(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		response.setContentType("text/event-stream; charset=utf-8");
		response.setHeader("cache-control", "no-cache");
		//response.setHeader("connection", "keep-alive");
		PrintWriter pw;
		try
		{
			Member m = memberInterface.getMember((String) request.getSession()
					.getAttribute("username"));
			if (m != null)
			{
				pw = response.getWriter();
				Long unreadCount;
				unreadCount = messageInterface.getUnreadCount(m
						.getIdMember());
				if (unreadCount > 0)
				{
					pw.write("data: " + unreadCount + "\n\n");
					pw.flush();
				}
				pw.close();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidParametersException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
