package com.demo.hibernate.entities;

public class Pay {
	
	private int monthlyPay;
	private int yearlyPay;
	private int vocationWithPay;
	
	private Worker worker;
	
	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public int getMonthlyPay() {
		return monthlyPay;
	}
	
	public void setMonthlyPay(int monthlyPay) {
		this.monthlyPay = monthlyPay;
	}
	
	public int getYearlyPay() {
		return yearlyPay;
	}
	
	public void setYearlyPay(int yearlyPay) {
		this.yearlyPay = yearlyPay;
	}
	
	public int getVocationWithPay() {
		return vocationWithPay;
	}
	
	public void setVocationWithPay(int vocationWithPay) {
		this.vocationWithPay = vocationWithPay;
	}
}
