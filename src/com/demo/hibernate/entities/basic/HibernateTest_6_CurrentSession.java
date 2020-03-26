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
	 * ����openSession��ȡSession����ô��commit����Ҫ��������session.close()��Ŀ�����ͷ������Դ.
	 * ����getCurrentSession()��ȡSession����ôcommit����rollback��SessionFactory�ͻ���������session.close()������Ҫ���������ر�.
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
		
		// �� Hibernate 5.2 ֮ǰ��e.g. 5.0 ���� 5.1����
		// ͨ�� criteria.add(criterion), ���� ���صĽ������.
		// 'Criterion' ��ʵ��������ͨ�� ���� 'Restrictions'��� ��̬���� static methods ��ã�
		// ���磺Restrictions.eq("author", "aaa")
		Criterion criterion1 = Restrictions.eq("author", "aaa");
		Criterion criterion2 = Restrictions.gt("id", 6);
		
		// ���⣬����ͨ�� ���� 'Restrictions'��� ��̬���� static method - 'and' ����� SQL���� 'AND'������������Ч����
		// ���磺Restrictions.and(criterion1, criterion2)
		Criterion criterion3 = Restrictions.and(criterion1, criterion2);
		criteria.add(criterion3);
		
		list = criteria.list();
		list.forEach(System.out::println);
		
		System.out.println(" ------ ");
	
		// ���ߣ�Ҳ����ͨ�� ���� 'Restrictions'��� ��̬����static method - 'conjunction()' �����SQL���� 'AND'������������Ч����
		// ��������������
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
		
		// �� Hibernate 5.2 �Ժ�
		CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
		CriteriaQuery<News> criteriaQuery = criteriaBuilder.createQuery(News.class);
		Root<News> root = criteriaQuery.from(News.class);
		// ֱ�� ��ѯ
		Query query = currentSession.createQuery(criteriaQuery);
		list3 = query.getResultList();
		list3.forEach(System.out::println);
		
		System.out.println(" ------ ");
		
		// ���� ��ѯ 1
		criteriaQuery.where(criteriaBuilder.like(root.get("author"), "%aa%"));
		Query query2 = currentSession.createQuery(criteriaQuery);
		list4 = query2.getResultList();
		list4.forEach(System.out::println);
		
		System.out.println(" ------ ");

		// ���� ��ѯ 2
		criteriaQuery.where(criteriaBuilder.equal(root.get("author"), "bb2"));
		Query query3 = currentSession.createQuery(criteriaQuery);
		list4 = query3.getResultList();
		list4.forEach(System.out::println);
		
		System.out.println(" ------ ");
		
		// ���� ����
		News news = new News("AAA11", "ccc", new Date());
		currentSession.save(news);
		currentSession.flush();
		currentSession.clear();

		System.out.println(" ------ ");
		
		// ֱ�� ��ѯ
		list5 = query.getResultList();
		list5.forEach(System.out::println);
	}
	
}
