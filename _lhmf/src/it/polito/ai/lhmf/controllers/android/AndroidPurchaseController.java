package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AndroidPurchaseController {
	@Autowired
	private PurchaseInterface purchaseInterface;
	
	@Autowired
	private MemberInterface memberInterface;

	@Autowired
	private OrderInterface orderInterface;
	
	
	@RequestMapping(value = "/androidApi/setnewpurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewPurchase(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idOrder") Integer idOrder,
			@RequestParam(value = "idProducts") String idProducts,
			@RequestParam(value = "amountProducts") String amountProducts) throws InvalidParametersException, ParseException
	{
		String username = principal.getName();
		Member memberNormal = memberInterface.getMember(username);
		
		String[] idTmp = idProducts.split(",");
		String[] amountTmp = amountProducts.split(",");
		
		if(idTmp.length > 0 && idTmp.length == amountTmp.length){
			Integer[] ids = new Integer[idTmp.length];
			Integer[] amounts = new Integer[idTmp.length];
			for( int i = 0; i < idTmp.length; i++) 
			{
				try {
					ids[i] = Integer.parseInt(idTmp[i]);
				
					amounts[i] = Integer.parseInt(amountTmp[i]);
					if(amounts[i] <= 0)
						return -1;
				} catch(NumberFormatException e){
					e.printStackTrace();
					return -1;
				}
			}
			
			return purchaseInterface.createPurchase(memberNormal, idOrder, ids, amounts);
		}
		else
			return -1;
	}
	
	@RequestMapping(value = "/androidApi/getactivepurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getActivePurchase(HttpServletRequest request, Principal principal) throws InvalidParametersException
	{
		String username = principal.getName();
		
		Member memberNormal = memberInterface.getMember(username);
		List<Order> orderTmp = null;
		orderTmp = orderInterface.getOrdersNow();
		List<Purchase> listPurchase = null;
		listPurchase = purchaseInterface.getPurchasesOnDate(memberNormal.getIdMember(), orderTmp); 
		return listPurchase;
	}
	
	@RequestMapping(value = "/androidApi/getpurchasecost", method = RequestMethod.GET)
	public @ResponseBody
	Float getPurchaseCost(HttpServletRequest request, Principal principal, 
			@RequestParam(value="idPurchase", required = true)Integer idPurchase) throws InvalidParametersException
	{
		Float ret = 0.0f;
		List<PurchaseProduct> bought = purchaseInterface.getPurchaseProduct(idPurchase);
		if(bought.size() == 0)
			return null;
		for(PurchaseProduct pp : bought){
			ret += pp.getAmount() * pp.getProduct().getUnitCost();
		}
		return ret;
	}
	
}
