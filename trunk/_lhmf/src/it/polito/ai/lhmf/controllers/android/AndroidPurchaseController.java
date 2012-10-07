package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.security.Principal;
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
	
	@RequestMapping(value = "/androidApi/setnewpurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewPurchase(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idOrder") Integer idOrder,
			@RequestParam(value = "idProducts") String idProducts,
			@RequestParam(value = "amountProducts") String amountProducts)
	{
		try
		{
			String[] idTmp = idProducts.split(",");
			String[] amountTmp = amountProducts.split(",");

			if (idTmp.length > 0 && idTmp.length == amountTmp.length)
			{
				Integer[] ids = new Integer[idTmp.length];
				Integer[] amounts = new Integer[idTmp.length];
				for (int i = 0; i < idTmp.length; i++)
				{
					try
					{
						ids[i] = Integer.parseInt(idTmp[i]);

						amounts[i] = Integer.parseInt(amountTmp[i]);
						if (amounts[i] <= 0)
							return -1;
					}
					catch (NumberFormatException e)
					{
						e.printStackTrace();
						return -1;
					}
				}

				return purchaseInterface.createPurchase(
						principal.getName(), idOrder, ids,
						amounts);
			}
			else
				return -1;
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@RequestMapping(value = "/androidApi/getactivepurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getActivePurchase(HttpServletRequest request, Principal principal) throws InvalidParametersException
	{
		return purchaseInterface.getPurchasesOnDate(principal.getName(), 0);
	}
	
	@RequestMapping(value = "/androidApi/getmypurchase", method = RequestMethod.GET)
	public @ResponseBody
	Purchase getMyPurchase(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idOrder") Integer idOrder) throws InvalidParametersException
	{
		return purchaseInterface.getMyPurchase(principal.getName(), idOrder);
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
	Set<PurchaseProduct> getPurchaseProductsForNormal(
			HttpServletRequest request,
			Principal principal,
			@RequestParam(value = "idPurchase", required = true) Integer idPurchase)
	{
		try
		{
			return purchaseInterface.getPurchaseProductsCheckMember(principal.getName(),
					idPurchase);
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value = "/androidApi/newpurchaseproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newPurchaseProduct(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase,
			@RequestParam(value = "idProduct") Integer idProduct,
			@RequestParam(value = "amount") Integer amountProduct)
	{
		try
		{
			return purchaseInterface.insertProduct(principal.getName(), idPurchase,
					idProduct, amountProduct);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value = "/androidApi/updatepurchaseproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer updatePurchaseProduct(HttpServletRequest request,
			Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase,
			@RequestParam(value = "idProduct") Integer idProduct,
			@RequestParam(value = "amount") Integer amountProduct)
	{
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		// -1 = Quantita' non disponibile
		// -2 = valore non idoneo/errore
		try
		{
			return purchaseInterface.updateProduct(principal.getName(), idPurchase,
					idProduct, amountProduct);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -2;
		}
	}

	@RequestMapping(value = "/androidApi/delpurchaseproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer delPurchaseProduct(HttpServletRequest request, Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase,
			@RequestParam(value = "idProduct") Integer idProduct)
	{
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		// -1 = errore
		
		try
		{
			return purchaseInterface.deletePurchaseProduct(principal.getName(),
					idPurchase, idProduct);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	
	@RequestMapping(value = "/androidApi/getpurchaseswithdelivery", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getPurchaseWithDeliveryDate(HttpServletRequest request, Principal principal) throws InvalidParametersException
	{
		return purchaseInterface.getShipPurchases(principal.getName());
	}
}
