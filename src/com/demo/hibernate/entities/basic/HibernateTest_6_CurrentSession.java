package com.demo.hibernate.entities.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest_6_CurrentSession {

	private SessionFactory sessionFactory;
	private Session currentSession;
	private Session currentSession2;
	private Session currentSession3;
	private Transaction transaction;
	
	@Before
	public void init() {
		System.out.println("init...");
		StandardServiceRegistry standardServiceRegistry = 
				new StandardServiceRegistryBuilder().configure().build();
		MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);
		Metadata metaData = metadataSources.getMetadataBuilder().build();
		sessionFactory = metaData.getSessionFactoryBuilder().build();
		
		currentSession = sessionFactory.getCurrentSession();
		currentSession2 = sessionFactory.getCurrentSession();
		currentSession3 = sessionFactory.getCurrentSession();
		
		System.out.println(currentSession.hashCode());
		System.out.println(currentSession2.hashCode());
		System.out.println(currentSession3.hashCode());
		
		transaction = currentSession.beginTransaction();
	}
	
	/**
	 * 若用openSession获取Session，那么在commit后，需要主动调用session.close()，目的是释放相关资源.
	 * 若用getCurrentSession()获取Session，那么commit或者rollback后，SessionFactory就会主动调用session.close()，不需要我们主动关闭.
	 * 
	 */
	@After
	public void destroy() {
		System.out.println("destroy...");
		transaction.commit();
//		currentSession.close();
		sessionFactory.close();
	}
	
	@Test
	public void testHibernateCriteria() {
		Criteria criteria = currentSession.createCriteria(News.class);
		List<News> list = new ArrayList<>();
		List<News> list2 = new ArrayList<>();
		List<News> list3 = new ArrayList<>();
		List<News> list4 = new ArrayList<>();
		List<News> list5 = new ArrayList<>();
		
		// 在 Hibernate 5.2 之前（e.g. 5.0 或者 5.1）：
		// 通过 criteria.add(criterion), 过滤 返回的结果集合.
		// 'Criterion' 的实例，可以通过 调用 'Restrictions'类的 静态方法 static methods 获得，
		// 例如：Restrictions.eq("author", "aaa")
		Criterion criterion1 = Restrictions.eq("author", "aaa");
		Criterion criterion2 = Restrictions.gt("id", 6);
		
		// 另外，可以通过 调用 'Restrictions'类的 静态方法 static method - 'and' 来获得 SQL语句的 'AND'的连接条件的效果，
		// 例如：Restrictions.and(criterion1, criterion2)
		Criterion criterion3 = Restrictions.and(criterion1, criterion2);
		criteria.add(criterion3);
		
		list = criteria.list();
		list.forEach(System.out::println);
		
		System.out.println(" ------ ");
	
		// 或者，也可以通过 调用 'Restrictions'类的 静态方法static method - 'conjunction()' 来获得SQL语句的 'AND'的连接条件的效果，
		// 例如以下三步：
		// Junction conjunction = Restrictions.conjunction();
		// conjunction.add(criterion1);
		// conjunction.add(criterion2);
		Criteria criteria2 = currentSession.createCriteria(News.class);
		Junction conjunction = Restrictions.conjunction();
		conjunction.add(criterion1);
		conjunction.add(criterion2);
		criteria2.add(conjunction);
		
		list2 = criteria2.list();
		list2.forEach(System.out::println);		
		
		System.out.println(" ------ ");
		
		// 在 Hibernate 5.2 以后：
		CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
		CriteriaQuery<News> criteriaQuery = criteriaBuilder.createQuery(News.class);
		Root<News> root = criteriaQuery.from(News.class);
		// 直接 查询
		Query query = currentSession.createQuery(criteriaQuery);
		list3 = query.getResultList();
		list3.forEach(System.out::println);
		
		System.out.println(" ------ ");
		
		// 条件 查询 1
		criteriaQuery.where(criteriaBuilder.like(root.get("author"), "%aa%"));
		Query query2 = currentSession.createQuery(criteriaQuery);
		list4 = query2.getResultList();
		list4.forEach(System.out::println);
		
		System.out.println(" ------ ");

		// 条件 查询 2
		criteriaQuery.where(criteriaBuilder.equal(root.get("author"), "bb2"));
		Query query3 = currentSession.createQuery(criteriaQuery);
		list4 = query3.getResultList();
		list4.forEach(System.out::println);
		
		System.out.println(" ------ ");
		
		// 插入 操作
		News news = new News("AAA11", "ccc", new Date());
		currentSession.save(news);
		currentSession.flush();
		currentSession.clear();

		System.out.println(" ------ ");
		
		// 直接 查询
		list5 = query.getResultList();
		list5.forEach(System.out::println);
	}
	
}
