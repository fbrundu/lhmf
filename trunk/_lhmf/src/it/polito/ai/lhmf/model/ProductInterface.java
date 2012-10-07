package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Notify;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.ProductCategory;
import it.polito.ai.lhmf.orm.PurchaseProduct;
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
import java.util.LinkedList;
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
	private PurchaseInterface purchaseInterface;
	private SupplierInterface supplierInterface;
	private ProductCategoryInterface productCategoryInterface;
	private MemberInterface memberInterface;
//	private MemberTypeInterface memberTypeInterface;
	private NotifyInterface notifyInterface;
	private LogInterface logInterface;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	public void setModelState(ModelState ms)
	{
		this.modelState = ms;
	}
	
	public void setPurchaseInterface(PurchaseInterface purchaseInterface)
	{
		this.purchaseInterface = purchaseInterface;
	}
	
	public void setSupplierInterface(SupplierInterface supplierInterface)
	{
		this.supplierInterface = supplierInterface;
	}
	
	public void setProductCategoryInterface(
			ProductCategoryInterface productCategoryInterface)
	{
		this.productCategoryInterface = productCategoryInterface;
	}

	public void setMemberInterface(MemberInterface memberInterface)
	{
		this.memberInterface = memberInterface;
	}
	
	public void setNotifyInterface(NotifyInterface notifyInterface)
	{
		this.notifyInterface = notifyInterface;
	}
	
	public void setLogInterface(LogInterface logInterface)
	{
		this.logInterface = logInterface;
	}
	
//	public void setMemberTypeInterface(MemberTypeInterface memberTypeInterface)
//	{
//		this.memberTypeInterface = memberTypeInterface;
//	}
	
	public boolean checkMinMaxBuy(Integer minBuy, Integer maxBuy)
	{
		return (minBuy == null && (maxBuy == null || maxBuy > 0))
				|| (minBuy > 0 && (maxBuy == null || maxBuy >= minBuy));
	}
	
