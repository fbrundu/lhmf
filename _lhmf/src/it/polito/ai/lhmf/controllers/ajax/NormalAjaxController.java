package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.text.ParseException;
import java.util.ArrayList;
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
	private ProductInterface productInterface;

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getActivePurchase", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getActivePurchase(HttpServletRequest request,
			HttpSession session) throws InvalidParametersException
	{
		return purchaseInterface.getPurchasesOnDate(
				(String) session.getAttribute("username"), 0);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOldPurchase", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getOldPurchase(HttpServletRequest request,
			HttpSession session) throws InvalidParametersException
	{
		return purchaseInterface.getPurchasesOnDate(
				(String) session.getAttribute("username"), -1);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductFromOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductFromOrder(HttpServletRequest request,
			HttpSession session, @RequestParam(value = "idOrder") int idOrder)
			throws InvalidParametersException
	{
		return orderInterface.getProducts(idOrder);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductNormal", method = RequestMethod.POST)
	public @ResponseBody
	Product getProductNormal(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idProduct") int idProduct)
			throws InvalidParametersException
	{
		return productInterface.getProduct(idProduct);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseDetails", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getPurchaseDetails(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
			throws InvalidParametersException
	{
		return productInterface.getProductListPurchase(idPurchase);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOtherProductsOfPurchase", method = RequestMethod.POST)
	public @ResponseBody
	Set<Product> getOtherProductsOfPurchase(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
			throws InvalidParametersException
	{
		return purchaseInterface.getOtherProductsOfOrder(idPurchase);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOrdersString", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getOrdersString(HttpServletRequest request, HttpSession session)
	{
		try
		{
			return orderInterface.getAvailableOrders((String) session
					.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// Legacy
			return new ArrayList<Order>();
		}
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setNewPurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewPurchase(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "idProducts") String idProducts,
			@RequestParam(value = "amountProducts") String amountProducts)
			throws InvalidParametersException, ParseException
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
					(String) session.getAttribute("username"), idOrder, ids,
					amounts);
		}
		else
			return -1;
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getDispOfProduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer getDispOfProduct(HttpServletRequest request,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct)
			throws InvalidParametersException, ParseException
	{
		// -1 Disponibilita' infinita
		// 0 Non Disponibile
		// xx Quantita' disponibile
		return orderInterface.getDispProduct(idPurchase, idProduct);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getDispOfProductOrder", method = RequestMethod.POST)
	public @ResponseBody
	Integer getDispOfProductOrder(HttpServletRequest request,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "idProduct") int idProduct)
			throws InvalidParametersException, ParseException
	{
		// -1 Disponibilita' infinita
		// 0 Non Disponibile
		// xx Quantita' disponibile
		return orderInterface.getDispProductO(idOrder, idProduct);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/newPurchaseProduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newPurchaseProduct(HttpServletRequest request,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct,
			@RequestParam(value = "amount") int amountProduct)
			throws InvalidParametersException, ParseException
	{
		return purchaseInterface
				.insertProduct(
						(String) request.getSession().getAttribute("username"),
						idPurchase, idProduct, amountProduct);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/updatePurchaseProduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer updatePurchaseProduct(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct,
			@RequestParam(value = "amount") int amountProduct)
			throws InvalidParametersException, ParseException
	{

		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		// -1 = Quantita' non disponibile
		// -2 = valore non idoneo
		return purchaseInterface.updateProduct(
				(String) session.getAttribute("username"), idPurchase,
				idProduct, amountProduct);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/delPurchaseProduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer delPurchaseProduct(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct)
			throws InvalidParametersException, ParseException
	{
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		return purchaseInterface.deletePurchaseProduct(
				(String) session.getAttribute("username"), idPurchase,
				idProduct);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getAmountfromPurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer getAmountfromPurchase(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct)
			throws InvalidParametersException
	{
		return purchaseInterface.getPurchaseProductAmountFromId(idPurchase,
				idProduct);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProgressOrder", method = RequestMethod.POST)
	public @ResponseBody
	Float getProgressOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder)
			throws InvalidParametersException
	{
		return orderInterface.getProgress(idOrder);
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProgressProductOfOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<String> getProgressProductOfOrder(HttpServletRequest request,
			HttpSession session, @RequestParam(value = "idOrder") int idOrder)
			throws InvalidParametersException
	{
		return orderInterface.getProgressProduct(idOrder);
	}
}
