package it.polito.ai.lhmf.controllers.ajax;

import java.util.List;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PurchaseAjaxController
{
	@Autowired
	private PurchaseInterface purchaseInterface;
	
	@Autowired
	private OrderInterface orderInterface;
	
	@Autowired
	private MemberInterface memberInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/newpurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer newPurchase(HttpServletRequest request,
			@RequestParam(value = "isShipped", required = true) Boolean isShipped) throws InvalidParametersException
	{
		Integer idPurchase = -1;
		Order order = orderInterface.getOrder((Integer) request.getSession().getAttribute("idOrder"));
		Member member = memberInterface.getMember((Integer) request.getSession().getAttribute("idMember"));		
		if(order != null && member != null)
		{
			Purchase purchase = new Purchase();
			purchase.setIsShipped(isShipped);
			purchase.setOrder(order);
			purchase.setMember(member);
			idPurchase = purchaseInterface.newPurchase(purchase);
		}
		
		return idPurchase;
	}
	
	@RequestMapping(value = "/ajax/getpastpurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getPastPurchase(HttpServletRequest request)
	{
		List<Purchase> purchaseList = null;
		purchaseList = purchaseInterface.getPastPurchase(); 
		return purchaseList;
	}
	
	@RequestMapping(value = "/ajax/getactivepurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getActivePurchase(HttpServletRequest request)
	{
		List<Purchase> purchaseList = null;
		purchaseList = purchaseInterface.getActivePurchase(); 
		return purchaseList;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getmypurchases", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getMyProducts(HttpServletRequest request)
	{
		List<Purchase> purchasesList = null;
		purchasesList = purchaseInterface.getPurchasesByMember((String) request
				.getSession().getAttribute("username"));
		return purchasesList;
	}
	
}
