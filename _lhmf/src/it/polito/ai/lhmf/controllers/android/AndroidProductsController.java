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

import javax.servlet.ServletContext;
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
		List<ProductCategory> productCategoriesList = null;
		productCategoriesList = productCategoryInterface.getProductCategories();
		return productCategoriesList;
	}
	
	@RequestMapping(value = "/androidApi/getproduct", method = RequestMethod.GET)
	public @ResponseBody
	Product getProduct(HttpServletRequest request, @RequestParam(value = "idProduct", required = true) Integer idProduct){
		if (idProduct != null && idProduct > 0)
		{
			Product p = productInterface.getProduct(idProduct);

			return p;
		}
		return null;
	}
	
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.SUPPLIER
			+ ", " + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/androidApi/newproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProductCategory(HttpServletRequest request,
			@RequestParam(value = "description", required = true) String description)
			throws InvalidParametersException
	{
		Integer idProductCategory = -1;
		idProductCategory = productCategoryInterface
				.newProductCategory(description);
		return idProductCategory;
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
			throws InvalidParametersException {
		Integer idProduct = -1;
		Supplier s = supplierInterface.getSupplier(principal.getName());
		ProductCategory pc = productCategoryInterface
				.getProductCategory(idProductCategory);
		if (s != null && pc != null && !productName.equals("")
				&& !productDescription.equals("") && productDimension > 0
				&& !measureUnit.equals("") && unitBlock > 0
				&& transportCost > 0 && unitCost > 0 && checkMinMaxBuy(minBuy, maxBuy))
		{
			Product p = new Product();
			p.setName(productName);
			p.setDescription(productDescription);
			p.setDimension(productDimension);
			p.setMeasureUnit(measureUnit);
			p.setUnitBlock(unitBlock);
			p.setTransportCost(transportCost);
			p.setUnitCost(unitCost);
			p.setMinBuy(minBuy);
			p.setMaxBuy(maxBuy);
			p.setAvailability(true);
			p.setSupplier(s);
			p.setProductCategory(pc);
			idProduct = productInterface.newProduct(p);
		}
		return idProduct;
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
			throws InvalidParametersException {
		Integer idProduct = -1;
		Supplier s = supplierInterface.getSupplier(principal.getName());
		ProductCategory pc = productCategoryInterface
				.getProductCategory(idProductCategory);
		if (s != null && pc != null && !productName.equals("")
				&& !productDescription.equals("") && productDimension > 0
				&& !measureUnit.equals("") && unitBlock > 0
				&& transportCost > 0 && unitCost > 0 && checkMinMaxBuy(minBuy, maxBuy))
		{
			Product p = new Product();
			p.setName(productName);
			p.setDescription(productDescription);
			p.setDimension(productDimension);
			p.setMeasureUnit(measureUnit);
			p.setUnitBlock(unitBlock);
			p.setTransportCost(transportCost);
			p.setUnitCost(unitCost);
			p.setMinBuy(minBuy);
			p.setMaxBuy(maxBuy);
			p.setAvailability(true);
			p.setSupplier(s);
			p.setProductCategory(pc);
			
			ServletContext context = request.getServletContext();
			String serverPath = "img/prd/";
			String realPath = context.getRealPath(serverPath);
			
			idProduct = productInterface.newProduct(p, picture, serverPath, realPath);
		}
		return idProduct;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/androidApi/getmyproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getMyProducts(HttpServletRequest request, Principal principal)
	{
		String username = principal.getName();
		Member memberSupplier = memberInterface.getMember(username);
		
		List<Product> productsList = null;
		productsList = productInterface.getProductsBySupplier(memberSupplier);
		return productsList;
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
		productsList = productInterface.getProductsBySupplier(supp.getMemberByIdMember());
		
		for(Product p : productsList)
			if(p.isAvailability() == true)
				ret.add(p);
		
		return ret;
	}

	private boolean checkMinMaxBuy(Integer minBuy, Integer maxBuy) {
		return (minBuy == null && (maxBuy == null || maxBuy > 0)) ||
				(minBuy > 0 && (maxBuy == null || maxBuy >= minBuy));
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/androidApi/setproductavailable", method = RequestMethod.GET)
	public @ResponseBody
	Integer setProductAvailable(
			HttpServletRequest request,
			Principal principal,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
			throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		if (idProduct != null && idProduct > 0)
		{
			Product p = productInterface.getProduct(idProduct);
			if (p != null)
			{
				Supplier s = supplierInterface.getSupplier(principal.getName());
				if (s == null || p.getSupplier().getIdMember() != s.getIdMember())
					return rowsAffected;
				rowsAffected = productInterface.setProductAvailable(idProduct);
			}
		}
		return rowsAffected;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/androidApi/setproductunavailable", method = RequestMethod.GET)
	public @ResponseBody
	Integer setProductUnavailable(
			HttpServletRequest request,
			Principal principal,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
			throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		if (idProduct != null && idProduct > 0)
		{
			Product p = productInterface.getProduct(idProduct);
			if (p != null)
			{
				Supplier s = supplierInterface.getSupplier(principal.getName());
				if (s == null || p.getSupplier().getIdMember() != s.getIdMember())
					return rowsAffected;
				rowsAffected = productInterface.setProductUnavailable(idProduct);
			}
		}
		return rowsAffected;
	}
}
