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
	 * 插入 操作
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
		
		//设定 关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		order3.setCustomer(customer);
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		customer.getOrders().add(order3);
		
		// 执行 save 操作：
		//
		// (1) 先插入 Customer， 再插入 Order, 有 3条 INSERT 语句， 1条 UPDATE 语句。
		// 因为 1的一端 和 n的一端 都维护关联关系，所以会多出 UPDATE 语句。
		// 另外，可以在 1的一端 的 set节点 指定 "inverse=true"，来使1的一端 放弃维护关联关系！
		// 这时候，就只需要 3条 INSERT 语句。
		// 因此，建议设定 set节点的 inverse=true，建议先插入1的一端，后插入n的一端。
		// 好处是：不会多出 UPDATE　语句。
		session.save(customer);		
//		session.save(order1);
//		session.save(order2);
		
	
		// (2) 先插入 Order，再插入 Customer, 有 3条 INSERT 语句，3条 UPDATE 语句
//		session.save(order1);
//		session.save(order2);
//		session.save(customer);	
	}
	
	
	/*
	 * 查询 操作
	 */
	@Test
	public void testOne2ManyGet() {
		// 1. 对 n的一端 的集合 使用 延迟加载
		Customer customer = session.get(Customer.class, 8);
		System.out.println(customer.getCustomerName());
		
		// 2. 返回的 n的一端 的集合是 Hibernate 内置的集合类型 （例如： org.hibernate.collection.internal.PersistentSet）。
		// 该集合具有 延迟加载 和 存放“代理对象”的功能。
		System.out.println(customer.getOrders().getClass());
		
		// 3. 可能会抛出 LazyInitializationException 异常。
//		session.close();
		System.out.println(customer.getOrders().size());
		
		// 4. 在需要使用集合中的元素的时候，进行初始化。
		
	}
	
	
	/**
	 * 更新 操作
	 */
	@Test
	public void testUpdate() {
		Customer customer = session.get(Customer.class, 1);
		customer.getOrders().iterator().next().setOrderName("GGG");
	}
	
	
	/**
	 * 删除 操作
	 */
	@Test
	public void testMany2OneDelete() {
		// 在不设定 “级联关系” 的情况下，而且 1这一端 的对象 还有 n的对象在引用，则不能直接删除 1这一端的对象。会抛出异常！
		Customer customer = session.get(Customer.class, 4);
		session.delete(customer);
	}
	
	
	/**
	 * 测试 级联操作 cascade
	 */
	@Test
	public void testCascade() {
		Customer customer = session.get(Customer.class, 7);
		customer.getOrders().clear();		
	}
		
}