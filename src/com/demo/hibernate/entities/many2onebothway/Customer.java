package com.demo.hibernate.entities.many2onebothway;

import java.util.HashSet;
import java.util.Set;

public class Customer {
	
	private Integer customerId;
	private String customerName;
	
	
	/**
	 * 1. ������������ʱ����Ҫʹ�ýӿ����ͣ����磺Set������Ϊ Hibernate �ڻ�ȡ��������ʱ��
	 *    ���ص��� Hibernate ���õļ������� �����磺org.hibernate.collection.internal.PersistentSet����
	 *    ������ Java SE һ����׼�ļ���ʵ�֡�
	 *    
	 * 2. ��Ҫ�Ѽ��Ͻ��� ��ʼ�������Է�ֹ���� ��ָ���쳣��
	 */
	private Set<Order> orders = new HashSet<>();
	
	public Integer getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}	

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", customerName=" + customerName + "]";
	}	
}
