package com.tbj.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class Account1 implements Serializable {

	private static final long serialVersionUID = 7053271413525144675L;

	private Integer id;

	private String key;

	private String companyId;
	private BigDecimal balance;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "Account1 [id=" + id + ", companyId=" + companyId + ", balance="
				+ balance + "]";
	}
}
