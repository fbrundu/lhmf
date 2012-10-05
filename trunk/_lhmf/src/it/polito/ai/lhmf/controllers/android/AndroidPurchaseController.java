package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
	
	
	@RequestMapping(value = "/androidApi/setnewpurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewPurchase(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idOrder") Integer idOrder,
			@RequestParam(value = "idProducts") String idProducts,
			@RequestParam(value = "amountProducts") String amountProducts) throws InvalidParametersException, ParseException
	{
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
			
			return purchaseInterface.createPurchase(principal.getName(),
					idOrder, ids, amounts);
		}
		else
			return -1;
	}
	
	@RequestMapping(value = "/androidApi/getactivepurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getActivePurchase(HttpServletRequest request, Principal principal) throws InvalidParametersException
	{
		return purchaseInterface.getPurchasesOnDate(principal.getName(), 0);
	}
	
	@RequestMapping(value = "/androidApi/getpurchasecost", method = RequestMethod.GET)
	public @ResponseBody
	Float getPurchaseCost(HttpServletRequest request, Principal principal, 
			@RequestParam(value="idPurchase", required = true)Integer idPurchase) throws InvalidParametersException
	{
		return purchaseInterface.getPurchaseCost(principal.getName(), idPurchase, false);
	}
	
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getpurchaseproducts", method = RequestMethod.GET)
	public @ResponseBody
	Set<PurchaseProduct> getPurchaseProductsForNormal(HttpServletRequest request, Principal principal, 
			@RequestParam(value="idPurchase", required = true)Integer idPurchase) throws InvalidParametersException {
		String username = principal.getName();
		Member member = memberInterface.getMember(username);
		
		purchaseInterface.getPurchaseProducts(idPurchase);
		
		Purchase p = purchaseInterface.getPurchase(idPurchase);
		if(p == null || p.getMember().getIdMember() != member.getIdMember() && p.getOrder().getMember().getIdMember() != member.getIdMember())
			return null;
		
		return p.getPurchaseProducts();
	}
	
	@RequestMapping(value = "/androidApi/newpurchaseproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newPurchaseProduct(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase,
			@RequestParam(value = "idProduct") Integer idProduct,
			@RequestParam(value = "amount") Integer amountProduct)
			throws InvalidParametersException
	{
		return purchaseInterface.insertProduct(principal.getName(), idPurchase,
				idProduct, amountProduct);
	}
	
	@RequestMapping(value = "/androidApi/updatepurchaseproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer updatePurchaseProduct(HttpServletRequest request,
			Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase,
			@RequestParam(value = "idProduct") Integer idProduct,
			@RequestParam(value = "amount") Integer amountProduct)
			throws InvalidParametersException
	{

		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		// -1 = Quantita' non disponibile
		// -2 = valore non idoneo
		return purchaseInterface.updateProduct(principal.getName(), idPurchase,
				idProduct, amountProduct);
	}

	@RequestMapping(value = "/androidApi/delpurchaseproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer delPurchaseProduct(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase,
			@RequestParam(value = "idProduct") Integer idProduct)
			throws InvalidParametersException
	{
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato

		return purchaseInterface.deletePurchaseProduct(principal.getName(),
				idPurchase, idProduct);
	}
}
