package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.orm.PurchaseProductId;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	@Autowired
	private ProductInterface productInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getActivePurchase", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getActivePurchase(HttpServletRequest request, HttpSession session) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		
		Member memberNormal = memberInterface.getMember(username);
		List<Order> orderTmp = null;
		orderTmp = orderInterface.getOrdersNow();
		List<Purchase> listPurchase = null;
		listPurchase = purchaseInterface.getPurchasesOnDate(memberNormal.getIdMember(), orderTmp); 
		return listPurchase;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getOldPurchase", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getOldPurchase(HttpServletRequest request, HttpSession session) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		
		Member memberNormal = memberInterface.getMember(username);
		List<Order> orderTmp = null;
		orderTmp = orderInterface.getOrdersPast();
		List<Purchase> listPurchase = null;
		listPurchase = purchaseInterface.getPurchasesOnDate(memberNormal.getIdMember(), orderTmp); 
		return listPurchase;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getProductFromOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductFromOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder) throws InvalidParametersException
	{
		Order order = orderInterface.getOrder(idOrder);
		List<Product> listProduct = new ArrayList<Product>(order.getProducts());
		return listProduct;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getProductNormal", method = RequestMethod.POST)
	public @ResponseBody
	Product getProductNormal(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idProduct") int idProduct) throws InvalidParametersException
	{
		Product product = productInterface.getProduct(idProduct);
		return product;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getPurchaseDetails", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getPurchaseDetails(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase) throws InvalidParametersException
	{
		List<PurchaseProduct> productTmp = purchaseInterface.getPurchaseProduct(idPurchase);
		List<Product> listProduct = new ArrayList<Product>();
		for(PurchaseProduct product : productTmp)
		{
			listProduct.add(productInterface.getProduct(product.getId().getIdProduct()));
		}
		return listProduct;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getOtherProductsOfPurchase", method = RequestMethod.POST)
	public @ResponseBody
	Set<Product> getOtherProductsOfPurchase(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase) throws InvalidParametersException
	{
		Purchase purchase = purchaseInterface.getPurchase(idPurchase);
		Order order = purchase.getOrder();
		
		Set<Product> returnList = new HashSet<Product>();
		Set<Product> sProduct = order.getProducts();
		
		returnList.addAll(sProduct);
		
		Set<PurchaseProduct> ppList = purchase.getPurchaseProducts();
		
		for(PurchaseProduct product : ppList)
		{
			returnList.remove(product.getProduct());
		}
		
		return returnList;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getOrdersString", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> getOrdersString(HttpServletRequest request, HttpSession session)
	{
		ArrayList<String> orderString = new ArrayList<String>();
		
		List<Order> listOrders = new ArrayList<Order>();
		listOrders = orderInterface.getOrdersNow();
		
		for (Order or : listOrders) 
		{			
			String temp = or.getIdOrder() + ", " + or.getOrderName() + " - " + or.getDateClose() + "," + or.getDateDelivery();					
			orderString.add(temp);		
		}		
		return orderString;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/setNewPurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewPurchase(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "idProducts") String idProducts,
			@RequestParam(value = "amountProducts") String amountProduct) throws InvalidParametersException, ParseException
	{
		int result = -1;
		String username = (String) session.getAttribute("username");
		Member memberNormal = memberInterface.getMember(username);
		
		String[] idTmp = idProducts.split(",");
		String[] amountTmp = amountProduct.split(",");
		for( int i = 0; i < idTmp.length; i++) 
		{
			Product product = productInterface.getProduct(Integer.parseInt(idTmp[i]));
			if((Integer.parseInt(amountTmp[i]) > product.getMaxBuy()) || (Integer.parseInt(amountTmp[i])) <= 0)
			{
				return -2;
			}
		}
		
		Order order = orderInterface.getOrder(idOrder);
		
		Purchase purchase = new Purchase(order, memberNormal);
		
		if((result = purchaseInterface.newPurchase(purchase)) <= 0)
		{
			return result;
		}
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(calendar.getTime());
		Date insertedTimestamp = dateFormat.parse(sDate);
		
		for( int i = 0; i < idTmp.length; i++) 
		{
			Product product = productInterface.getProduct(Integer.parseInt(idTmp[i]));
			PurchaseProductId id = new PurchaseProductId(purchase.getIdPurchase(), Integer.parseInt(idTmp[i]));
			PurchaseProduct purchaseproduct = new PurchaseProduct(id, purchase, product, Integer.parseInt(amountTmp[i]), insertedTimestamp);				
			//Non faccio check sul valore di ritorno. In questo caso, dato che l'id non è generato ma già passato, se ci sono errori lancia un'eccezione
			purchaseInterface.newPurchaseProduct(purchaseproduct);
		}		
		return 1;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getDispOfProduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer getDispOfProduct(HttpServletRequest request,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct	) throws InvalidParametersException, ParseException
	{
		
		// -1 Disponibilità infinita
		// 0 Non Disponibile
		// xx Quantità disponibile
		
		Integer result = -1;
	
		Product product = productInterface.getProduct(idProduct);
		
		Integer maxBuy = product.getMaxBuy();
		
		if(maxBuy == null)
			return 0;
		
		Purchase purchase = purchaseInterface.getPurchase(idPurchase);
		Order order = purchase.getOrder();
		
		Integer totAmount = (int) (long) orderInterface.getTotalAmountOfProduct(order, product);
		
		result = maxBuy - totAmount;
		
		return result;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/newPurchaseProduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newPurchaseProduct(HttpServletRequest request,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct,
			@RequestParam(value = "amount") int amountProduct) throws InvalidParametersException, ParseException
	{
		int result = 0;
	
		Product product = productInterface.getProduct(idProduct);
		if(amountProduct > product.getMaxBuy() || amountProduct <= 0)
		{
			return -2;
		}
		
		Purchase purchase = purchaseInterface.getPurchase(idPurchase);
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(calendar.getTime());
		Date insertedTimestamp = dateFormat.parse(sDate);
		
		PurchaseProductId id = new PurchaseProductId(idPurchase, idProduct);
		PurchaseProduct purchaseproduct = new PurchaseProduct(id, purchase, product, amountProduct, insertedTimestamp);	
		PurchaseProductId res = purchaseInterface.newPurchaseProduct(purchaseproduct);	
		
		if(res == null)
			result = -1;
		
		return result;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/updatePurchaseProduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer updatePurchaseProduct(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct,
			@RequestParam(value = "amount") int amountProduct) throws InvalidParametersException, ParseException
	{
		
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		// -1 = Quantità non disponibile
		// -2 = valore non idoneo
		
		int result = 0;
	
		Product product = productInterface.getProduct(idProduct);
		
		Integer disp = getDispOfProduct(request, idPurchase, idProduct);
		
		
		
		if(amountProduct > product.getMaxBuy() || amountProduct <= 0)
			return -2;

		Purchase purchase = purchaseInterface.getPurchase(idPurchase);
		
		Set<PurchaseProduct> ppSet = purchase.getPurchaseProducts();
		Product pTemp;
		
		for (PurchaseProduct ppTemp : ppSet) {
			
			pTemp = ppTemp.getProduct();
			
			if(pTemp.equals(product)) {
				
				Integer actualAmount = ppTemp.getAmount();
				
				if(amountProduct - actualAmount > disp)
					return -1;

				ppTemp.setAmount(amountProduct);
				result = purchaseInterface.updatePurchaseProduct(ppTemp);
				break;
			}
			
		}
		
		return result;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/delPurchaseProduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer delPurchaseProduct(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct ) throws InvalidParametersException, ParseException
	{
		
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
	
		Product product = productInterface.getProduct(idProduct);
		Purchase purchase = purchaseInterface.getPurchase(idPurchase);

		return purchaseInterface.deletePurchaseProduct(purchase, product);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getAmountfromPurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer getAmountfromPurchase(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct) throws InvalidParametersException
	{
		PurchaseProduct tmpPP = null;
		tmpPP = purchaseInterface.getPurchaseProductFromId(idPurchase,idProduct); 
		return tmpPP.getAmount();
	}
	
}
