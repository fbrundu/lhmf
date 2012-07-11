package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.orm.Log;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UserInterface {
	//The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public void adminAddMember(Member admin, Member member, int role){
		Log log = new Log();
		log.setLogtext("");
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public void adminAddSupplier(Member admin, Supplier supplier){
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public void adminEnableMember(Member admin, String username) throws InvalidParametersException{
		if(username == null)
			throw new InvalidParametersException();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Member where username = :username");
		query.setParameter("username", username);
		Member member = (Member) query.uniqueResult();
		if(member == null)
			throw new InvalidParametersException();
		else{
			if(member.getMemberStatus().getIdMemberStatus() == MemberStatuses.VERIFIED_DISABLED){
				member.setMemberStatus((MemberStatus)session.get(MemberStatus.class, MemberStatuses.ENABLED));
				Log log = new Log();
				log.setMember(admin);
				log.setLogtext("Admin enabled member '" + username + "'");
				session.save(log);
			}
		}
	}
	
	/* TODO se i supplier li aggiunge solo l'admin, è necessario abilitarli in un secondo momento???
	@Transactional(propagation = Propagation.REQUIRED)
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public void adminEnableSupplier(Member admin, ){
		
	}
	*/
	
	@Transactional(propagation = Propagation.REQUIRED)
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public void adminRemoveMember(Member admin, String username) throws InvalidParametersException{
		if(username == null)
			throw new InvalidParametersException();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Member where username = :username");
		query.setParameter("username", username);
		Member member = (Member) query.uniqueResult();
		if(member != null){
			session.delete(member);
			Log log = new Log();
			log.setMember(admin);
			log.setLogtext("Admin enabled member" + "id");
			session.save(log);
		}
		//TODO cosa succcede se l'utente eliminato è loggato???
	}
	
	public void adminUpgradeMember(Member admin, int newRoleId){
		
	}
}
