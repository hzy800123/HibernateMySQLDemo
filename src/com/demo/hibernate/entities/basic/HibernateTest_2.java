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
	 * 1. save() ����
	 * 1�� ʹһ����ʱ���� ��Ϊ �־û�����
	 * 2�� Ϊ���� ���� ID��
	 * 3�� �� flush ����ʱ���ᷢ��һ�� INSERT ��䡣
	 * 4�� �� save ����֮ǰ�����õ�ID ����Ч�ġ�
	 * 5�� �־û����� �� ID �ǲ��ܱ��޸ĵ� ��
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
	 * persist(): Ҳ��ִ�� INSERT ����
	 * 
	 * �� save() ������
	 * �� persist() ����֮ǰ���������Ѿ��� ID �ˣ��򲻻�ִ�� INSERT�����һ� �׳��쳣��
	 * ��save() ����֮ǰ���������Ѿ��� ID �ˣ�����Ȼ��ִ��INSERT��ֻ�����õ�ID ��Ч�������׳��쳣��
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
	 * get VS load ������
	 * 
	 * 1. ִ��get() ���������������ض���
	 *    ��ִ��load() ����������ʹ�øö����򲻻�����ִ�� ��ѯ������������һ�� ��������󡱡� --- �ӳټ��� ��Lazy Loading��
	 *    
	 *    get �� ����������load �� �ӳټ�����
	 *    
	 * 2. load �������ܻ��׳� LazyInitializationException �쳣��
	 *    ����Ҫ ��ʼ�� ��������� ֮ǰ�Ѿ��ر��� Session.
	 * 
	 * 3. �� ���ݱ��� û�ж�Ӧ�ļ�¼���� session Ҳû�б��رգ�
	 *    get ���� null
	 *    load ����ʹ�øö�����κ����ԣ�û���⣻����Ҫ��ʼ����ʹ�ã�����ֱ���׳��쳣��
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
	 * update ������
	 * 
	 * 1. ������һ�� �־û����󣬲���Ҫ��ʾ�ĵ���update��������Ϊ�ڵ��� Transaction�� commit() ����ǰ��
	 *    ����ִ��session�� flush ������
	 *  
	 * 2. ����һ�� ���������Ҫ��ʾ�ĵ���session�� update ���������԰�һ�� ������� ��Ϊ �־û�����
	 * 
	 * ��Ҫע��ģ�
	 * 1. ����Ҫ���µ� ������� �� ���ݱ�ļ�¼�Ƿ�һ�£����ᷢ�� UPDATE ��䡣
	 *    ������� update ��������äĿ�� ���� UPDATE ����أ�
	 *    �ڸ���֮ǰ���Ȳ�ѯһ�μ��ɡ�
	 *    ��Ҫ�� .hbm.xml �ļ��� class �ڵ����� 
	 *    select-before-update = true ��Ĭ���� false����
	 *    ��ͨ������Ҫ���ø����ԣ�������Ҫ�� ���ݿ�Ĵ�����Trigger Эͬ������
	 *    
	 * 2. �����ݱ� ��û�ж�Ӧ�ļ�¼�������ǵ����� update ���������׳��쳣��
	 * 
	 * 3. �� update() ��������һ�� ������� ʱ��
	 *    ����� Session �Ļ����� �Ѿ����� ��ͬ�� OID �ĳ־ö��󣬻��׳��쳣��
	 *    ��Ϊ�� Session �Ļ����� ���������� OID ��ͬ�Ķ��� �� 
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
	 * saveOrUpdate ������ 
	 * 
	 * saveOrUpdate()����ͬʱ������save()��update()�����Ĺ��ܣ�
	 * �������Ĳ�������ʱ���󣬾͵���save()������
	 * �������Ĳ�����������󣬾͵���update()������
	 * �������Ĳ����ǳ־û������Ǿ�ֱ�ӷ��ء�
	 * 
	 * Java����� OID ȡֵΪ null����ô Hibernate�Ͱ�����Ϊ ��ʱ���󣬵��� save() ������
	 * ��� OID ȡֵ��Ϊ null, �Ͱ�����Ϊ ������󣬵��� update() ������
	 * ����
	 * ������� ����OID����update��
	 * ������� ������OID����save��
	 * 
	 * ע�⣺
	 * 1. �� OID ��Ϊ null, �����ݱ��л�û�� �����Ӧ�ļ�¼�����׳�һ���쳣��
	 * 2. �˽⣺OID ֵ���� Id �� unsaved-value ����ֵ�Ķ���Ҳ����Ϊ��һ�� �������
	 * 	 * 
	 */
	@Test
	public void testSaveOrUpdate() {
		News news = new News("FFF", "fff", new Date());
		news.setId(11);
		session.saveOrUpdate(news);
		
	}
	
	/**
	 * delete ����
	 * ִ�� ɾ��������ֻҪ OID �����ݱ��е� һ����¼��Ӧ���ͻ�׼��ִ�� DELETE ������
	 * �� OID �����ݱ���û�ж�Ӧ�ļ�¼�����׳��쳣��
	 * 
	 * ����ͨ������ Hibernate �����ļ� hibernate.use_identifier_rollback Ϊ true,
	 * ʹɾ������󣬰��� OID ��Ϊ null��
	 */
	@Test
	public void testDelete() {
		// ������� ���Ա�ɾ��
//		News news = new News();
//		news.setId(1);
		
		// �־û����� Ҳ���Ա�ɾ��
		News news = session.get(News.class, 35);		
		
		session.delete(news);
		System.out.println(news);
	}
	
	
	/**
	 * evict ����
	 * �� Session �����а�ָ���� �־û����� �Ƴ�
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
	 * ͨ�� Hibernate ���� �洢���� Stored Procedure
	 * 
	 * Work �ӿڣ�ֱ��ͨ�� JDBC API ������ ���ݿ����
	 * 
	 * E.g.
	 * public interface Work {
	 * 		public void execute(Connection connection) throws SQLException
	 * }
	 * 
	 * Session �� doWork(Work) ��������ִ�� Work ����ָ���Ĳ����������� Work ����� execute() ������
	 * Session ��ѵ�ǰʹ�õĵײ�� ���ݿ����� Connection ֱ�Ӵ��ݸ� execute() ������
	 * 
	 */
	@Test
	public void testDoWork() {
		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				 System.out.println(connection);				 

				 // ���� �洢���̡���Ҫ�� MySQL ���ݿ����ȶ���� �洢����  Stored Procedure��
				 String procedure = "call testProcedure()";
				 CallableStatement cstmt = connection.prepareCall(procedure);
				 int result = cstmt.executeUpdate();
				 System.out.println("Call Stored Procedure Result - Update Count: " + result);
			}
			
		});		
	}
	
}