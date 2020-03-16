package com.demo.hibernate.entities.many2onebothway;

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
		customer.setCustomerName("DD");
		
		Order order1 = new Order();
		order1.setOrderName("order-3");
		
		Order order2 = new Order();
		order2.setOrderName("order-4");
		
		Order order3 = new Order();
		order3.setOrderName("order-5");
		
		//�趨 ������ϵ
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		order3.setCustomer(customer);
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		customer.getOrders().add(order3);
		
		// ִ�� save ������
		//
		// (1) �Ȳ��� Customer�� �ٲ��� Order, �� 3�� INSERT ��䣬 1�� UPDATE ��䡣
		// ��Ϊ 1��һ�� �� n��һ�� ��ά��������ϵ�����Ի��� UPDATE ��䡣
		// ���⣬������ 1��һ�� �� set�ڵ� ָ�� "inverse=true"����ʹ1��һ�� ����ά��������ϵ��
		// ��ʱ�򣬾�ֻ��Ҫ 3�� INSERT ��䡣
		// ��ˣ������趨 set�ڵ�� inverse=true�������Ȳ���1��һ�ˣ������n��һ�ˡ�
		// �ô��ǣ������� UPDATE����䡣
		session.save(customer);		
//		session.save(order1);
//		session.save(order2);
		
	
		// (2) �Ȳ��� Order���ٲ��� Customer, �� 3�� INSERT ��䣬3�� UPDATE ���
//		session.save(order1);
//		session.save(order2);
//		session.save(customer);	
	}
	
	
	/*
	 * ��ѯ ����
	 */
	@Test
	public void testOne2ManyGet() {
		// 1. �� n��һ�� �ļ��� ʹ�� �ӳټ���
		Customer customer = session.get(Customer.class, 8);
		System.out.println(customer.getCustomerName());
		
		// 2. ���ص� n��һ�� �ļ����� Hibernate ���õļ������� �����磺 org.hibernate.collection.internal.PersistentSet����
		// �ü��Ͼ��� �ӳټ��� �� ��š�������󡱵Ĺ��ܡ�
		System.out.println(customer.getOrders().getClass());
		
		// 3. ���ܻ��׳� LazyInitializationException �쳣��
//		session.close();
		System.out.println(customer.getOrders().size());
		
		// 4. ����Ҫʹ�ü����е�Ԫ�ص�ʱ�򣬽��г�ʼ����
		
	}
	
	
	/**
	 * ���� ����
	 */
	@Test
	public void testUpdate() {
		Customer customer = session.get(Customer.class, 1);
		customer.getOrders().iterator().next().setOrderName("GGG");
	}
	
	
	/**
	 * ɾ�� ����
	 */
	@Test
	public void testMany2OneDelete() {
		// �ڲ��趨 ��������ϵ�� ������£����� 1��һ�� �Ķ��� ���� n�Ķ��������ã�����ֱ��ɾ�� 1��һ�˵Ķ��󡣻��׳��쳣��
		Customer customer = session.get(Customer.class, 4);
		session.delete(customer);
	}
	
	
	/**
	 * ���� �������� cascade
	 */
	@Test
	public void testCascade() {
		Customer customer = session.get(Customer.class, 7);
		customer.getOrders().clear();		
	}
		
}