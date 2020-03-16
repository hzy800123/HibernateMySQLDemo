package com.demo.hibernate.entities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;
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

public class HibernateTest_3 {

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
	 *	dynamic-update
	 *  若设置为 true，表示当更新一个对象时，会动态生成 UPDATE 语句，UPDATE 语句中仅包含所有取值需要更新的字段，默认值为  false。
	 *   
	 *  dynamic-insert
	 *  若设置为true，表示当保存一个对象时，会动态生成 INSERT 语句，INSERT 语句中仅包含所有取值不为 null 的字段，默认值为 false。
	 *  
	 */
	@Test
	public void testDynamicUpdate() {
		News news = session.get(News.class, 27);
		news.setAuthor("ABCDEFG");
		
		session.save(new News("ABC"));
	}
	
	/**
	 * ID Generator:
	 * 
	 * native - 标识符生成器依据 底层数据库 对自动生成标识符的支持能力，来选择使用 identity, sequence 或 hilo 标识符生成器。（推荐使用！！！）
	 * 
	 * increment - Hibernate 会先读取 News 表中主键的最大值，而接下来向 News表中插入记录时，就在 Max(id) 的基础上递增，增量为1。
	 * 这种方式可能产生的问 题是：如果当前有多个实例访问同一个数据库，那么由于各个实例各自维护主键状态，
	 * 不同实例可能生成同样的主键，从而造成主键重复异常。
	 * 因此，如果同一数据库 有多个实例访问，此方式必须避免使用。
	 * 用于为long, short或者int类型生成 唯一标识。只有在没有其他进程往同一张表中插入数据时才能使 用。 
	 * 在集群下不要使用。
	 * 
	 * identity - 由底层数据库来负责生成 标识符，它要求底层数据库把 主键 定义为 “自动增长”字段类型。（e.g. DB2, MySQL）
	 * 
	 * sequence - 标识符生成器 利用底层数据库提供的 序列 来生成标识符。支持 序列 的数据库包括：DB2, Oracle。
	 * 
	 * hilo - 标识符生成器 由 Hibernate 按照一种 High/Low 算法，生成标识符。它从数据库的 特定表 的字段中获取 High 值。
	 *        Hibernate 5 不在支持 Hilo 标识符生成器  ! ! !
	 *        MySQL + Hibernate： 只能使用 native, increment, identity
	 */
	@Test
	public void testIdGenerator() throws InterruptedException {
		News news = new News("AA4", "aa3", new Date());
		session.save(news);
		
//		Thread.sleep(5000);		
	}
	
	/*
	 * 映射 派生属性: desc 为 title: author
	 * 派生属性 - 并不是持久化类的所有属性都直接和表的字段匹配，持久化类的有些属性的值 必须在运行时通过计算
	 *         才能得出来，这种属性成为 派生属性。
	 */
	@Test
	public void testPropertyUpdate() {
		News news = session.get(News.class, 1);
		news.setTitle("aaa");
		
		System.out.println(news.getDesc());
		System.out.println(news.getDate().getClass());
	}
	
	/**
	 * 测试 映射 Java 的时间，日期类型
	 */
	@Test
	public void testDate() {
		News news = new News("AA_TestDate", "aa3", new Date());
		session.save(news);	
		
	}
	
	/**
	 * 测试 保存 大对象 （BLOB）
	 *  
	 */
	@Test
	public void testSaveBlob() throws IOException, SQLException {
		News news = new News();
		news.setTitle("AAA2");
		news.setAuthor("aaa");
		news.setDate(new Date());
		news.setDesc("DESC");
		news.setContent("Content2");
		
		InputStream stream = new FileInputStream("Believe is seeing.jpg");
		Blob image = Hibernate.getLobCreator(session).createBlob(stream, stream.available());
		
		news.setImage(image);
		session.save(news);
		
		News news2 = session.get(News.class, 1);
		Blob image2 = news2.getImage();
		
		InputStream in = image2.getBinaryStream();
		System.out.println(in.available());
		
		
	}
}