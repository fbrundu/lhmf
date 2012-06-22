package it.polito.ai.lhmf.model;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.exceptions.NoHibernateSessionException;
import it.polito.ai.lhmf.orm.*;

public abstract class HibernateInterface
{
	public static Integer newProduct(Session hibernateSession, Product product)
			throws NoHibernateSessionException, InvalidParametersException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		if (product == null)
			throw new InvalidParametersException();

		return (Integer) hibernateSession.save(product);
	}

	@SuppressWarnings("unchecked")
	public static List<Product> getProducts(Session hibernateSession)
			throws NoHibernateSessionException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		return hibernateSession.createQuery("from Product").list();
	}

	public static Integer newProductCategory(Session hibernateSession,
			ProductCategory productCategory)
			throws NoHibernateSessionException, InvalidParametersException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		if (productCategory == null)
			throw new InvalidParametersException();

		return (Integer) hibernateSession.save(productCategory);
	}

	@SuppressWarnings("unchecked")
	public static List<ProductCategory> getProductCategories(
			Session hibernateSession) throws NoHibernateSessionException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		return hibernateSession.createQuery("from ProductCategory").list();
	}

	public static Integer updateProductCategory(Session hibernateSession,
			ProductCategory productCategory)
			throws NoHibernateSessionException, InvalidParametersException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		if (productCategory == null)
			throw new InvalidParametersException();

		Query query = hibernateSession
				.createQuery("update ProductCategory set description = :description"
						+ " where idProductCategory = :idProductCategory");
		query.setParameter("description", productCategory.getDescription());
		query.setParameter("idProductCategory",
				productCategory.getIdProductCategory());

		return (Integer) query.executeUpdate();
	}

	public static Integer deleteProductCategory(Session hibernateSession,
			Integer idProductCategory) throws NoHibernateSessionException,
			InvalidParametersException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		if (idProductCategory == null)
			throw new InvalidParametersException();

		Query query = hibernateSession
				.createQuery("delete from ProductCategory where idProductCategory = :idProductCategory");

		query.setString("idProductCategory", idProductCategory.toString());

		return (Integer) query.executeUpdate();
	}
}
