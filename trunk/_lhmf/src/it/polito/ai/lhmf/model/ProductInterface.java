package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Product;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
		return newProduct(product, null, null);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newProduct(Product product, MultipartFile picture, String pictureDirectoryPath) throws InvalidParametersException {
		Integer newProductId = -1;
		if (product == null)
			throw new InvalidParametersException();
		
		newProductId = (Integer) sessionFactory.getCurrentSession().save(product);
		
		if(picture != null){
			if(pictureDirectoryPath == null)
				throw new InvalidParametersException();
			String fileName = picture.getOriginalFilename();
			String extension = null;
			int i = fileName.lastIndexOf('.');

			if (i > 0 && i < fileName.length() - 1)
				extension = fileName.substring(i+1);

			if(extension == null)
				extension = "";
			
			String picturePath = pictureDirectoryPath + File.separator + newProductId + extension;
			
			File f = new File(picturePath);
			OutputStream writer = null;
			try {
				writer = new BufferedOutputStream(new FileOutputStream(f));
				writer.write(picture.getBytes());
				writer.flush();
				writer.close();
				product.setImgPath(picturePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return newProductId;
	}
	

	@Transactional(readOnly = true)
	public Product getProduct(Integer idProduct)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from Product " + "where idProduct = :idProduct");
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
	public List<Product> getProductsBySupplier(Member memberSupplier)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Product " + "where idSupplier = :idMember order by productCategory");
		query.setParameter("idMember", memberSupplier.getIdMember());
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
						+ "idSupplier = :idSupplier,"
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
		query.setParameter("idSupplier", product.getSupplier()
				.getIdMember());
		query.setParameter("idCategory", product.getProductCategory()
				.getIdProductCategory());
		query.setParameter("idProduct", product.getIdProduct());

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setProductAvailable(Integer idProduct)
			throws InvalidParametersException
	{
		if (idProduct == null || idProduct < 0)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Product " + "set availability = true "
						+ "where idProduct = :idProduct");
		query.setParameter("idProduct", idProduct);

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setProductUnavailable(Integer idProduct)
			throws InvalidParametersException
	{
		if (idProduct == null || idProduct < 0)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Product " + "set availability = false "
						+ "where idProduct = :idProduct");
		query.setParameter("idProduct", idProduct);

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
