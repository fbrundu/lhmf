package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Supplier;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ProductInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newProduct(Product product)
			throws InvalidParametersException
	{
		if (product == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(product);
	}

	@Transactional(readOnly = true)
	public Product getProduct(Integer idProduct)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Product " + "where idProduct = :idProduct");
		query.setParameter("idProduct", idProduct);
		return (Product) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Product> getProducts()
	{
		return sessionFactory.getCurrentSession().createQuery("from Product")
				.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Product> getProductsBySupplier(String username)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select idMember " + "from Member "
						+ "where username = :username");
		query.setParameter("username", username);
		Integer idMember = (Integer) query.uniqueResult();
		if (idMember <= 0)
			return null;
		query = sessionFactory.getCurrentSession().createQuery(
				"from Product " + "where idSupplier = :idMember");
		query.setParameter("idMember", idMember);
		return query.list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer updateProduct(Product product)
			throws InvalidParametersException
	{
		if (product == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Product " + "set name = :name,"
						+ "description = :description,"
						+ "dimension = :dimension,"
						+ "measureUnit = :measureUnit,"
						+ "unitBlock = :unitBlock,"
						+ "availability = :availability,"
						+ "transportCost = :transportCost,"
						+ "unitCost = :unitCost," + "minBuy = :minBuy,"
						+ "maxBuy = :maxBuy,"
						+ "idMember_supplier = :idMember_supplier,"
						+ "idCategory = :idCategory "
						+ "where idProduct = :idProduct");
		query.setParameter("name", product.getName());
		query.setParameter("description", product.getDescription());
		query.setParameter("dimension", product.getDimension());
		query.setParameter("measureUnit", product.getMeasureUnit());
		query.setParameter("unitBlock", product.getUnitBlock());
		query.setParameter("availability", product.isAvailability());
		query.setParameter("transportCost", product.getTransportCost());
		query.setParameter("unitCost", product.getUnitCost());
		query.setParameter("minBuy", product.getMinBuy());
		query.setParameter("maxBuy", product.getMaxBuy());
		query.setParameter("idMember_supplier", product.getSupplier()
				.getIdMember());
		query.setParameter("idCategory", product.getProductCategory()
				.getIdProductCategory());
		query.setParameter("idProduct", product.getIdProduct());

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer deleteProduct(Integer idProduct)
			throws InvalidParametersException
	{
		if (idProduct == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete from Product " + "where idProduct = :idProduct");

		query.setParameter("idProduct", idProduct);

		return (Integer) query.executeUpdate();
	}
}
