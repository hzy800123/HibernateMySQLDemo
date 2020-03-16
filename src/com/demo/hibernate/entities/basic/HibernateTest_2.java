package com.demo.hibernate.entities.basic;

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

public class HibernateTest_2 {

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
	 * 1. save() 方法
	 * 1） 使一个临时对象 变为 持久化对象。
	 * 2） 为对象 分配 ID。
	 * 3） 在 flush 缓存时，会发送一条 INSERT 语句。
	 * 4） 在 save 方法之前，设置的ID 是无效的。
	 * 5） 持久化对象 的 ID 是不能被修改的 ！
	 */
	@Test
	public void testSave() {
		News news = new News();		
		news.setTitle("BB");
		news.setAuthor("bb");
		news.setDate(new Date());
		news.setId(101);
		
		System.out.println(news);
		
		session.save(news);
		
		System.out.println(news);
		news.setId(102);
	}
	
	/**
	 * persist(): 也会执行 INSERT 操作
	 * 
	 * 和 save() 的区别：
	 * 在 persist() 方法之前，若对象已经有 ID 了，则不会执行 INSERT，而且会 抛出异常。
	 * 在save() 方法之前，若对象已经有 ID 了，则依然会执行INSERT，只是设置的ID 无效，不会抛出异常。
	 */
	@Test
	public void testPersist() {
		News news = new News();		
		news.setTitle("DD");
		news.setAuthor("dd");
		news.setDate(new Date());
		news.setId(201);
				
		session.persist(news);
	}
	
	/**
	 * get VS load 方法：
	 * 
	 * 1. 执行get() 方法，会立即加载对象。
	 *    而执行load() 方法，若不使用该对象，则不会立即执行 查询操作，而返回一个 “代理对象”。 --- 延迟加载 （Lazy Loading）
	 *    
	 *    get 是 立即检索，load 是 延迟检索。
	 *    
	 * 2. load 方法可能会抛出 LazyInitializationException 异常：
	 *    在需要 初始化 “代理对象” 之前已经关闭了 Session.
	 * 
	 * 3. 若 数据表中 没有对应的记录，且 session 也没有被关闭：
	 *    get 返回 null
	 *    load 若不使用该对象的任何属性，没问题；若需要初始化（使用），则直接抛出异常。
	 */	
	@Test
	public void testLoad() {
		News news = session.load(News.class, 20);
		System.out.println(news.getClass().getName());
		
//		session.close();
//		System.out.println(news);
	}
	
	@Test
	public void testGet() {
		News news = session.get(News.class, 2);
		
//		session.close();
		System.out.println(news);
		
	}
	
	
	/**
	 * update 方法：
	 * 
	 * 1. 若更行一个 持久化对象，不需要显示的调用update方法，因为在调用 Transaction的 commit() 方法前，
	 *    会先执行session的 flush 方法。
	 *  
	 * 2. 更新一个 游离对象，需要显示的调用session的 update 方法。可以把一个 游离对象 变为 持久化对象。
	 * 
	 * 需要注意的：
	 * 1. 无论要更新的 游离对象 和 数据表的记录是否一致，都会发送 UPDATE 语句。
	 *    如何能让 update 方法不会盲目的 触发 UPDATE 语句呢？
	 *    在更新之前，先查询一次即可。
	 *    需要在 .hbm.xml 文件的 class 节点设置 
	 *    select-before-update = true （默认是 false），
	 *    但通常不需要设置该属性，除非需要和 数据库的触发器Trigger 协同工作。
	 *    
	 * 2. 若数据表 中没有对应的记录，但还是调用了 update 方法，会抛出异常。
	 * 
	 * 3. 当 update() 方法关联一个 游离对象 时，
	 *    如果在 Session 的缓存中 已经存在 相同的 OID 的持久对象，会抛出异常。
	 *    因为在 Session 的缓存中 不能有两个 OID 相同的对象 ！ 
	 */
	@Test
	public void testUpdate() {
		News news = session.get(News.class, 2);
			
		transaction.commit();
		session.close();
		
//		news.setId(101);
		
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
//		news.setAuthor("SUN");
		
		News news2 = session.get(News.class, 2);
		
		System.out.println("before update");
		session.update(news);
	}
	
	/**
	 * saveOrUpdate 方法： 
	 * 
	 * saveOrUpdate()方法同时包含了save()与update()方法的功能，
	 * 如果传入的参数是临时对象，就调用save()方法；
	 * 如果传入的参数是游离对象，就调用update()方法；
	 * 如果传入的参数是持久化对象，那就直接返回。
	 * 
	 * Java对象的 OID 取值为 null，那么 Hibernate就把它作为 临时对象，调用 save() 方法。
	 * 如果 OID 取值不为 null, 就把它作为 游离对象，调用 update() 方法。
	 * 即：
	 * 如果对象 存在OID，则update。
	 * 如果对象 不存在OID，则save。
	 * 
	 * 注意：
	 * 1. 若 OID 不为 null, 但数据表中还没有 和其对应的记录，会抛出一个异常。
	 * 2. 了解：OID 值等于 Id 的 unsaved-value 属性值的对象，也被认为是一个 游离对象。
	 * 	 * 
	 */
	@Test
	public void testSaveOrUpdate() {
		News news = new News("FFF", "fff", new Date());
		news.setId(11);
		session.saveOrUpdate(news);
		
	}
	
	/**
	 * delete 方法
	 * 执行 删除操作，只要 OID 和数据表中的 一条记录对应，就会准备执行 DELETE 操作。
	 * 若 OID 在数据表中没有对应的记录，则抛出异常。
	 * 
	 * 可以通过设置 Hibernate 配置文件 hibernate.use_identifier_rollback 为 true,
	 * 使删除对象后，把其 OID 置为 null。
	 */
	@Test
	public void testDelete() {
		// 游离对象 可以被删除
//		News news = new News();
//		news.setId(1);
		
		// 持久化对象 也可以被删除
		News news = session.get(News.class, 35);		
		
		session.delete(news);
		System.out.println(news);
	}
	
	
	/**
	 * evict 方法
	 * 从 Session 缓存中把指定的 持久化对象 移除
	 * 
	 */
	@Test
	public void testEvict() {
		News news1 = session.get(News.class, 27);
		News news2 = session.get(News.class, 36);
		
		news1.setTitle("CC");
		news2.setTitle("DD");
		
		session.evict(news1);
	}
	
	/**
	 * 通过 Hibernate 调用 存储过程 Stored Procedure
	 * 
	 * Work 接口：直接通过 JDBC API 来访问 数据库操作
	 * 
	 * E.g.
	 * public interface Work {
	 * 		public void execute(Connection connection) throws SQLException
	 * }
	 * 
	 * Session 的 doWork(Work) 方法用于执行 Work 对象指定的操作，即调用 Work 对象的 execute() 方法。
	 * Session 会把当前使用的底层的 数据库连接 Connection 直接传递给 execute() 方法。
	 * 
	 */
	@Test
	public void testDoWork() {
		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				 System.out.println(connection);				 

				 // 调用 存储过程。需要在 MySQL 数据库里先定义好 存储过程  Stored Procedure。
				 String procedure = "call testProcedure()";
				 CallableStatement cstmt = connection.prepareCall(procedure);
				 int result = cstmt.executeUpdate();
				 System.out.println("Call Stored Procedure Result - Update Count: " + result);
			}
			
		});		
	}
	
}