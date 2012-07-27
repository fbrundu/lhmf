package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NormalAjaxController
{
	@Autowired
	private OrderInterface orderInterface;
	
	@Autowired
	private PurchaseInterface purchaseInterface;
	
	@Autowired
	private MemberInterface memberInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getActiveOrderNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getActiveOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "start") long start,
			@RequestParam(value = "end") long end) throws InvalidParametersException
	{
		//String username = (String) session.getAttribute("username");
		
		//Member memberResp = memberInterface.getMember(username);
		
		List<Order> listOrder = null;
		listOrder = orderInterface.getOrdersNow();
		return listOrder;
	}
	///////////////////////////////////
	//@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	//@RequestMapping(value = "/ajax/getActivePurchase", method = RequestMethod.POST)
	//public @ResponseBody
	//List<Purchase> getActivePurchase(HttpServletRequest request, HttpSession session
			/*,@RequestParam(value = "start") long start,
			@RequestParam(value = "end") long end) throws InvalidParametersException*/
	//{
		//String username = (String) session.getAttribute("username");
		
		//Member memberResp = memberInterface.getMember(username);
		
		//List<Purchase> listPurchase = null;
		//listPurchase = purchaseInterface.getPurchasesByMember(memberResp.getUsername()); 
		//return listPurchase;
	//}
	
}