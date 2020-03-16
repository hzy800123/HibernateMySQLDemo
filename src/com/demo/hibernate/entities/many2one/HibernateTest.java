package com.demo.hibernate.entities.many2one;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest {

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
	

	/*
	 * ���� ����
	 */
	@Test
	public void testMany2OneSave() {
		Customer customer = new Customer();
		customer.setCustomerName("BB");
		
		Order order1 = new Order();
		order1.setOrderName("order-3");
		
		Order order2 = new Order();
		order2.setOrderName("order-4");
		
		//�趨 ������ϵ
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//ִ�� save ������
		//
		// (1) �Ȳ��� Customer�� �ٲ��� Order, �� 3�� INSERT ���
		// �Ȳ��� 1 ��һ�ˣ��ٲ��� n ��һ�ˣ�ֻ�� INSERT ��䡣
//		session.save(customer);		
//		session.save(order1);
//		session.save(order2);
		
		// (2) �Ȳ��� Order���ٲ��� Customer, �� 3�� INSERT ��䣬2�� UPDATE ���
		// �Ȳ��� n��һ�ˣ��ٲ��� 1��һ�ˣ����� UPDATE ��䣡
		// ��Ϊ�ڲ��� n��һ��ʱ���޷�ȷ�� 1��һ�˵� ���ֵ������ֻ�ܵ� 1��һ�˲�����ٶ��ⷢ�� UPDATE ������n��һ�˵�ֵ��
		// ��ˣ��Ƽ��Ȳ��� 1��һ�ˣ������ n��һ�ˡ�
		session.save(order1);
		session.save(order2);
		session.save(customer);	
	}
	
	
	/*
	 * ��ѯ ����
	 */
	@Test
	public void testMany2OneGet() {
		// 1. ����ѯ�� n��һ�˵�һ��������Ĭ������£�ֻ��ѯ��n��һ�˵Ķ��󣬶�û�в�ѯ������1��һ�˵Ķ���
		Order order = session.get(Order.class, 1);
		System.out.println(order.getOrderName());

		System.out.println(order.getCustomer().getClass().getName());
		
//		session.close();
		
		// 2. ����Ҫʹ�õ������Ķ���ʱ���ŷ��Ͷ�Ӧ�Ĳ�ѯ SQL ��䡣
		Customer customer = order.getCustomer();
		System.out.println(customer.getCustomerName());
		
		// 3. �ڲ�ѯ Customer����ʱ���� n��һ�� ������ 1��һ��ʱ��
		// ����ʱ session �ѱ��رգ���Ĭ������£��ᷢ�� LazyInitializationException �쳣

		// 4. ��ȡ Order ����ʱ��Ĭ������£�������� Customer������һ�� ��������� ���ӳټ��أ���
		System.out.println(order.getCustomer().getClass().getName());
	}
	
	
	/**
	 * ���� ����
	 */
	@Test
	public void testMany2OneUpdate() {
		Order order = session.get(Order.class, 1);
		order.getCustomer().setCustomerName("CCC");
	}
	
	
	/**
	 * ɾ�� ����
	 */
	@Test
	public void testMany2OneDelete() {
		// �ڲ��趨 ��������ϵ�� ������£����� 1��һ�� �Ķ��� ���� n�Ķ��������ã�����ֱ��ɾ�� 1��һ�˵Ķ��󡣻��׳��쳣��
		Customer customer = session.get(Customer.class, 2);
		session.delete(customer);		
		
		// ��� ͬʱ ɾ�� 1��һ�� �� n��һ�� �Ķ�Ӧ�� ���󣬲ſ���ͬʱ�ɹ� ɾ����
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Order> cr = cb.createQuery(Order.class);
		Root<Order> root = cr.from(Order.class);
		Predicate predicate = cb.equal(root.get("customer"), 2);
		cr.select(root).where(predicate);		
		
		Query query = session.createQuery(cr);
		List<Order> results = query.getResultList();
		results.stream().forEach(System.out::println);
		
		results.forEach(session::delete);
	}		
}