//	@Transactional(propagation = Propagation.REQUIRED)
//	public Integer newProduct(Product product)
//			throws InvalidParametersException
//	{
//		Supplier s = supplierInterface.getSupplier((String) request
//				.getSession().getAttribute("username"));
//		ProductCategory pc = productCategoryInterface
//				.getProductCategory(idProductCategory);
//		{
//			Product p = new Product();
//			p.setName(productName);
//			p.setDescription(productDescription);
//			p.setDimension(productDimension);
//			p.setMeasureUnit(measureUnit);
//			p.setUnitBlock(unitBlock);
//			p.setTransportCost(transportCost);
//			p.setUnitCost(unitCost);
//			p.setMinBuy(minBuy);
//			p.setMaxBuy(maxBuy);
//			p.setAvailability(true);
//			p.setSupplier(s);
//			p.setProductCategory(pc);
//			idProduct = productInterface.newProduct(p);
//		}
//		return newProduct(product, null, null, null);
//	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newProduct(String username, String productName,
			String productDescription, int productDimension,
			String measureUnit, int unitBlock, float transportCost,
			float unitCost, Integer minBuy, Integer maxBuy,
			int idProductCategory, MultipartFile picture, String serverPath,
			String pictureDirectoryPath) throws InvalidParametersException,
			IOException
	{
		Supplier s = supplierInterface.getSupplier(username);
		ProductCategory pc = productCategoryInterface
				.getProductCategory(idProductCategory);
		if (s == null || pc == null || productName.equals("")
				|| productDescription.equals("") || productDimension <= 0
				|| measureUnit.equals("") || unitBlock <= 0
				|| transportCost <= 0 || unitCost <= 0
				|| !checkMinMaxBuy(minBuy, maxBuy))
			throw new InvalidParametersException();

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

		Integer newProductId = (Integer) sessionFactory.getCurrentSession()
				.save(p);
		if (newProductId > 0)
		{
			modelState.setToReloadProducts(true);
			// Invia la notifica al responsabile
			Notify n = new Notify();
			n.setMember(s.getMemberByIdMemberResp());
			n.setIsReaded(false);
			// FIXME mettere costanti
			n.setNotifyCategory(1);
			n.setText(newProductId.toString());
			n.setNotifyTimestamp(new Date());
			notifyInterface.newNotify(n);
			// Crea log
			logInterface.createLog("Ha creato il prodotto con id: "
					+ newProductId, s.getIdMember());

			if (picture != null)
			{
				if (pictureDirectoryPath == null || serverPath == null)
					throw new InvalidParametersException();
				String fileName = picture.getOriginalFilename();
				String extension = null;
				int i = fileName.lastIndexOf('.');

				if (i > 0 && i < fileName.length() - 1)
					extension = fileName.substring(i + 1);

				String pictureFilePath = null;
				String pictureServerPath = null;
				if (extension == null)
				{
					pictureFilePath = pictureDirectoryPath + File.separator
							+ newProductId;
					pictureServerPath = serverPath + newProductId;
				}

				else
				{
					pictureFilePath = pictureDirectoryPath + File.separator
							+ newProductId + "." + extension;
					pictureServerPath = serverPath + newProductId + "."
							+ extension;
				}

				File f = new File(pictureFilePath);
				OutputStream writer = null;
				writer = new BufferedOutputStream(new FileOutputStream(f));
				writer.write(picture.getBytes());
				writer.flush();
				writer.close();
				p.setImgPath(pictureServerPath);
			}
		}
		else
			logInterface.createLog("Ha provato a creare un nuovo prodotto senza successo",
					s.getIdMember());
		return newProductId;
	}

	@Transactional(readOnly = true)
	public Product getProduct(Integer idProduct, String username)
			throws InvalidParametersException
	{
		if (idProduct == null || username == null)
			throw new InvalidParametersException();
		Member m = memberInterface.getMember(username);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Product " + "where idProduct = :idProduct");
		query.setParameter("idProduct", idProduct);
		Product p = (Product) query.uniqueResult();
		if (m.getMemberType().getIdMemberType() == MemberTypes.USER_ADMIN
				|| (m.getMemberType().getIdMemberType() == MemberTypes.USER_SUPPLIER && p
						.getSupplier().getIdMember() == m.getIdMember())
				|| (m.getMemberType().getIdMemberType() == MemberTypes.USER_RESP && p
						.getSupplier().getMemberByIdMemberResp().getIdMember() == m
						.getIdMember())
				|| (m.getMemberType().getIdMemberType() == MemberTypes.USER_NORMAL))
			return p;
		else
			return null;
	}

	public Product getProduct(Integer idProduct)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Product " + "where idProduct = :idProduct");
		query.setParameter("idProduct", idProduct);
		return (Product) query.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public List<Product> getProductListPurchase(Integer idPurchase)
			throws InvalidParametersException
	{
		List<Product> lp = new ArrayList<Product>();
		for (PurchaseProduct p : purchaseInterface
				.getPurchaseProductsInternal(idPurchase))
		{
			lp.add(this.getProduct(p.getId().getIdProduct()));
		}
		return lp;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Product> getProducts()
	{
		return sessionFactory.getCurrentSession().createQuery("from Product")
				.list();
	}

	@Transactional(readOnly = true)
	public List<Product> getProductsBySupplier(String respUsername,
			Integer idSupplier)
	{
		List<Product> ret = new ArrayList<Product>();
		Member memberResp = memberInterface.getMember(respUsername);

		Supplier supp = supplierInterface.getSupplier(idSupplier);

		if (memberResp == null || supp == null)
			return ret;

		if (supp.getMemberByIdMemberResp().getIdMember() != memberResp
				.getIdMember())
			return ret;

		List<Product> productsList = null;
		productsList = getProductsBySupplier(idSupplier);

		for (Product p : productsList)
			if (p.isAvailability() == true)
				ret.add(p);

		return ret;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Product> getProductsBySupplier(String supplierUsername)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Product " + "where idSupplier = :idMember"
						+ " order by productCategory");
		query.setParameter("idMember",
				memberInterface.getMember(supplierUsername).getIdMember());
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Product> getProductsBySupplier(Integer idSupplier)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Product " + "where idSupplier = :idMember"
						+ " order by productCategory");
		query.setParameter("idMember", idSupplier);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ProductCategory> getProductCategoriesBySupplier(
			String usernameSupplier) throws InvalidParametersException
	{
		if (usernameSupplier == null)
			throw new InvalidParametersException();

		List<ProductCategory> pcl = new LinkedList<ProductCategory>();

		// TODO riscriverla in hql per una maggiore efficienza
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Product " + "where idSupplier = :idMember");
		query.setParameter("idMember",
				memberInterface.getMember(usernameSupplier));
		for (Product p : (List<Product>) query.list())
			if (!pcl.contains(p.getProductCategory()))
				pcl.add(p.getProductCategory());
		return pcl;
	}

	// NOT USED
