package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.ProductCategory;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ProductCategoryInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newProductCategory(ProductCategory productCategory)
			throws InvalidParametersException
	{
		if (productCategory == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(
				productCategory);
	}

	@Transactional(readOnly = true)
	public ProductCategory getProductCategory(Integer idProductCategory)
			throws InvalidParametersException
	{
		if (idProductCategory == null)
			throw new InvalidParametersException();
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from ProductCategory "
						+ "where idProductCategory = :idProductCategory");
		query.setParameter("idProductCategory", idProductCategory);
		return (ProductCategory) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ProductCategory> getProductCategories()
	{
		return sessionFactory.getCurrentSession()
				.createQuery("from ProductCategory").list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer updateProductCategory(ProductCategory productCategory)
			throws InvalidParametersException
	{
		if (productCategory == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update ProductCategory set description = :description "
						+ " where idProductCategory = :idProductCategory");
		query.setParameter("description", productCategory.getDescription());
		query.setParameter("idProductCategory",
				productCategory.getIdProductCategory());

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer deleteProductCategory(Integer idProductCategory)
			throws InvalidParametersException
	{
		if (idProductCategory == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete from ProductCategory "
						+ "where idProductCategory = :idProductCategory");

		query.setParameter("idProductCategory", idProductCategory);

		return (Integer) query.executeUpdate();
	}
}
