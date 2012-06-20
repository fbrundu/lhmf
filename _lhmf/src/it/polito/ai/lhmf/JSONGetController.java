package it.polito.ai.lhmf;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import it.polito.ai.lhmf.exceptions.*;
import it.polito.ai.lhmf.model.*;
import it.polito.ai.lhmf.orm.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ajax")
public class JSONGetController
{
	@RequestMapping(value = "/getproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> JSONGetProducts(HttpServletRequest request)
	{
		List<Product> productList = null;
		try
		{
			productList = HibernateInterface
					.getProducts((org.hibernate.Session) request
							.getAttribute("hibernate_session"));
		}
		catch (NoHibernateSessionException e)
		{
			e.printStackTrace();
		}
		return productList;
	}
}
