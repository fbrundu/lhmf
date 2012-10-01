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
import java.util.Set;

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
	
	@RequestMapping(value = "/androidApi/getpurchaseproductsnormal", method = RequestMethod.GET)
	public @ResponseBody
	Set<PurchaseProduct> getPurchaseProductsForNormal(HttpServletRequest request, Principal principal, 
			@RequestParam(value="idPurchase", required = true)Integer idPurchase) throws InvalidParametersException {
		String username = principal.getName();
		Member memberNormal = memberInterface.getMember(username);
		
		Purchase p = purchaseInterface.getPurchase(idPurchase);
		if(p == null || p.getMember().getIdMember() != memberNormal.getIdMember())
			return null;
		
		return p.getPurchaseProducts();
	}
	
	@RequestMapping(value = "/androidApi/newpurchaseproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newPurchaseProduct(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase,
			@RequestParam(value = "idProduct") Integer idProduct,
			@RequestParam(value = "amount") Integer amountProduct) throws InvalidParametersException {
		String username = principal.getName();
		
		Member memberNormal = memberInterface.getMember(username);
		
		return purchaseInterface.insertProduct(memberNormal, idPurchase, idProduct, amountProduct);
	}
	
	@RequestMapping(value = "/ajax/updatepurchaseproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer updatePurchaseProduct(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase,
			@RequestParam(value = "idProduct") Integer idProduct,
			@RequestParam(value = "amount") Integer amountProduct) throws InvalidParametersException {
		
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		// -1 = Quantità non disponibile
		// -2 = valore non idoneo
		
		String username = principal.getName();
		Member memberNormal = memberInterface.getMember(username);
		
		return purchaseInterface.updateProduct(memberNormal, idPurchase, idProduct, amountProduct);
	}
	
	@RequestMapping(value = "/ajax/delpurchaseproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer delPurchaseProduct(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase,
			@RequestParam(value = "idProduct") Integer idProduct ) throws InvalidParametersException
	{
		
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		String username = principal.getName();
		Member memberNormal = memberInterface.getMember(username);
		
		return purchaseInterface.deletePurchaseProduct(memberNormal, idPurchase, idProduct);
	}
	
	/** FIXME implementare cancellazione scheda dopo eliminazione ultimo prodotto da android (l'originale è in NormalAjaxController)
	 * @PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/delPurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer delPurchase(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase			 ) throws InvalidParametersException, ParseException
	{
		
		// 1 = scheda eliminata
		// 0 = scheda non trovata
		
		Purchase purchase = purchaseInterface.getPurchase(idPurchase);
		
		return purchaseInterface.deletePurchase(purchase);
	}
	 */
	
}
