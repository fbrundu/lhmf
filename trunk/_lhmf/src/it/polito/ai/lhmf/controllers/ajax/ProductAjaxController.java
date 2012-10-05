package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.security.MyUserDetailsService;

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
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProductAjaxController
{
	@Autowired
	private ProductInterface productInterface;
//	@Autowired
//	private ProductCategoryInterface productCategoryInterface;
//	@Autowired
//	private SupplierInterface supplierInterface;

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/newproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProduct(
			HttpServletRequest request,
			@RequestParam(value = "productName", required = true) String productName,
			@RequestParam(value = "productDescription", required = true) String productDescription,
			@RequestParam(value = "productDimension", required = true) int productDimension,
			@RequestParam(value = "measureUnit", required = true) String measureUnit,
			@RequestParam(value = "unitBlock", required = true) int unitBlock,
			@RequestParam(value = "transportCost", required = true) float transportCost,
			@RequestParam(value = "unitCost", required = true) float unitCost,
			@RequestParam(value = "minBuy", required = false) Integer minBuy,
			@RequestParam(value = "maxBuy", required = false) Integer maxBuy,
			@RequestParam(value = "productCategory", required = true) int idProductCategory)
	{
		try
		{
			return productInterface.newProduct((String) request.getSession()
					.getAttribute("username"), productName, productDescription,
					productDimension, measureUnit, unitBlock, transportCost,
					unitCost, minBuy, maxBuy, idProductCategory, null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/newproductwithpicture", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProductWithPicture(
			HttpServletRequest request,
			@RequestParam(value = "productName", required = true) String productName,
			@RequestParam(value = "productDescription", required = true) String productDescription,
			@RequestParam(value = "productDimension", required = true) int productDimension,
			@RequestParam(value = "measureUnit", required = true) String measureUnit,
			@RequestParam(value = "unitBlock", required = true) int unitBlock,
			@RequestParam(value = "transportCost", required = true) float transportCost,
			@RequestParam(value = "unitCost", required = true) float unitCost,
			@RequestParam(value = "minBuy", required = false) Integer minBuy,
			@RequestParam(value = "maxBuy", required = false) Integer maxBuy,
			@RequestParam(value = "productCategory", required = true) int idProductCategory,
			@RequestParam(value = "picture", required = true) MultipartFile picture)
	{
		try
		{
			if (!picture.getContentType().matches("image/.*"))
				return -1;
			return productInterface.newProduct((String) request.getSession()
					.getAttribute("username"), productName, productDescription,
					productDimension, measureUnit, unitBlock, transportCost,
					unitCost, minBuy, maxBuy, idProductCategory, picture,
					"img/prd/",
					request.getServletContext().getRealPath("img/prd/"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.SUPPLIER
			+ ", " + MyUserDetailsService.UserRoles.RESP + ", "
			+ MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getproduct", method = RequestMethod.GET)
	public @ResponseBody
	Product getProduct(
			HttpServletRequest request,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
	{
		try
		{
			return productInterface.getProduct(idProduct, (String) request
					.getSession().getAttribute("username"));
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getProducts(HttpServletRequest request)
	{
		List<Product> productsList = null;
		productsList = productInterface.getProducts();
		return productsList;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getmyproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getMyProducts(HttpServletRequest request, HttpSession session)
	{
		return productInterface.getProductsBySupplier((String) session
				.getAttribute("username"));
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.SUPPLIER
			+ ", " + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/setproductavailable", method = RequestMethod.GET)
	public @ResponseBody
	Integer setProductAvailable(
			HttpServletRequest request,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
	{
		try
		{
			return productInterface.setProductAvailable(idProduct,
					(String) request.getSession().getAttribute("username"));
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.SUPPLIER
			+ ", " + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/setproductunavailable", method = RequestMethod.GET)
	public @ResponseBody
	Integer setProductUnavailable(
			HttpServletRequest request,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
	{
		try
		{
			return productInterface.setProductUnavailable(idProduct,
					(String) request.getSession().getAttribute("username"));
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return -1;
		}
	}

//	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.SUPPLIER
//			+ ", " + MyUserDetailsService.UserRoles.ADMIN + "')")
//	@RequestMapping(value = "/ajax/updateproduct", method = RequestMethod.POST)
//	public @ResponseBody
//	Integer updateProduct(
//			HttpServletRequest request,
//			@RequestParam(value = "productId", required = true) Integer productId,
//			@RequestParam(value = "productName", required = true) String productName,
//			@RequestParam(value = "productDescription", required = true) String productDescription,
//			@RequestParam(value = "productDimension", required = true) int productDimension,
//			@RequestParam(value = "measureUnit", required = true) String measureUnit,
//			@RequestParam(value = "unitBlock", required = true) int unitBlock,
//			@RequestParam(value = "transportCost", required = true) float transportCost,
//			@RequestParam(value = "unitCost", required = true) float unitCost,
//			@RequestParam(value = "minBuy", required = true) int minBuy,
//			@RequestParam(value = "maxBuy", required = true) int maxBuy,
//			@RequestParam(value = "productCategory", required = true) int idProductCategory)
//			throws InvalidParametersException
//	{
//		Integer rowsAffected = -1;
//		ProductCategory pc = productCategoryInterface
//				.getProductCategory(idProductCategory);
//		Product p = productInterface.getProduct(productId);
//		if (p != null)
//		{
//			if (((Integer) request.getSession().getAttribute("member_type")) == MemberTypes.USER_SUPPLIER)
//			{
//				Supplier s = supplierInterface.getSupplier((String) request
//						.getSession().getAttribute("username"));
//				if (s == null
//						|| p.getSupplier().getIdMember() != s.getIdMember())
//					return rowsAffected;
//			}
//
//			if (pc != null && !productName.equals("")
//					&& !productDescription.equals("") && productDimension > 0
//					&& !measureUnit.equals("") && unitBlock > 0
//					&& transportCost > 0 && unitCost > 0 && minBuy > 0
//					&& maxBuy >= minBuy)
//			{
//				p.setName(productName);
//				p.setDescription(productDescription);
//				p.setDimension(productDimension);
//				p.setMeasureUnit(measureUnit);
//				p.setUnitBlock(unitBlock);
//				p.setTransportCost(transportCost);
//				p.setUnitCost(unitCost);
//				p.setMinBuy(minBuy);
//				p.setMaxBuy(maxBuy);
//				p.setProductCategory(pc);
//				rowsAffected = productInterface.updateProduct(p);
//			}
//		}
//		return rowsAffected;
//	}
//
//	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.SUPPLIER
//			+ ", " + MyUserDetailsService.UserRoles.ADMIN + "')")
//	@RequestMapping(value = "/ajax/deleteproduct", method = RequestMethod.POST)
//	public @ResponseBody
//	Integer deleteProduct(
//			HttpServletRequest request,
//			@RequestParam(value = "idProduct", required = true) Integer idProduct)
//			throws InvalidParametersException
//	{
//		Integer rowsAffected = -1;
//		if (idProduct != null && idProduct > 0)
//		{
//			Product p = productInterface.getProduct(idProduct);
//			if (p != null)
//			{
//				if (((Integer) request.getSession().getAttribute("member_type")) == MemberTypes.USER_SUPPLIER)
//				{
//					Supplier s = supplierInterface.getSupplier((String) request
//							.getSession().getAttribute("username"));
//					if (s == null
//							|| p.getSupplier().getIdMember() != s.getIdMember())
//						return rowsAffected;
//				}
//				rowsAffected = productInterface.deleteProduct(idProduct);
//			}
//		}
//		return rowsAffected;
//	}
}
