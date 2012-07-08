package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Product;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ProductInterface
{
	//The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer newProduct(Product product)
			throws InvalidParametersException
	{
		if (product == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(product);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Product> getProducts()
	{
		return sessionFactory.getCurrentSession().createQuery("from Product").list();
	}
}
