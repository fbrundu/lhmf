package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.ProductCategoryInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.ProductCategory;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AndroidProductsController {
	@Autowired
	private ProductInterface productInterface;
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private ProductCategoryInterface productCategoryInterface;
	@Autowired
	private SupplierInterface supplierInterface;
	
	@RequestMapping(value = "/androidApi/getproductcategories", method = RequestMethod.GET)
	public @ResponseBody
	List<ProductCategory> getProductCategories(HttpServletRequest request)
	{
		return productCategoryInterface.getProductCategories();
	}
	
	@RequestMapping(value = "/androidApi/getproduct", method = RequestMethod.GET)
	public @ResponseBody
	Product getProduct(
			HttpServletRequest request,
			Principal principal,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
	{
		try
		{
			return productInterface.getProduct(idProduct, principal.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.SUPPLIER
			+ ", " + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/androidApi/newproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProductCategory(HttpServletRequest request,
			@RequestParam(value = "description", required = true) String description)
	{
		try
		{
			return productCategoryInterface
					.newProductCategory(description);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/androidApi/newproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProduct(
			HttpServletRequest request,
			Principal principal,
			@RequestParam(value = "productName", required = true) String productName,
			@RequestParam(value = "productDescription", required = true) String productDescription,
			@RequestParam(value = "productDimension", required = true) int productDimension,
			@RequestParam(value = "measureUnit", required = true) String measureUnit,
			@RequestParam(value = "unitBlock", required = true) int unitBlock,
			@RequestParam(value = "transportCost", required = true) float transportCost,
			@RequestParam(value = "unitCost", required = true) float unitCost,
			@RequestParam(value = "minBuy", required = false) Integer minBuy,
			@RequestParam(value = "maxBuy", required = false) Integer maxBuy,
			@RequestParam(value = "productCategory", required = true) int idProductCategory )
	{
		try
		{
			return productInterface.newProduct(principal.getName(), productName, productDescription,
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
	@RequestMapping(value = "/androidApi/newproductwithpicture", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProductWithPicture(
			HttpServletRequest request,
			Principal principal,
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
			@RequestParam(value = "picture", required=true) MultipartFile picture )
	{
		try
		{
			if (!picture.getContentType().matches("image/.*"))
				return -1;
			return productInterface.newProduct(principal.getName(), productName, productDescription,
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
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/androidApi/getmyproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getMyProducts(HttpServletRequest request, Principal principal)
	{
		return productInterface.getProductsBySupplier(principal.getName());
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/androidApi/getsupplierproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getSupplierProducts(HttpServletRequest request, Principal principal,
			@RequestParam(value="idSupplier") Integer idSupplier)
	{
		List<Product> ret = new ArrayList<Product>();
		String username = principal.getName();
		Member memberResp = memberInterface.getMember(username);
		
		Supplier supp = supplierInterface.getSupplier(idSupplier);
		
		if(memberResp == null || supp == null)
			return ret;
		
		if(supp.getMemberByIdMemberResp().getIdMember() != memberResp.getIdMember())
			return ret;
		
		List<Product> productsList = null;
		productsList = productInterface.getProductsBySupplier(principal.getName());
		
		for(Product p : productsList)
			if(p.isAvailability() == true)
				ret.add(p);
		
		return ret;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/androidApi/setproductavailable", method = RequestMethod.GET)
	public @ResponseBody
	Integer setProductAvailable(
			HttpServletRequest request,
			Principal principal,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
	{
		try
		{
			return productInterface.setProductAvailable(idProduct,
					principal.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/androidApi/setproductunavailable", method = RequestMethod.GET)
	public @ResponseBody
	Integer setProductUnavailable(
			HttpServletRequest request,
			Principal principal,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
	{
		try
		{
			return productInterface.setProductUnavailable(idProduct,
					principal.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
