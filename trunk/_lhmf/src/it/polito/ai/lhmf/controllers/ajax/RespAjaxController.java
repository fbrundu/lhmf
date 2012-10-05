package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class RespAjaxController
{
	@Autowired
	private OrderInterface orderInterface;
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private PurchaseInterface purchaseInterface;
	@Autowired
	private ProductInterface productInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getActiveOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getActiveOrder(HttpServletRequest request, HttpSession session)
	{
		try
		{
			return orderInterface.getActiveOrders((String) session
					.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOldOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getOldOrderResp(HttpServletRequest request,
			HttpSession session, @RequestParam(value = "end") long end,
			@RequestParam(value = "dateDeliveryType") int dateDeliveryType)
	{
		try
		{
			return orderInterface.getOldOrders(
					(String) session.getAttribute("username"), end,
					dateDeliveryType);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getDeliveredOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getDeliveredOrderResp(HttpServletRequest request,
			HttpSession session)
	{
		try
		{
			return orderInterface.getOrdersToDelivery((String) session
					.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.RESP + ","
			+ MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getProductListFromOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductListFromOrder(HttpServletRequest request,
			HttpSession session, @RequestParam(value = "idOrder") int idOrder)
	{
		try
		{
			return orderInterface.getProducts(idOrder,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductListFromPurchase", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductListFromPurchase(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
	{
		try
		{
			return purchaseInterface.getProducts(idPurchase,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setDeliveryDate", method = RequestMethod.POST)
	public @ResponseBody
	Integer setDeliveryDate(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "dateDelivery") long dateDelivery)
	{
		try
		{
			return orderInterface.setDeliveryDate(idOrder, (String) session.getAttribute("username"), new Date(dateDelivery));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getCompleteOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getCompleteOrderResp(HttpServletRequest request,
			HttpSession session)
	{
		try
		{
			return orderInterface.getCompletedOrders((String) session
					.getAttribute("username"));
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseFromOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getPurchaseFromOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder)
	{
		try
		{
			return orderInterface.getPurchasesFromOrder(idOrder,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseProducts", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getPurchaseProducts(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
	{
		try
		{
			return purchaseInterface.getProducts(idPurchase,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseAmount", method = RequestMethod.POST)
	public @ResponseBody
	Integer getPurchaseAmount(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct)
	{
		return purchaseInterface.getAmount(idPurchase, idProduct);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setShip", method = RequestMethod.POST)
	public @ResponseBody
	Integer setPurchaseShipped(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
	{
		try
		{
			return purchaseInterface.setPurchaseShipped(
					(String) session.getAttribute("username"), idPurchase);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@RequestMapping(value = "/ajax/getMembersSupplierString", method = RequestMethod.POST)
	public @ResponseBody
	List<String> getMembersSupplierString(HttpServletRequest request,
			HttpSession session)
	{
		return memberInterface.getMembersSupplierString((String) session
				.getAttribute("username"));
	}
	
	@RequestMapping(value = "/ajax/getSupplierByResp", method = RequestMethod.POST)
	public @ResponseBody
	Set<Supplier> getSupplierByResp(HttpServletRequest request,
			HttpSession session)
	{
		return memberInterface.getSupplierByResp((String) session
				.getAttribute("username"));
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductFromSupplier", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductFromSupplier(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idSupplier") int idSupplier)
	{
		return productInterface.getProductsBySupplier(idSupplier);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setNewOrder", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idSupplier") int idSupplier,
			@RequestParam(value = "orderName") String orderName,
			@RequestParam(value = "idString") String idString,
			@RequestParam(value = "dataCloseTime") long dataCloseTime)
	{
		try
		{
			List<Integer> productIds = new ArrayList<Integer>();

			// setto la data odierna
			Calendar calendar = Calendar.getInstance();
			Date dateOpen = calendar.getTime();

			Date dateClose = new Date(dataCloseTime);

			String[] temp = idString.split(",");
			if (temp.length > 0)
			{
				for (int i = 0; i < temp.length; i++)
				{
					Integer id = Integer.valueOf(temp[i]);
					productIds.add(id);
				}
				return orderInterface.createOrder(
						(String) session.getAttribute("username"), idSupplier,
						productIds, orderName, dateOpen, dateClose);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOrdersStringNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<String> getOrdersString(HttpServletRequest request, HttpSession session)
	{
		return orderInterface.getOrdersNowString();
	}
	
	//FIXME : serve agli utenti normal o resp??
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductFromOrderNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductFromOrderNormal(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idOrderNorm") int idOrder)
	{
		try
		{
			return orderInterface.getProducts(idOrder,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getActivePurchaseNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getActivePurchaseNormal(HttpServletRequest request,
			HttpSession session)
	{
		return purchaseInterface.getPurchasesOnDate(
				(String) session.getAttribute("username"), 0);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setNewPurchaseNorm", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewPurchase(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrderNorm") int idOrder,
			@RequestParam(value = "idProducts") String idProducts,
			@RequestParam(value = "amountProducts") String amountProduct)
	{
		try
		{
			return purchaseInterface.setNewPurchase(idOrder, idProducts,
					amountProduct, (String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseDetailsNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getPurchaseDetails(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
	{
		try
		{
			return purchaseInterface.getPurchaseDetails(idPurchase,
					(String) session.getAttribute("username"));
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOldPurchaseNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getOldPurchase(HttpServletRequest request,
			HttpSession session)
	{
		return purchaseInterface.getPurchasesOnDate(
				(String) session.getAttribute("username"), -1);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getAmountfromPurchaseNorm", method = RequestMethod.POST)
	public @ResponseBody
	Integer getAmountfromPurchaseNorm(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct)
	{
		return purchaseInterface.getAmountPurchaseProductFromId(idPurchase,
				idProduct);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProgressOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	Float getProgressOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder)
	{
		return orderInterface.getProgress(idOrder);
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getTotBought", method = RequestMethod.POST)
	public @ResponseBody
	List<Integer> getTotBought(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "idProducts") List<Integer> idProducts)
	{
		return orderInterface.getBoughtAmounts(idOrder, idProducts);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getTotPurchaseCost", method = RequestMethod.POST)
	public @ResponseBody
	Float getTotPurchaseCost(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
	{
		try
		{
			return purchaseInterface
					.getPurchaseCost((String) session.getAttribute("username"),
							idPurchase, true);
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return (float) 0;
		}
	}
}
