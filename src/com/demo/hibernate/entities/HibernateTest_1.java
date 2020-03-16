package com.demo.hibernate.entities;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.jdbc.Work;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest_1 {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;
	
	@Before
	public void init() {
		System.out.println("init...");
		StandardServiceRegistry standardServiceRegistry = 
				new StandardServiceRegistryBuilder().configure().build();
		MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);
		Metadata metaData = metadataSources.getMetadataBuilder().build();
		sessionFactory = metaData.getSessionFactoryBuilder().build();			
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();		
	}
	
	@After
	public void destroy() {
		System.out.println("destroy...");
		transaction.commit();		
		session.close();
		sessionFactory.close();
	}
	
	
	/**
	 * Session ������� ���� Hibernate Ӧ�ó���������ݿ�� Ƶ�ʡ�
	 */
	@Test
	public void testSessionCache() {
		News news = session.get(News.class, 2);
		System.out.println(news);
		
		News news2 = session.get(News.class, 2);
		System.out.println(news2);
	}
	
	
	/**
	 * flush: ʹ���ݱ��еļ�¼ �� Session �����еĶ����״̬����һ�¡�
	 *        Ϊ�˱���һ�£�����ܻᷢ�Ͷ�Ӧ�� SQL ��䡣
	 * 1. �ڵ��� Transaction.commit() �ķ����У�
	 *    �ȵ��� Session �� flush �������� commit �ύ����
	 * 2. flush() �������ܻ� ���� SQL ��䣬���ǲ����ύ����
	 * 
	 * 3. ע�⣺	��δ�ύ���� �� ��ʽ�ص��� session.flush() ����֮ǰ��Ҳ���ܻ���� flush() ������
	 *    ��1��ִ�� HQL �� QBC ��ѯ�����Ƚ��� flush() �������Եõ����ݱ�����µļ�¼��
	 *    ��2�� ����¼�� ID ���ɵײ����ݿ� ʹ�������ķ�ʽ���ɵģ����ڵ��� save() �����󣬾ͻ��������� Insert ��䡣
	 *        ��Ϊ save �����󣬱��뱣֤����� ID �Ǵ��ڵģ�
	 *        
	 * commit() ��  flush() ����������
	 * - flush ִ��һϵ�е� SQL��䣬�����ύ����
	 * - Commit ���� �ȵ��� flush ������Ȼ���ύ�����ύ������ζ�� �����ݿ���� ���ñ��������ˡ�
	 * 
	 */
	@Test
	public void testSessionFlush() {
		News news = session.get(News.class, 2);
		news.setAuthor("Oracle");
		
//		session.flush();
//		System.out.println("flush...");
		
		// ��ʹ�� QBC ��ѯ���������� ִ�� flush() �������ٲ�ѯ ���µļ�¼ ״̬��
		CriteriaQuery<News> criteria = session.getCriteriaBuilder().createQuery(News.class);
		Root<News> root = criteria.from(News.class);
		criteria.select(root);
		
		Query<News> query = session.createQuery(criteria);
		List<News> list = query.getResultList();
		System.out.println(list);		
	}
	
	@Test
	public void testSessionFlush2() {
		News news = new News("Java", "SUN4", new Date());
		session.save(news);
		
	}

	
	/**
	 * refresh(): ��ǿ�� ���͡�SELECT����䣬��ʹ��Session�����ж����״̬���͡����ݱ��ж�Ӧ�ļ�¼������һ�£�
	 */
	@Test
	public void testRefresh() {
		News news = session.get(News.class, 2);
		System.out.println(news);
		
		session.refresh(news);
		System.out.println(news);
	}
	
	
	/**
	 * clear(): ������
	 *          ������֮�󣬻�����û�иö���Hibernate ��Ҫ���·���һ�� Select SQL �����в�ѯ��
	 */
	@Test
	public void testClear() {
		News news = session.get(News.class, 2);
		
		session.clear();
		News news2 = session.get(News.class, 2);		
	}
}