package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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
	
	@RequestMapping(value = "/androidApi/getorderproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getOrderProducts(@RequestParam(value="idOrder", required=true) Integer idOrder)
	{
		List<Product> productsList = null;
		productsList = orderInterface.getProducts(idOrder);
		return productsList;
	}
	
	@RequestMapping(value = "/androidApi/getboughtamounts", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getBoughtAmounts(@RequestParam(value="idOrder", required=true) Integer idOrder,
			 @RequestParam(value="productIds", required=true) String productIdsString){
		
		if(idOrder != null && productIdsString != null && productIdsString.length() > 0){
			String[] splittedIds = productIdsString.split(",");
			List<Integer> productIds = new ArrayList<Integer>();
			for(int i = 0; i < splittedIds.length; i++)
				productIds.add(Integer.valueOf(splittedIds[i]));
			
			List<Integer> boughtAmounts = null;
			boughtAmounts = orderInterface.getBoughtAmounts(idOrder, productIds);
			return boughtAmounts;
		}
		return  null;
	}
	
	@RequestMapping(value = "/androidApi/getavailableordersforpurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getAvailableOrdersForPurchase(Principal principal)
	{
		String userName = principal.getName();
		Member member = memberInterface.getMember(userName);
		if(member != null){
			List<Order> listOrders = null;
			listOrders = orderInterface.getAvailableOrders(member);
			
			return listOrders;
		}
		else
			return null;
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
			
			List<Float> progresses = null;
			progresses = orderInterface.getProgresses(orderIds);
			return progresses;
		}
		return  null;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getmysuppliers", method = RequestMethod.GET)
	public @ResponseBody
	List<Supplier> getMySuppliers(Principal principal){
		String username = principal.getName();
		Member memberResp = memberInterface.getMember(username);
		
		if(memberResp != null){
			List<Supplier> ret = null;
			ret = new ArrayList<Supplier>(memberResp.getSuppliersForIdMemberResp());
			
			return ret;
		}
		
		return null;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/setneworder", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewOrder(Principal principal,
			@RequestParam(value = "idSupplier") int idSupplier,
			@RequestParam(value = "orderName") String orderName,
			@RequestParam(value = "idString") String idString,
			@RequestParam(value = "dataCloseTime") long dataCloseTime) throws InvalidParametersException, ParseException
	{
		
		List<Integer> productIds = new ArrayList<Integer>();
		String username = principal.getName();
		Member resp = memberInterface.getMember(username);
		
		if(resp == null)
			return -1;
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		Date dateOpen = calendar.getTime();
		
		Date dateClose = new Date(dataCloseTime);
		
		String[] temp = idString.split(",");
		if(temp.length > 0){
			for(int i = 0; i < temp.length; i++){
				try {
					Integer id = Integer.valueOf(temp[i]);
					productIds.add(id);
				} catch(NumberFormatException e){
					e.printStackTrace();
					throw new InvalidParametersException();
				}
				
			}
			return orderInterface.createOrder(resp, idSupplier, productIds, orderName, dateOpen, dateClose);
		}
		return -1;
		
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getactiveorderresp", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getActiveOrder(Principal principal) throws InvalidParametersException
	{
		String username = principal.getName();
		
		Member memberResp = memberInterface.getMember(username);
		
		List<Order> listOrder = null;
		listOrder = orderInterface.getActiveOrders(memberResp);
		return listOrder;
	}
	
	
}