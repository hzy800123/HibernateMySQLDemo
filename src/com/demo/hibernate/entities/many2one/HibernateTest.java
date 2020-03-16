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
	 * 插入 操作
	 */
	@Test
	public void testMany2OneSave() {
		Customer customer = new Customer();
		customer.setCustomerName("BB");
		
		Order order1 = new Order();
		order1.setOrderName("order-3");
		
		Order order2 = new Order();
		order2.setOrderName("order-4");
		
		//设定 关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行 save 操作：
		//
		// (1) 先插入 Customer， 再插入 Order, 有 3条 INSERT 语句
		// 先插入 1 的一端，再插入 n 的一端，只有 INSERT 语句。
//		session.save(customer);		
//		session.save(order1);
//		session.save(order2);
		
		// (2) 先插入 Order，再插入 Customer, 有 3条 INSERT 语句，2条 UPDATE 语句
		// 先插入 n的一端，再插入 1的一端，会多出 UPDATE 语句！
		// 因为在插入 n的一端时，无法确定 1的一端的 外键值，所以只能等 1的一端插入后，再额外发送 UPDATE 语句更新n的一端的值。
		// 因此，推荐先插入 1的一端，后插入 n的一端。
		session.save(order1);
		session.save(order2);
		session.save(customer);	
	}
	
	
	/*
	 * 查询 操作
	 */
	@Test
	public void testMany2OneGet() {
		// 1. 若查询的 n的一端的一个对象，则默认情况下，只查询了n的一端的对象，而没有查询关联的1的一端的对象。
		Order order = session.get(Order.class, 1);
		System.out.println(order.getOrderName());

		System.out.println(order.getCustomer().getClass().getName());
		
//		session.close();
		
		// 2. 在需要使用到关联的对象时，才发送对应的查询 SQL 语句。
		Customer customer = order.getCustomer();
		System.out.println(customer.getCustomerName());
		
		// 3. 在查询 Customer对象时，有 n的一端 导航到 1的一端时，
		// 若此时 session 已被关闭，则默认情况下，会发生 LazyInitializationException 异常

		// 4. 获取 Order 对象时，默认情况下，其关联的 Customer对象是一个 “代理对象” （延迟加载）！
		System.out.println(order.getCustomer().getClass().getName());
	}
	
	
	/**
	 * 更新 操作
	 */
	@Test
	public void testMany2OneUpdate() {
		Order order = session.get(Order.class, 1);
		order.getCustomer().setCustomerName("CCC");
	}
	
	
	/**
	 * 删除 操作
	 */
	@Test
	public void testMany2OneDelete() {
		// 在不设定 “级联关系” 的情况下，而且 1这一端 的对象 还有 n的对象在引用，则不能直接删除 1这一端的对象。会抛出异常！
		Customer customer = session.get(Customer.class, 2);
		session.delete(customer);		
		
		// 如果 同时 删除 1这一端 和 n这一端 的对应的 对象，才可以同时成功 删除。
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