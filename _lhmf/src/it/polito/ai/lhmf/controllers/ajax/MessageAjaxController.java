package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.MessageInterface;
import it.polito.ai.lhmf.orm.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessageAjaxController
{
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private MessageInterface messageInterface;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/getusernames", method = RequestMethod.GET)
	public @ResponseBody
	List<String> getUsernames(HttpServletRequest request)
	{
		return memberInterface.getUsernames();
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/getusernamesexceptme", method = RequestMethod.GET)
	public @ResponseBody
	List<String> getUsernamesExceptMe(HttpServletRequest request)
	{
		return memberInterface.getUsernamesExceptMe((String) request
				.getSession().getAttribute("username"));
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/getusersexceptme", method = RequestMethod.GET)
	public @ResponseBody
	Map<String, String> getUsersExceptMe(HttpServletRequest request)
	{
		Map<String, String> users = memberInterface
				.getUsersForMessageExceptMe((String) request.getSession()
						.getAttribute("username"));
		return users;
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/newmessage", method = RequestMethod.POST)
	public @ResponseBody
	Integer newMessage(
			HttpServletRequest request,
			@RequestParam(value = "dest", required = true) String dest,
			@RequestParam(value = "messageText", required = true) String messageText
	// @RequestParam(value = "idOrder", required = true) Integer idOrder,
	// @RequestParam(value = "idProduct", required = true) Integer idProduct
	)
	{
		Integer idMessage = -1;
		try
		{
			return messageInterface.newMessage(dest, messageText,
					(String) request.getSession().getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return idMessage;
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/getmymessages", method = RequestMethod.GET)
	public @ResponseBody
	List<Message> getMyMessages(HttpServletRequest request)
			throws InvalidParametersException
	{
		return messageInterface.getMessagesByUsername((String) request
				.getSession().getAttribute("username"));
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/setreadmessage", method = RequestMethod.POST)
	public @ResponseBody
	Integer setRead(
			HttpServletRequest request,
			@RequestParam(value = "idMessage", required = true) Integer idMessage)
			throws InvalidParametersException
	{
		return messageInterface.setRead((String) request.getSession()
				.getAttribute("username"), idMessage);
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping("/ajax/newmessages")
	public void newMessages(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		response.setContentType("text/event-stream; charset=utf-8");
		response.setHeader("cache-control", "no-cache");
		// response.setHeader("connection", "keep-alive");
		PrintWriter pw;
		try
		{
			pw = response.getWriter();
			Long unreadCount;
			unreadCount = messageInterface.getUnreadCount((String) request
					.getSession().getAttribute("username"));
			if (unreadCount > 0)
			{
				pw.write("retry: 10000\n");
				pw.write("data: " + unreadCount + "\n\n");
				pw.flush();
			}
			pw.close();
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
