package it.polito.ai.lhmf.security;

import it.polito.ai.lhmf.exceptions.OpenIdNeedsRegistration;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberType;
import it.polito.ai.lhmf.orm.Supplier;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

public class MyUserDetailsService implements UserDetailsService, AuthenticationUserDetailsService<OpenIDAuthenticationToken>{
	public static class UserRoles{
		public static final String ADMIN = "ADMIN";
		public static final String RESP = "RESP";
		public static final String NORMAL = "NORMAL";
		public static final String SUPPLIER = "SUPPLIER";
	}
	//The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Member where username = :username");
		query.setParameter("username", username);
		List<Member> ret = query.list();
		if(ret.size() == 0){
			query = session.createQuery("from Supplier where username = :username");
			query.setParameter("username", username);
			List<Supplier> retS = query.list();
			if(retS.size() == 1){
				Supplier s = retS.get(0);
				List<SimpleGrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.SUPPLIER));
				return new User(s.getUsername(), s.getPassword(), s.isActive(), true, true, true, roles);
			}
			else
				throw new UsernameNotFoundException("Username not found!");
		}
		else{
			Member m = ret.get(0);
			List<SimpleGrantedAuthority> roles = null;
			MemberType type = m.getMemberType();
			if(type.getIdMemberType() == MemberTypes.USER_NORMAL)
				roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.NORMAL));
			else if(type.getIdMemberType() == MemberTypes.USER_RESP)
				roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.RESP));
			else if(type.getIdMemberType() == MemberTypes.USER_ADMIN)
				roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.ADMIN));
			//else
			//    errore
			boolean enabled = false;
			if(m.getMemberStatus().getIdMemberStatus() == MemberStatuses.ENABLED)
				enabled = true;
			return new User(m.getUsername(), m.getPassword(), enabled, true, true, true, roles);
		}
//		List<SimpleGrantedAuthority> roles = new ArrayList<SimpleGrantedAuthority>();
//		roles.add(new SimpleGrantedAuthority(UserRoles.NORMAL));
//		roles.add(new SimpleGrantedAuthority(UserRoles.RESP));
//		roles.add(new SimpleGrantedAuthority(UserRoles.ADMIN));
//		User user = new User("luca", "asf", true, true, true, true, roles);
//		return user;
	}

	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserDetails(OpenIDAuthenticationToken token)
			throws UsernameNotFoundException {
		String id = token.getIdentityUrl();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Member where username = :username");
		query.setParameter("username", id);
		Member m = (Member) query.uniqueResult();
		if(m == null)
			throw new OpenIdNeedsRegistration("open id login successfull but not registered", token);
		else{
			List<SimpleGrantedAuthority> roles = null;
			MemberType type = m.getMemberType();
			if(type.getIdMemberType() == MemberTypes.USER_NORMAL)
				roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.NORMAL));
			else if(type.getIdMemberType() == MemberTypes.USER_RESP)
				roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.RESP));
			else if(type.getIdMemberType() == MemberTypes.USER_ADMIN)
				roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.ADMIN));
			//else
			//    errore
			boolean enabled = false;
			if(m.getMemberStatus().getIdMemberStatus() == MemberStatuses.ENABLED)
				enabled = true;
			return new User(m.getUsername(), m.getPassword(), enabled, true, true, true, roles);
		}

//        CustomUserDetails user = registeredUsers.get(id);
//
//        if (user != null) {
//            return user;
//        }

//        String email = null;
//        String firstName = null;
//        String lastName = null;
//        String fullName = null;
//
//        List<OpenIDAttribute> attributes = token.getAttributes();
//
//        for (OpenIDAttribute attribute : attributes) {
//            if (attribute.getName().equals("email")) {
//                email = attribute.getValues().get(0);
//            }
//
//            if (attribute.getName().equals("firstname")) {
//                firstName = attribute.getValues().get(0);
//            }
//
//            if (attribute.getName().equals("lastname")) {
//                lastName = attribute.getValues().get(0);
//            }
//
//            if (attribute.getName().equals("fullname")) {
//                fullName = attribute.getValues().get(0);
//            }
//        }
//
//        if (fullName == null) {
//            StringBuilder fullNameBldr = new StringBuilder();
//
//            if (firstName != null) {
//                fullNameBldr.append(firstName);
//            }
//
//            if (lastName != null) {
//                fullNameBldr.append(" ").append(lastName);
//            }
//            fullName = fullNameBldr.toString();
//        }
//        User user = new User(id, "unused", AuthorityUtils.createAuthorityList(UserRoles.NORMAL));

//        user = new CustomUserDetails(id, DEFAULT_AUTHORITIES);
//        user.setEmail(email);
//        user.setName(fullName);
//
//        registeredUsers.put(id, user);
//
//        user = new CustomUserDetails(id, DEFAULT_AUTHORITIES);
//        user.setEmail(email);
//        user.setName(fullName);
//        user.setNewUser(true);

//        return user;
	}

}
