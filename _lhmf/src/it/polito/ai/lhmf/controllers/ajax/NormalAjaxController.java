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
			//Non faccio check sul valore di ritorno. In questo caso, dato che l'id non � generato ma gi� passato, se ci sono errori lancia un'eccezione
			purchaseInterface.newPurchaseProduct(purchaseproduct);
		}		
		return 1;
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