//	@Transactional(propagation = Propagation.REQUIRED)
//	public Integer updateProduct(Product product)
//			throws InvalidParametersException
//	{
//		if (product == null)
//			throw new InvalidParametersException();
//
//		modelState.setToReloadProducts(true);
//
//		Query query = sessionFactory.getCurrentSession().createQuery(
//				"update Product " + "set name = :name,"
//						+ "description = :description,"
//						+ "dimension = :dimension,"
//						+ "measureUnit = :measureUnit,"
//						+ "unitBlock = :unitBlock,"
//						+ "availability = :availability,"
//						+ "transportCost = :transportCost,"
//						+ "unitCost = :unitCost," + "minBuy = :minBuy,"
//						+ "maxBuy = :maxBuy," + "idSupplier = :idSupplier,"
//						+ "idCategory = :idCategory "
//						+ "where idProduct = :idProduct");
//		query.setParameter("name", product.getName());
//		query.setParameter("description", product.getDescription());
//		query.setParameter("dimension", product.getDimension());
//		query.setParameter("measureUnit", product.getMeasureUnit());
//		query.setParameter("unitBlock", product.getUnitBlock());
//		query.setParameter("availability", product.isAvailability());
//		query.setParameter("transportCost", product.getTransportCost());
//		query.setParameter("unitCost", product.getUnitCost());
//		query.setParameter("minBuy", product.getMinBuy());
//		query.setParameter("maxBuy", product.getMaxBuy());
//		query.setParameter("idSupplier", product.getSupplier().getIdMember());
//		query.setParameter("idCategory", product.getProductCategory()
//				.getIdProductCategory());
//		query.setParameter("idProduct", product.getIdProduct());
//
//		return (Integer) query.executeUpdate();
//	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setProductAvailable(Integer idProduct, String username)
			throws InvalidParametersException
	{
		if (idProduct == null || idProduct <= 0 || username == null)
			throw new InvalidParametersException();

		Member m = memberInterface.getMember(username);
		Integer result = -1;
		if (m.getMemberType().getIdMemberType() == MemberTypes.USER_ADMIN)
			result = privateSetProductAvailable(idProduct);

		if (m.getMemberType().getIdMemberType() == MemberTypes.USER_SUPPLIER
				&& getProduct(idProduct).getSupplier().getIdMember() == m
						.getIdMember())
			result = privateSetProductAvailable(idProduct);
		
		if (result > 0)
		{
			Notify n = new Notify();
			n.setMember(getProduct(idProduct).getSupplier()
					.getMemberByIdMemberResp());
			n.setIsReaded(false);
			// FIXME mettere costanti
			n.setNotifyCategory(3);
			n.setText(idProduct.toString());
			n.setNotifyTimestamp(new Date());
			notifyInterface.newNotify(n);

			logInterface.createLog("Ha reso disponibile il prodotto con id: "
					+ idProduct, m.getIdMember());
		}
		else
			logInterface.createLog("Ha provato a rendere disponibile il prodotto con id: "
					+ idProduct + " senza successo", m.getIdMember());
		
		return result;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	private Integer privateSetProductAvailable(Integer idProduct)
	{
		modelState.setToReloadProducts(true);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Product " + "set availability = true "
						+ "where idProduct = :idProduct and availability = false");
		query.setParameter("idProduct", idProduct);

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setProductUnavailable(Integer idProduct, String username)
			throws InvalidParametersException
	{
		if (idProduct == null || idProduct <= 0 || username == null)
			throw new InvalidParametersException();

		Member m = memberInterface.getMember(username);
		Integer result = -1;
		if (m.getMemberType().getIdMemberType() == MemberTypes.USER_ADMIN)
			result = privateSetProductUnvailable(idProduct);

		if (m.getMemberType().getIdMemberType() == MemberTypes.USER_SUPPLIER
				&& getProduct(idProduct).getSupplier().getIdMember() == m
						.getIdMember())
			result = privateSetProductUnvailable(idProduct);

		if (result > 0)
		{
			Notify n = new Notify();
			n.setMember(getProduct(idProduct).getSupplier()
					.getMemberByIdMemberResp());
			n.setIsReaded(false);
			// FIXME mettere costanti
			n.setNotifyCategory(3);
			n.setText(idProduct.toString());
			n.setNotifyTimestamp(new Date());
			notifyInterface.newNotify(n);
	
			logInterface.createLog("Ha reso non disponibile il prodotto con id: "
					+ idProduct, m.getIdMember());
		}
		else
			logInterface.createLog("Ha provato senza successo a rendere non disponibile il prodotto con id: "
					+ idProduct, m.getIdMember());
		
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private Integer privateSetProductUnvailable(Integer idProduct)
	{
		modelState.setToReloadProducts(true);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Product " + "set availability = false "
						+ "where idProduct = :idProduct and availability = true");
		query.setParameter("idProduct", idProduct);

		return (Integer) query.executeUpdate();
	}
	
	// NOT USED
//	@Transactional(propagation = Propagation.REQUIRED)
//	public Integer deleteProduct(Integer idProduct, String username)
//			throws InvalidParametersException
//	{
//		if (idProduct == null)
//			throw new InvalidParametersException();
//
//		modelState.setToReloadProducts(true);
//
//		Member m = memberInterface.getMember(username);
//		
//		Query query = sessionFactory.getCurrentSession().createQuery(
//				"delete from Product " + "where idProduct = :idProduct");
//
//		query.setParameter("idProduct", idProduct);
//		Integer result = (Integer) query.executeUpdate();
//		
//		if(result > 0)
//			logInterface.createLog("Ha cancellato il prodotto con id: "
//					+ idProduct, m.getIdMember());
//		else
//			logInterface.createLog("Ha provato senza successo a cancellare il prodotto con id: "
//					+ idProduct, m.getIdMember());
//		
//		return result;
//	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Map<Product, Float> getProfitOnProducts(Supplier supplier)
	{

		Map<Product, Float> pList = new HashMap<Product, Float>();

		Calendar cal = Calendar.getInstance();
		Date endDate = cal.getTime();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"select pp.product, sum(pp.amount) "
						+ "from PurchaseProduct pp "
						+ "join pp.purchase as purchase "
						+ "join purchase.order as order "
						+ "where order.supplier = :supp "
						+ " AND order.dateClose <= :endDate "
						+ " AND order.dateDelivery is NOT NULL "
						+ "group by pp.product");

		query.setParameter("supp", supplier);
		query.setDate("endDate", endDate);
		Product tempProduct;
		Integer tempAmount;
		Float tempTot;

		for (Iterator it = query.iterate(); it.hasNext();)
		{
			Object[] row = (Object[]) it.next();

			tempProduct = (Product) row[0];
			tempAmount = (int) (long) row[1];

			tempTot = tempAmount * (tempProduct.getUnitCost());

			pList.put(tempProduct, tempTot);
		}

		return pList;

	}

	@Transactional(readOnly = true)
	public Long getNumberOfProductsBySupplier(Member memberSupplier)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select count(*) from Product "
						+ "where idSupplier = :idMember");
		query.setParameter("idMember", memberSupplier.getIdMember());
		return (Long) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Long getNumberOfProductsOnListBySupplier(Member memberSupplier)
	{
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select count(*) from Product "
								+ "where idSupplier = :idMember and availability = true");
		query.setParameter("idMember", memberSupplier.getIdMember());
		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Map<Product, Long> getTopProduct()
	{

		Map<Product, Long> pList = new HashMap<Product, Long>();

		Calendar cal = Calendar.getInstance();
		Date endDate = cal.getTime();

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select pp.product, sum(pp.amount)"
								+ "from PurchaseProduct as pp "
								+ "join pp.purchase as p "
								+ "join p.order as o "
								+ "where o.dateClose <= :endDate "
								+ " AND o.dateDelivery is NOT NULL "
								+ "group by pp.product ").setMaxResults(10);

		query.setDate("endDate", endDate);

		for (Iterator it = query.iterate(); it.hasNext();)
		{
			Object[] row = (Object[]) it.next();

			pList.put((Product) row[0], (Long) row[1]);
		}

		// Ordino la mappa
		List<Entry<Product, Long>> entries = new ArrayList<Entry<Product, Long>>(
				pList.entrySet());
		Collections.sort(entries, new Comparator<Entry<Product, Long>>() {
			public int compare(Entry<Product, Long> e1, Entry<Product, Long> e2)
			{
				return e1.getValue().compareTo(e2.getValue());
			}
		});
		Collections.reverse(entries);

		// Put entries back in an ordered map.
		Map<Product, Long> orderedMap = new LinkedHashMap<Product, Long>();
		for (Entry<Product, Long> entry : entries)
		{
			orderedMap.put(entry.getKey(), entry.getValue());
		}

		return orderedMap;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<PurchaseProduct> getPurchaseProductFromPurchase(Integer idPurchase)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from PurchaseProduct "
								+ "where idPurchase = :idPurchase");
		query.setParameter("idPurchase", idPurchase);
		return query.list();
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer checkFailed(Integer idProduct, int amount)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Product " + "where idProduct = :idProduct");

		query.setParameter("idProduct", idProduct);

		Product tmp = (Product) query.uniqueResult();
		
		if(amount >= tmp.getMinBuy())
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
}
