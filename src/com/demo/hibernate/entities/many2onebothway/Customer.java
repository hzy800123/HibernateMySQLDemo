package com.demo.hibernate.entities.many2onebothway;

import java.util.HashSet;
import java.util.Set;

public class Customer {
	
	private Integer customerId;
	private String customerName;
	
	
	/**
	 * 1. 声明集合类型时，需要使用接口类型（例如：Set），因为 Hibernate 在获取集合类型时，
	 *    返回的是 Hibernate 内置的集合类型 （例如：org.hibernate.collection.internal.PersistentSet），
	 *    而不是 Java SE 一个标准的集合实现。
	 *    
	 * 2. 需要把集合进行 初始化，可以防止发生 空指针异常。
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
