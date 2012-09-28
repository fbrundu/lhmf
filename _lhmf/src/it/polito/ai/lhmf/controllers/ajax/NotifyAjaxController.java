package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.NotifyInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Notify;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.io.IOException;
import java.io.PrintWriter;
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
public class NotifyAjaxController
{
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private NotifyInterface notifyInterface;
	@Autowired
	private OrderInterface orderInterface;
	@Autowired
	private ProductInterface productInterface;

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

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/setreadnotify", method = RequestMethod.POST)
	public @ResponseBody
	Integer setRead(
			HttpServletRequest request,
			@RequestParam(value = "idNotify", required = true) Integer idNotify)
			throws InvalidParametersException
	{
		Member m = memberInterface.getMember((String) request.getSession()
				.getAttribute("username"));
		if (m != null)
			return notifyInterface.setRead(m.getIdMember(), idNotify);
		else
			return null;
	}
	
	@RequestMapping("/ajax/newnotifies")
	public void newNotifies(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		response.setContentType("text/event-stream; charset=utf-8");
		response.setHeader("cache-control", "no-cache");
		response.setHeader("connection", "keep-alive");
		PrintWriter pw;
		try
		{
			Member m = memberInterface.getMember((String) request.getSession()
					.getAttribute("username"));
			if (m != null)
			{
				pw = response.getWriter();
				Long unreadCount;
				unreadCount = notifyInterface.getUnreadCount(m.getIdMember());
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
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/viewP", method = RequestMethod.POST)
	public @ResponseBody
	Product viewProd(
			HttpServletRequest request,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
			throws InvalidParametersException
	{
		return productInterface.getProduct(idProduct);
	}
	
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/viewO", method = RequestMethod.POST)
	public @ResponseBody
	Order viewOrder(HttpServletRequest request,
			@RequestParam(value = "idOrder", required = true) Integer idOrder)
			throws InvalidParametersException
	{
		return orderInterface.getOrder(idOrder);
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/viewM", method = RequestMethod.POST)
	public @ResponseBody
	Member viewMember(
			HttpServletRequest request,
			@RequestParam(value = "idMember", required = true) Integer idMember)
			throws InvalidParametersException
	{
		return memberInterface.getMember(idMember);
	}
}
