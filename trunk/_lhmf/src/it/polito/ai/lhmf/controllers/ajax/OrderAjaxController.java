package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderAjaxController
{
	@Autowired
	private OrderInterface orderInterface;

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/neworder", method = RequestMethod.POST)
	public @ResponseBody
	Integer newOrder(HttpServletRequest request, @RequestBody Order order)
	{
		try
		{
			return orderInterface.newOrder(order);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	@RequestMapping(value = "/ajax/getpastorders", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getPastOrders(HttpServletRequest request)
	{
		return orderInterface.getPastOrders();
	}

	@RequestMapping(value = "/ajax/getactiveorders", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getActiveOrders(HttpServletRequest request)
	{
		return orderInterface.getActiveOrders();
	}
}
