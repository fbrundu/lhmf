package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.util.ModelState;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public class ProductInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	private ModelState modelState;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}
	
	public void setModelState(ModelState ms)
	{
		this.modelState = ms;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newProduct(Product product)
			throws InvalidParametersException
	{
		return newProduct(product, null, null, null);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newProduct(Product product, MultipartFile picture, String serverPath, String pictureDirectoryPath) throws InvalidParametersException {
		Integer newProductId = -1;
		if (product == null)
			throw new InvalidParametersException();
		
		newProductId = (Integer) sessionFactory.getCurrentSession().save(product);
		modelState.setToReloadProducts(true);
		
		if(picture != null){
			if(pictureDirectoryPath == null || serverPath == null)
				throw new InvalidParametersException();
			String fileName = picture.getOriginalFilename();
			String extension = null;
			int i = fileName.lastIndexOf('.');

			if (i > 0 && i < fileName.length() - 1)
				extension = fileName.substring(i+1);

			String pictureFilePath = null;
			String pictureServerPath = null;
			if(extension == null){
				pictureFilePath = pictureDirectoryPath + File.separator + newProductId;
				pictureServerPath = serverPath + newProductId;
			}
			
			else{
				pictureFilePath = pictureDirectoryPath + File.separator + newProductId + "." + extension;
				pictureServerPath = serverPath + newProductId + "." + extension;
			}
			
			File f = new File(pictureFilePath);
			OutputStream writer = null;
			try {
				writer = new BufferedOutputStream(new FileOutputStream(f));
				writer.write(picture.getBytes());
				writer.flush();
				writer.close();
				product.setImgPath(pictureServerPath);
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

		modelState.setToReloadProducts(true);
		
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

		modelState.setToReloadProducts(true);
		
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
		
		modelState.setToReloadProducts(true);
		
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

		modelState.setToReloadProducts(true);
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete from Product " + "where idProduct = :idProduct");

		query.setParameter("idProduct", idProduct);

		return (Integer) query.executeUpdate();
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Map<Product,Float> getProfitOnProducts(Supplier supplier) {
		
		Map<Product,Float> pList = new HashMap<Product,Float>();
		
		Calendar cal = Calendar.getInstance();
		Date endDate = cal.getTime();
		
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select pp.product, sum(pp.amount) " +
				"from PurchaseProduct pp " +
				  "join pp.purchase as purchase " +
				  "join purchase.order as order " +
				  "where order.supplier = :supp " +
				  " AND order.dateClose <= :endDate " +
				  " AND order.dateDelivery is NOT NULL " +
				  "group by pp.product");
		
		query.setParameter("supp", supplier);
		query.setDate("endDate", endDate);
		Product tempProduct;
		Integer tempAmount;
		Float tempTot;
		
		for (Iterator it = query.iterate(); it.hasNext();) {
			Object[] row = (Object[]) it.next();
			
			tempProduct = (Product) row[0];
			tempAmount = (int) (long) row[1];
			
			tempTot =  tempAmount*(tempProduct.getUnitCost());
			
			
			pList.put(tempProduct, tempTot);
		}
		
		return pList;

	}

	@Transactional(readOnly = true)
	public Long getNumberOfProductsBySupplier(Member memberSupplier) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select count(*) from Product " + "where idSupplier = :idMember");
		query.setParameter("idMember", memberSupplier.getIdMember());
		return (Long) query.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public Long getNumberOfProductsOnListBySupplier(Member memberSupplier) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select count(*) from Product " + "where idSupplier = :idMember and availability = true");
		query.setParameter("idMember", memberSupplier.getIdMember());
		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Map<Product, Long> getTopProduct() {
		
		Map<Product, Long> pList = new HashMap<Product,Long>();
		
		Calendar cal = Calendar.getInstance();
		Date endDate = cal.getTime();
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select pp.product, sum(pp.amount)" +
				"from PurchaseProduct as pp " +
				"join pp.purchase as p " +
				"join p.order as o " +
			    "where o.dateClose <= :endDate " +
				" AND o.dateDelivery is NOT NULL " +
				"group by pp.product ").setMaxResults(10);
		
		query.setDate("endDate", endDate);
		
		for (Iterator it = query.iterate(); it.hasNext();) {
			Object[] row = (Object[]) it.next();
			
			pList.put((Product) row[0], (Long) row[1]);
		}
		
		//Ordino la mappa
		List<Entry<Product, Long>> entries = new ArrayList<Entry<Product, Long>>(pList.entrySet());
		Collections.sort(entries, new Comparator<Entry<Product, Long>>() {
		    public int compare(Entry<Product, Long> e1, Entry<Product, Long> e2) {
		        return e1.getValue().compareTo(e2.getValue());
		    }
		});
		Collections.reverse(entries);
		
		// Put entries back in an ordered map.
		Map<Product, Long> orderedMap = new LinkedHashMap<Product, Long>();
		for (Entry<Product, Long> entry : entries) {
		    orderedMap.put(entry.getKey(), entry.getValue());
		}	
		
		return orderedMap;
	}
}
