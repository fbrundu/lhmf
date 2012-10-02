package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.OrderProduct;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.text.ParseException;
import java.util.ArrayList;
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
		List<Product> ret = null;
		ret = orderInterface.getProducts(idOrder);
		return ret;
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
		Set<OrderProduct> sProduct = order.getOrderProducts();
		
		for(OrderProduct op : sProduct)
			returnList.add(op.getProduct());
		
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
	List<Order> getOrdersString(HttpServletRequest request, HttpSession session)
	{
		
		String username = (String) session.getAttribute("username");
		Member memberNormal = memberInterface.getMember(username);
		
		return orderInterface.getAvailableOrders(memberNormal);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/setNewPurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewPurchase(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "idProducts") String idProducts,
			@RequestParam(value = "amountProducts") String amountProducts) throws InvalidParametersException, ParseException
	{
		String username = (String) session.getAttribute("username");
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
			return -1;
		
		Purchase purchase = purchaseInterface.getPurchase(idPurchase);
		Order order = purchase.getOrder();
		
		Integer totAmount = (int) (long) orderInterface.getTotalAmountOfProduct(order, product);
		
		result = maxBuy - totAmount;
		
		return result;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getDispOfProductOrder", method = RequestMethod.POST)
	public @ResponseBody
	Integer getDispOfProductOrder(HttpServletRequest request,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "idProduct") int idProduct	) throws InvalidParametersException, ParseException
	{
		
		// -1 Disponibilità infinita
		// 0 Non Disponibile
		// xx Quantità disponibile
		
		Integer result = -1;
	
		Product product = productInterface.getProduct(idProduct);
		
		Integer maxBuy = product.getMaxBuy();
		
		if(maxBuy == null)
			return -1;

		Order order = orderInterface.getOrder(idOrder);
		
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
		String username = (String) request.getSession().getAttribute("username");
		Member memberNormal = memberInterface.getMember(username);
		
		return purchaseInterface.insertProduct(memberNormal, idPurchase, idProduct, amountProduct);
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
		
		String username = (String) session.getAttribute("username");
		Member memberNormal = memberInterface.getMember(username);
		
		return purchaseInterface.updateProduct(memberNormal, idPurchase, idProduct, amountProduct);
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
		String username = (String) session.getAttribute("username");
		Member memberNormal = memberInterface.getMember(username);
		
		return purchaseInterface.deletePurchaseProduct(memberNormal, idPurchase, idProduct);
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
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getProgressOrder", method = RequestMethod.POST)
	public @ResponseBody
	Float getProgressOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder		) throws InvalidParametersException
	{
		return orderInterface.getProgress(idOrder);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getProgressProductOfOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<String> getProgressProductOfOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder		) throws InvalidParametersException
	{
		return orderInterface.getProgressProduct(idOrder);
	}
}
