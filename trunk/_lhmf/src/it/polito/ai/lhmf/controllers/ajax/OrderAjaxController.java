package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.orm.Order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping(value = "/ajax/neworder", method = RequestMethod.POST)
	public @ResponseBody
	Integer newOrder(HttpServletRequest request, @RequestBody Order order)
	{
		Integer idOrder = -1;
		try 
		{
			idOrder = orderInterface.newOrder(order);
		}
		catch (InvalidParametersException e) 
		{
			e.printStackTrace();
		}
		return idOrder;
	}
	
	@RequestMapping(value = "/ajax/getpastorders", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getPastOrders(HttpServletRequest request)
	{
		List<Order> orderList = null;
		orderList = orderInterface.getPastOrders(); 
		return orderList;
	}
	
	@RequestMapping(value = "/ajax/getactiveorders", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getActiveOrders(HttpServletRequest request)
	{
		List<Order> orderList = null;
		orderList = orderInterface.getActiveOrders(); 
		return orderList;
	}
}
