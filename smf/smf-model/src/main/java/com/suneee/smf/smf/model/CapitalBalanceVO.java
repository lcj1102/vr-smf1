package com.suneee.smf.smf.model;

import java.util.Date;

public class CapitalBalanceVO {
	private Long pk_capital_balance;
	/*
	 * 资金结余统计时间
	 */
	private Date busi_time;
	/*
	 * 结余金额
	 */
	private Double amount;
	private Long pk_currency;
	private String currency_name;
	private String state;
	private Long enterpriseid;
	private Date time_stamp;
	private Date beginDate;
	private Date endDate;
	private String type;
	private String busi_time_str;
	public Long getPk_capital_balance() {
		return pk_capital_balance;
	}
	public void setPk_capital_balance(Long pk_capital_balance) {
		this.pk_capital_balance = pk_capital_balance;
	}
	public Date getBusi_time() {
		return busi_time;
	}
	public void setBusi_time(Date busi_time) {
		this.busi_time = busi_time;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Long getPk_currency() {
		return pk_currency;
	}
	public void setPk_currency(Long pk_currency) {
		this.pk_currency = pk_currency;
	}
	public String getCurrency_name() {
		return currency_name;
	}
	public void setCurrency_name(String currency_name) {
		this.currency_name = currency_name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getEnterpriseid() {
		return enterpriseid;
	}
	public void setEnterpriseid(Long enterpriseid) {
		this.enterpriseid = enterpriseid;
	}
	public Date getTime_stamp() {
		return time_stamp;
	}
	public void setTime_stamp(Date time_stamp) {
		this.time_stamp = time_stamp;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBusi_time_str() {
		return busi_time_str;
	}
	public void setBusi_time_str(String busi_time_str) {
		this.busi_time_str = busi_time_str;
	}

}
