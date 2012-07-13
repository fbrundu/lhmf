package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Purchase;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PurchaseAjaxController
{
	@Autowired
	private PurchaseInterface purchaseInterface;
	
	@RequestMapping(value = "/ajax/newpurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer newPurchase(HttpServletRequest request,
			@RequestBody Purchase purchase) throws InvalidParametersException
	{
		Integer idPurchase = -1;
			idPurchase = purchaseInterface.newPurchase(purchase);
		return idPurchase;
	}
	/*
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
		purchaseList = purchaseIterface.getActivePurchase(); 
		return purchaseList;
	}
	*/
}
