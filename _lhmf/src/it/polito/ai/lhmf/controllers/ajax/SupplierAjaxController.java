package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SupplierAjaxController
{
	@Autowired
	private SupplierInterface supplierInterface;
	@Autowired
	private OrderInterface orderInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/newSupplier", method = RequestMethod.POST)
	public @ResponseBody
	List<String> newSupplier(
			HttpServletRequest request,
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "firstname", required = true) String firstname,
			@RequestParam(value = "lastname", required = true) String lastname,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "address", required = true) String address,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "state", required = true) String state,
			@RequestParam(value = "cap", required = true) String cap,
			@RequestParam(value = "phone", required = false, defaultValue = "not set") String phone,
			@RequestParam(value = "mType", required = false) int memberType,
			@RequestParam(value = "company", required = false) String company,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "contactName", required = false) String contactName,
			@RequestParam(value = "fax", required = false) String fax,
			@RequestParam(value = "website", required = false) String website,
			@RequestParam(value = "payMethod", required = true) String payMethod,
			@RequestParam(value = "idResp", required = false) int idResp)
	{
		return supplierInterface.newSupplier(username, firstname, lastname,
				email, address, city, state, cap, phone, memberType, company,
				description, contactName, fax, website, payMethod, idResp);
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getOrderSupplier", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getOrderSupplier(HttpServletRequest request,
			HttpSession session, @RequestParam(value = "start") long start)
	{
		return orderInterface.getOrdersBySupplier(start,
				(String) session.getAttribute("username"));
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getmyidsupplier", method = RequestMethod.GET)
	public @ResponseBody
	Integer getMyIdSupplier(HttpServletRequest request)
	{
		return supplierInterface.getIdSupplier((String) request
				.getAttribute("user"));
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getmesupplier", method = RequestMethod.GET)
	public @ResponseBody
	Supplier getMeSupplier(HttpServletRequest request)
	{
		return supplierInterface.getSupplier((String) request
				.getAttribute("user"));
	}
	
	// TODO get da chi può essere fatto?
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getsupplier", method = RequestMethod.GET)
	public @ResponseBody
	Supplier getSupplier(HttpServletRequest request,
			@RequestBody Integer idSupplier)
	{
		return supplierInterface.getSupplier(idSupplier);
	}

	// TODO get da chi può essere fatto?
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getsuppliers", method = RequestMethod.GET)
	public @ResponseBody
	List<Supplier> getSuppliers(HttpServletRequest request)
	{
		return supplierInterface.getSuppliers();
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/updatesupplier", method = RequestMethod.POST)
	public @ResponseBody
	Integer updateSupplier(HttpServletRequest request,
			@RequestBody Supplier supplier)
	{
		try
		{
			return supplierInterface.updateSupplier(supplier);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/deletesupplier", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteSupplier(HttpServletRequest request,
			@RequestBody Integer idSupplier)
	{
		try
		{
			return supplierInterface.deleteSupplier(idSupplier);
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getActiveOrderSupplier", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getActiveOrderSupplier(HttpServletRequest request, HttpSession session)
	{
		try
		{
			return orderInterface.getActiveOrdersForSupplier((String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getCompleteOrderSupplier", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getCompleteOrderSupplier(HttpServletRequest request, HttpSession session)
	{
		try
		{
			return orderInterface.getCompletedOrdersForSupplier((String) session.getAttribute("username"));
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/isorderfailed", method = RequestMethod.GET)
	public @ResponseBody
	Boolean isOrderFailed(HttpServletRequest request,
			@RequestParam(value = "idOrder", required = true) Integer idOrder)
	{
		return orderInterface.isFailed(idOrder);
	}
}
