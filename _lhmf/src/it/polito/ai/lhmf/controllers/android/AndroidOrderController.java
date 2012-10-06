package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.OrderProduct;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AndroidOrderController {
	@Autowired
	private OrderInterface orderInterface;
	
	@Autowired
	private MemberInterface memberInterface;
	
	@Autowired
	private PurchaseInterface purchaseInterface;
	
	@RequestMapping(value = "/androidApi/getorder", method = RequestMethod.GET)
	public @ResponseBody
	Order getOrder(
			@RequestParam(value = "idOrder", required = true) Integer idOrder)
	{
		try
		{
			return orderInterface.getOrder(idOrder);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value = "/androidApi/getorderproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getOrderProducts(
			@RequestParam(value = "idOrder", required = true) Integer idOrder)
	{
		try
		{
			return orderInterface.getProducts(idOrder);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value = "/androidApi/getorderorderproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<OrderProduct> getOrderOrderProducts(@RequestParam(value="idOrder", required=true) Integer idOrder)
	{
		return orderInterface.getOrderProducts(idOrder);
	}
	
	@RequestMapping(value = "/androidApi/getboughtamounts", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getBoughtAmounts(@RequestParam(value="idOrder", required=true) Integer idOrder,
			 @RequestParam(value="productIds", required=true) String productIdsString){
		
		if(idOrder != null && productIdsString != null && productIdsString.length() > 0){
			String[] splittedIds = productIdsString.split(",");
			List<Integer> productIds = new ArrayList<Integer>();
			for(int i = 0; i < splittedIds.length; i++)
				productIds.add(Integer.valueOf(splittedIds[i]));
			
			return orderInterface.getBoughtAmounts(idOrder, productIds);
		}
		return  null;
	}
	
	@RequestMapping(value = "/androidApi/getavailableordersforpurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getAvailableOrdersForPurchase(Principal principal)
	{
		try
		{
			List<Order> listOrders = null;
			listOrders = orderInterface.getAvailableOrders(principal.getName());

			return listOrders;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	@RequestMapping(value = "/androidApi/getorderprogress", method = RequestMethod.GET)
	public @ResponseBody
	Float getOrderProgress(@RequestParam(value="idOrder", required=true) Integer idOrder){
		return orderInterface.getProgress(idOrder);
	}
	
	@RequestMapping(value = "/androidApi/getordersprogresses", method = RequestMethod.GET)
	public @ResponseBody
	List<Float> getOrdersProgresses(@RequestParam(value="orderIds", required=true) String orderIdsString){
		if(orderIdsString != null && orderIdsString.length() > 0){
			String[] splittedIds = orderIdsString.split(",");
			List<Integer> orderIds = new ArrayList<Integer>();
			for(int i = 0; i < splittedIds.length; i++)
				orderIds.add(Integer.valueOf(splittedIds[i]));
			
			return orderInterface.getProgresses(orderIds);
		}
		return  null;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getmysuppliers", method = RequestMethod.GET)
	public @ResponseBody
	List<Supplier> getMySuppliers(Principal principal)
	{
		return memberInterface.getSuppliersForIdMemberResp(principal.getName());
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/setneworder", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewOrder(Principal principal,
			@RequestParam(value = "idSupplier") int idSupplier,
			@RequestParam(value = "orderName") String orderName,
			@RequestParam(value = "idString") String idString,
			@RequestParam(value = "dataCloseTime") long dataCloseTime)
	{
		try
		{
			return orderInterface.setNewOrder(principal.getName(), idSupplier,
					orderName, idString, dataCloseTime);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getactiveorderresp", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getActiveOrder(Principal principal)
	{
		try
		{
			return orderInterface.getActiveOrders(principal.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getcompletedordersresp", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getCompletedOrders(Principal principal)
	{
		try
		{
			return orderInterface.getCompletedOrders(principal.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/setdeliverydate", method = RequestMethod.POST)
	public @ResponseBody
	Integer setDeliveryDate(Principal principal,
			@RequestParam(value = "idOrder") Integer idOrder,
			@RequestParam(value = "dateDelivery") Long dateDelivery)
	{

		try
		{
			return orderInterface.setDeliveryDate(idOrder, principal.getName(),
					new Date(dateDelivery));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getdeliveredorderresp", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getDeliveredOrderResp(Principal principal) throws InvalidParametersException
	{
		return orderInterface.getOrdersToDelivery(principal.getName());
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/setship", method = RequestMethod.POST)
	public @ResponseBody
	Integer setPurchaseShipped(Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase) throws InvalidParametersException
	{
		try
		{
			return purchaseInterface.setPurchaseShipped(
					principal.getName(), idPurchase);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getpurchasefromorder", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getPurchaseFromOrder(Principal principal,
			@RequestParam(value = "idOrder") Integer idOrder)
	{
		try
		{
			return orderInterface.getPurchasesFromOrder(idOrder,
					principal.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/ispurchasefailed", method = RequestMethod.GET)
	public @ResponseBody
	Boolean isPurchaseFailed(Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase)
			throws InvalidParametersException
	{
		return purchaseInterface.isFailed(principal.getName(), idPurchase);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getcomletedpurchasecost", method = RequestMethod.GET)
	public @ResponseBody
	Float getCompletedPurchaseCost(Principal principal,
			@RequestParam(value = "idPurchase") Integer idPurchase) throws InvalidParametersException
	{
		return purchaseInterface.getPurchaseCost(principal.getName(), idPurchase, true);
	}
}