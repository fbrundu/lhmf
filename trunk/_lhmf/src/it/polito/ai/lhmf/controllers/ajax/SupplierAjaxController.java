package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SupplierAjaxController
{
	@Autowired
	private SupplierInterface supplierInterface;

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/newsupplier", method = RequestMethod.POST)
	public @ResponseBody
	Integer newSupplier(HttpServletRequest request, @RequestBody Supplier supplier)
	{
		Integer idSupplier = -1;
		try
		{
			idSupplier = supplierInterface.newSupplier(supplier);
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
		}
		return idSupplier;
	}

	@RequestMapping(value = "/ajax/getsupplier", method = RequestMethod.GET)
	public @ResponseBody
	Supplier getSupplier(HttpServletRequest request,
			@RequestBody Integer idSupplier)
	{
		Supplier supplier = null;
		try
		{
			supplier = supplierInterface.getSupplier(idSupplier);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return supplier;
	}

	@RequestMapping(value = "/ajax/getsuppliers", method = RequestMethod.GET)
	public @ResponseBody
	List<Supplier> getSuppliers(HttpServletRequest request)
	{
		List<Supplier> suppliersList = null;
		try
		{
			suppliersList = supplierInterface.getSuppliers();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return suppliersList;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/updatesupplier", method = RequestMethod.POST)
	public @ResponseBody
	Integer updateSupplier(HttpServletRequest request,
			@RequestBody Supplier supplier)
	{
		Integer rowsAffected = -1;
		try
		{
			rowsAffected = supplierInterface.updateSupplier(supplier);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return rowsAffected;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/deletesupplier", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteSupplier(HttpServletRequest request,
			@RequestBody Integer idSupplier)
	{
		Integer rowsAffected = -1;
		try
		{
			rowsAffected = supplierInterface.deleteSupplier(idSupplier);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return rowsAffected;
	}
}
