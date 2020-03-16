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
	 *  ������Ϊ true����ʾ������һ������ʱ���ᶯ̬���� UPDATE ��䣬UPDATE ����н���������ȡֵ��Ҫ���µ��ֶΣ�Ĭ��ֵΪ  false��
	 *   
	 *  dynamic-insert
	 *  ������Ϊtrue����ʾ������һ������ʱ���ᶯ̬���� INSERT ��䣬INSERT ����н���������ȡֵ��Ϊ null ���ֶΣ�Ĭ��ֵΪ false��
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
	 * native - ��ʶ������������ �ײ����ݿ� ���Զ����ɱ�ʶ����֧����������ѡ��ʹ�� identity, sequence �� hilo ��ʶ�������������Ƽ�ʹ�ã�������
	 * 
	 * increment - Hibernate ���ȶ�ȡ News �������������ֵ������������ News���в����¼ʱ������ Max(id) �Ļ����ϵ���������Ϊ1��
	 * ���ַ�ʽ���ܲ������� ���ǣ������ǰ�ж��ʵ������ͬһ�����ݿ⣬��ô���ڸ���ʵ������ά������״̬��
	 * ��ͬʵ����������ͬ�����������Ӷ���������ظ��쳣��
	 * ��ˣ����ͬһ���ݿ� �ж��ʵ�����ʣ��˷�ʽ�������ʹ�á�
	 * ����Ϊlong, short����int�������� Ψһ��ʶ��ֻ����û������������ͬһ�ű��в�������ʱ����ʹ �á� 
	 * �ڼ�Ⱥ�²�Ҫʹ�á�
	 * 
	 * identity - �ɵײ����ݿ����������� ��ʶ������Ҫ��ײ����ݿ�� ���� ����Ϊ ���Զ��������ֶ����͡���e.g. DB2, MySQL��
	 * 
	 * sequence - ��ʶ�������� ���õײ����ݿ��ṩ�� ���� �����ɱ�ʶ����֧�� ���� �����ݿ������DB2, Oracle��
	 * 
	 * hilo - ��ʶ�������� �� Hibernate ����һ�� High/Low �㷨�����ɱ�ʶ�����������ݿ�� �ض��� ���ֶ��л�ȡ High ֵ��
	 *        Hibernate 5 ����֧�� Hilo ��ʶ��������  ! ! !
	 *        MySQL + Hibernate�� ֻ��ʹ�� native, increment, identity
	 */
	@Test
	public void testIdGenerator() throws InterruptedException {
		News news = new News("AA4", "aa3", new Date());
		session.save(news);
		
//		Thread.sleep(5000);		
	}
	
	/*
	 * ӳ�� ��������: desc Ϊ title: author
	 * �������� - �����ǳ־û�����������Զ�ֱ�Ӻͱ���ֶ�ƥ�䣬�־û������Щ���Ե�ֵ ����������ʱͨ������
	 *         ���ܵó������������Գ�Ϊ �������ԡ�
	 */
	@Test
	public void testPropertyUpdate() {
		News news = session.get(News.class, 1);
		news.setTitle("aaa");
		
		System.out.println(news.getDesc());
		System.out.println(news.getDate().getClass());
	}
	
	/**
	 * ���� ӳ�� Java ��ʱ�䣬��������
	 */
	@Test
	public void testDate() {
		News news = new News("AA_TestDate", "aa3", new Date());
		session.save(news);	
		
	}
	
	/**
	 * ���� ���� ����� ��BLOB��
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