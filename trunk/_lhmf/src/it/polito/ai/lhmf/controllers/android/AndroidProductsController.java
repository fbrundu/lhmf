package it.polito.ai.lhmf.controllers.android;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.ProductCategoryInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.ProductCategory;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

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
			@RequestParam(value = "minBuy", required = true) int minBuy,
			@RequestParam(value = "maxBuy", required = true) int maxBuy,
			@RequestParam(value = "productCategory", required = true) int idProductCategory,
			@RequestParam(value = "picture", required=false) MultipartFile picture )
			throws InvalidParametersException {
		Integer idProduct = -1;
		Supplier s = supplierInterface.getSupplier(principal.getName());
		ProductCategory pc = productCategoryInterface
				.getProductCategory(idProductCategory);
		if (s != null && pc != null && !productName.equals("")
				&& !productDescription.equals("") && productDimension > 0
				&& !measureUnit.equals("") && unitBlock > 0
				&& transportCost > 0 && unitCost > 0 && minBuy > 0
				&& maxBuy >= minBuy)
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
			if(picture != null){
				ServletContext context = request.getServletContext();
				String path = context.getRealPath("/img/prd/");
				
				idProduct = productInterface.newProduct(p, picture, path);
			}
			else
				idProduct = productInterface.newProduct(p);
		}
		return idProduct;
	}
}
