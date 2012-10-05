package it.polito.ai.lhmf.controllers.ajax;

import java.util.List;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.PurchaseInterface;
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

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/newpurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer newPurchase(
			HttpServletRequest request,
			@RequestParam(value = "isShipped", required = true) Boolean isShipped)
	{
		try
		{
			Purchase p = new Purchase();
			p.setIsShipped(isShipped);
			return purchaseInterface.newPurchase(p, (Integer) request
					.getSession().getAttribute("idMember"), (Integer) request
					.getSession().getAttribute("idOrder"));
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getmypurchases", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getMyProducts(HttpServletRequest request)
	{
		return purchaseInterface.getPurchasesByMember((String) request
				.getSession().getAttribute("username"));
	}
}
