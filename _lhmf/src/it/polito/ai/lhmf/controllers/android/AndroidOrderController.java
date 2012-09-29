package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AndroidOrderController {
	@Autowired
	private OrderInterface orderInterface;
	
	@Autowired
	private MemberInterface memberInterface;
	
	@RequestMapping(value = "/androidApi/getorderproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getOrderProducts(HttpServletRequest request, @RequestParam(value="idOrder", required=true) Integer idOrder)
	{
		List<Product> productsList = null;
		productsList = orderInterface.getProducts(idOrder);
		return productsList;
	}
	
	@RequestMapping(value = "/androidApi/getboughtamounts", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getBoughtAmounts(HttpServletRequest request, @RequestParam(value="idOrder", required=true) Integer idOrder,
			 @RequestParam(value="productIds", required=true) String productIdsString){
		
		if(idOrder != null && productIdsString != null && productIdsString.length() > 0){
			String[] splittedIds = productIdsString.split(",");
			List<Integer> productIds = new ArrayList<Integer>();
			for(int i = 0; i < splittedIds.length; i++)
				productIds.add(Integer.valueOf(splittedIds[i]));
			
			List<Integer> boughtAmounts = null;
			boughtAmounts = orderInterface.getBoughtAmounts(idOrder, productIds);
			return boughtAmounts;
		}
		return  null;
	}
	
	@RequestMapping(value = "/androidApi/getavailableordersforpurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getOrdersString(HttpServletRequest request, Principal principal)
	{
		String userName = principal.getName();
		Member member = memberInterface.getMember(userName);
		if(member != null){
			List<Order> listOrders = null;
			listOrders = orderInterface.getAvailableOrders(member);
			
			return listOrders;
		}
		else
			return null;
	}
}