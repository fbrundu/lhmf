package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.MessageInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Message;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

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
	@Autowired
	private OrderInterface orderInterface;
	@Autowired
	private ProductInterface productInterface;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/getusernames", method = RequestMethod.GET)
	public @ResponseBody
	List<String> getUsernames(HttpServletRequest request)
	{
		return memberInterface.getUsernames();
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/newmessage", method = RequestMethod.POST)
	public @ResponseBody
	Integer newMessage(
			HttpServletRequest request,
			@RequestParam(value = "dest", required = true) String dest,
			@RequestParam(value = "messageText", required = true) String messageText,
			@RequestParam(value = "idOrder", required = true) Integer idOrder,
			@RequestParam(value = "idProduct", required = true) Integer idProduct
			)
	{
		Integer idMessage = -1;
		try
		{
			Message m = new Message();
			m.setIsReaded(false);
			Member sender = memberInterface.getMember((String) request
					.getSession().getAttribute("username"));
			Member receiver = memberInterface.getMember(dest);
			m.setMemberByIdReceiver(receiver);
			m.setMemberByIdSender(sender);
			m.setMessageTimestamp(new Date());
			Order o = null;
			if(idOrder > 0)
				o = orderInterface.getOrder(idOrder);
			m.setOrder(o);
			Product p = null;
			if(idProduct > 0)
				p = productInterface.getProduct(idProduct);
			m.setProduct(p);
			m.setText(messageText);
			idMessage = messageInterface.newMessage(m);
		}
		catch (InvalidParametersException e)
		{
			// TODO Auto-generated catch block
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
		Member m = memberInterface.getMember((String) request.getSession()
				.getAttribute("username"));
		if (m != null)
			return messageInterface.getMessagesByIdMember(m.getIdMember());
		else
			return null;
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
			Member m = memberInterface.getMember((String) request.getSession()
					.getAttribute("username"));
			if (m != null)
			{
				pw = response.getWriter();
				Long unreadCount;
				unreadCount = messageInterface.getUnreadCount(m.getIdMember());
				if (unreadCount > 0)
				{
					pw.write("retry: 10000\n");
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
