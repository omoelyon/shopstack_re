package com.shopstack.dao.businessuser;

import java.util.List;
import java.util.logging.Logger;

import com.shopstack.entities.business.Business;
import com.shopstack.entities.business.BusinessOutlet;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shopstack.entities.businessuser.BusinessUser;


/**
 * @author oluwatobi
 *
 */
@Repository
public class BusinessUserDaoImpl implements BusinessUserDao{

	
	private Logger logger = Logger.getLogger(getClass().getName());

	@Autowired
	private SessionFactory sessionFactory;
	


	@Override
	@Transactional
	public List<BusinessUser> getUsers() {
		
		List<BusinessUser> resultList = null;
	
		Session currentSession = sessionFactory.getCurrentSession();
		
		try {
		
			Query<BusinessUser> query = currentSession.createQuery("from User", BusinessUser.class);

			resultList = query.getResultList();
		}
		catch(Exception exe) {
			
			resultList = null;

		}
		
			return resultList;
			
	}

	@Override
	@Transactional
	public void saveUser(BusinessUser theUser) {
			
		Session currentSession = sessionFactory.getCurrentSession();
		
		if(theUser != null)
			currentSession.saveOrUpdate(theUser);
		else 
			throw new NullPointerException();
		
		
	}



	@Override
	@Transactional
	public BusinessUser loadUserById(int businessUserId) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		BusinessUser existingUser = (BusinessUser) currentSession
												.get(BusinessUser.class.getName(), businessUserId);
	
		return existingUser;
		
	}

	@Override
	@Transactional
	public BusinessUser loadUserByEmail(String email) {
			
		BusinessUser existingUser;
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("rawtypes")
		Query query = currentSession.createQuery("from BusinessUser u where u.email =: bizEmail");
		
		query.setParameter("bizEmail", email);
		
		try {
			existingUser = (BusinessUser) query.getResultList().get(0);

			for(Business business:existingUser.getBusiness()){
				Hibernate.initialize(business);
				Hibernate.initialize((business.getBizCategory()));
				Hibernate.initialize(business.getBizService());

					for(BusinessOutlet outlets: business.getBusinessOutlets()){
						Hibernate.initialize(outlets);
					}


			}


		}catch(RuntimeException runtimeEx) {
			
			logger.warning("Runtime exception while fetching user from the database \n" + runtimeEx);
			existingUser = null;
		}
		
		return existingUser;
	}

	@Override
	@Transactional
	public BusinessUser findByToken(String token) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		BusinessUser existingUser = null;
		
		@SuppressWarnings("rawtypes")
		Query query = currentSession.createQuery("from BusinessUser b where b.token =: token");
			query.setParameter("token", token);
		try {
			
			existingUser = (BusinessUser) query.getResultList().get(0);
			
		}catch(RuntimeException runtime) {
			
			logger.info("Threw runtime exception" + runtime);
			
			existingUser = null;
		}
		
		return existingUser;
	}

	
	
	

	
	
	
}
