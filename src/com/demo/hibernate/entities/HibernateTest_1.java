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
	 * Session 缓存可以 减少 Hibernate 应用程序访问数据库的 频率。
	 */
	@Test
	public void testSessionCache() {
		News news = session.get(News.class, 2);
		System.out.println(news);
		
		News news2 = session.get(News.class, 2);
		System.out.println(news2);
	}
	
	
	/**
	 * flush: 使数据表中的记录 和 Session 缓存中的对象的状态保持一致。
	 *        为了保持一致，则可能会发送对应的 SQL 语句。
	 * 1. 在调用 Transaction.commit() 的方法中：
	 *    先调用 Session 的 flush 方法，再 commit 提交事务。
	 * 2. flush() 方法可能会 发送 SQL 语句，但是不会提交事务。
	 * 
	 * 3. 注意：	在未提交事务 或 显式地调用 session.flush() 方法之前，也可能会进行 flush() 操作。
	 *    （1）执行 HQL 或 QBC 查询，会先进行 flush() 操作，以得到数据表地最新的记录。
	 *    （2） 若记录的 ID 是由底层数据库 使用自增的方式生成的，则在调用 save() 方法后，就会立即发送 Insert 语句。
	 *        因为 save 方法后，必须保证对象的 ID 是存在的！
	 *        
	 * commit() 和  flush() 方法的区别：
	 * - flush 执行一系列的 SQL语句，但不提交事务。
	 * - Commit 方法 先调用 flush 方法，然后提交事务。提交事务意味着 对数据库操作 永久保存下来了。
	 * 
	 */
	@Test
	public void testSessionFlush() {
		News news = session.get(News.class, 2);
		news.setAuthor("Oracle");
		
//		session.flush();
//		System.out.println("flush...");
		
		// 当使用 QBC 查询：触发了先 执行 flush() 操作，再查询 最新的记录 状态。
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
	 * refresh(): 会强制 发送　SELECT　语句，以使　Session缓存中对象的状态　和　数据表中对应的记录　保持一致！
	 */
	@Test
	public void testRefresh() {
		News news = session.get(News.class, 2);
		System.out.println(news);
		
		session.refresh(news);
		System.out.println(news);
	}
	
	
	/**
	 * clear(): 清理缓存
	 *          清理缓存之后，缓存里没有该对象，Hibernate 需要重新发送一条 Select SQL 语句进行查询。
	 */
	@Test
	public void testClear() {
		News news = session.get(News.class, 2);
		
		session.clear();
		News news2 = session.get(News.class, 2);		
	}
}