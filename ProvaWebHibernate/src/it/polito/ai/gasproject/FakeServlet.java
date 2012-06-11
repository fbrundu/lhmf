package it.polito.ai.gasproject;

import it.polito.ai.lhmf.orm.MemberStatus;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Servlet implementation class FakeServlet
 */
@WebServlet("/*")
public class FakeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FakeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SessionFactory sf;
		try {
			Configuration configuration = new Configuration();
			configuration.configure();
			//configuration.addPackage("it.polito.ai.gasproject.orm");
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(
			configuration.getProperties()).buildServiceRegistry();
			sf = configuration.buildSessionFactory(serviceRegistry);
			} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
			}

		Session sess = sf.openSession();
		Transaction tx = null;
		try{
			tx = sess.beginTransaction();
			MemberStatus mailNotChecked = new MemberStatus(0);
			sess.saveOrUpdate(mailNotChecked);
			mailNotChecked.setDescription("registrered,address not verified");
			tx.commit();
			tx = null;
			tx = sess.beginTransaction();
			MemberStatus mailCheckedNotEnabled = new MemberStatus(1);
			sess.saveOrUpdate(mailCheckedNotEnabled);
			mailCheckedNotEnabled.setDescription("registrered,address verified,not enabled");
			tx.commit();
			tx = null;
			tx = sess.beginTransaction();
			MemberStatus userEnabled = new MemberStatus(2);
			sess.saveOrUpdate(userEnabled);
			userEnabled.setDescription("registrered,address verified,enabled");
			tx.commit();
		} catch(RuntimeException e){
			if(tx != null)
				tx.rollback();
			throw e;
		} finally{
			/*Esempio di query hql*/
			Query hqlQuery = sess.createQuery("from MemberStatus");
			Iterator it = hqlQuery.iterate();
			while(it.hasNext()){
				MemberStatus ms = (MemberStatus) it.next();
				System.out.println("MemberStatus: " + ms.getIdMemberStatus() + " '" + ms.getDescription() + "'");
			}
			sess.close();
		}
	}

}
