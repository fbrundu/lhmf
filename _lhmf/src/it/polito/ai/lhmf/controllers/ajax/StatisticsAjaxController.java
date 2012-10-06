package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.StatisticsInterface;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.ArrayList;

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
public class StatisticsAjaxController
{
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private StatisticsInterface statisticsInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/statMemberType", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Float> statMemberType(HttpServletRequest request)
	{
		ArrayList<Float> respStat = new ArrayList<Float>();
		Float normalUsers = (float) (long) memberInterface.getNumberItems(MemberTypes.USER_NORMAL);
		Float respUsers = (float) (long) memberInterface.getNumberItems(MemberTypes.USER_RESP);
		Float supplierUsers = (float) (long) memberInterface.getNumberItems(MemberTypes.USER_SUPPLIER);
		Float totalUsers = (float) (long) memberInterface.getNumberItems(4)-1;
		
		normalUsers = (float) (normalUsers/totalUsers)*100;
		respUsers = (respUsers/totalUsers)*100;
		supplierUsers = ((supplierUsers/totalUsers)*100);
		
		respStat.add(normalUsers);
		respStat.add(respUsers);
		respStat.add(supplierUsers);

		return respStat;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/statMemberReg", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Integer> statMemberReg(HttpServletRequest request,
			@RequestParam(value = "year", required = true) int year)
	{
		ArrayList<Integer> respStat = new ArrayList<Integer>();
		
		Integer normalUsers;
		Integer respUsers;
		Integer supplierUsers;
		
		for(int i = 0; i <= 11; i++) {
			
			normalUsers = (int) (long) memberInterface.getNumberItemsPerMonth(MemberTypes.USER_NORMAL, i, year);
			respUsers = (int) (long) memberInterface.getNumberItemsPerMonth(MemberTypes.USER_RESP, i, year);
			supplierUsers = (int) (long) memberInterface.getNumberItemsPerMonth(MemberTypes.USER_SUPPLIER, i, year);
			
			respStat.add(normalUsers);
			respStat.add(respUsers);
			respStat.add(supplierUsers);
		}
		
		return respStat;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierMoneyMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Float> statSupplierMoneyMonth(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "year", required = true) int year)
	{
		return statisticsInterface.getSupplierMoneyMonth(
				(String) session.getAttribute("username"), year);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierMoneyProduct", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> statSupplierMoneyProduct(HttpServletRequest request,
			HttpSession session)
	{
		return statisticsInterface.supplierMoneyProduct((String) session
				.getAttribute("username"));
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierProductList", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Double> statSupplierProductList(HttpServletRequest request,
			HttpSession session)
	{
		return statisticsInterface.supplierProductList((String) session
				.getAttribute("username"));
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierOrderMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Integer> statSupplierOrderMonth(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "year", required = true) int year)
	{
		return statisticsInterface.supplierOrderMonth(
				(String) session.getAttribute("username"), year);
	}
		
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierOrderYear", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Float> statSupplierOrderYear(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "year", required = true) int year)
	{
		return statisticsInterface.supplierOrderYear(
				(String) session.getAttribute("username"), year);
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statRespOrderMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Integer> statRespOrderMonth(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idSupplier", required = true) int idSupplier,
			@RequestParam(value = "year", required = true) int year)
	{
		return statisticsInterface.respOrderMonth(idSupplier, year);
	}
		
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statRespOrderYear", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Float> statRespOrderYear(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idSupplier", required = true) int idSupplier,
			@RequestParam(value = "year", required = true) int year				) 
	{
		return statisticsInterface.respOrderYear(idSupplier, year);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statRespTopUsers", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> statRespTopUsers(HttpServletRequest request,
			HttpSession session)
	{
		return statisticsInterface.respTopUsers((String) session
				.getAttribute("username"));
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statRespMoneyMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Double> statRespMoneyMonth(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "year", required = true) int year)
	{
		return statisticsInterface.respMoneyMonth(
				(String) session.getAttribute("username"), year);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/statNormMoneyMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Double> statNormMoneyMonth(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "year", required = true) int year)
	{
		return statisticsInterface.normMoneyMonth(
				(String) session.getAttribute("username"), year);
	}
	
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL + ", "
			+ MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statProdTopProduct", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> statProdTopProduct(HttpServletRequest request,
			HttpSession session)
	{
		return statisticsInterface.prodTopProduct();
	}
}
