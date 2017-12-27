package com.suneee.smf.smf.model;

import java.util.Date;

public class CapitalBalanceNowDO {
	/*
	 * 主键
	 */
	private Long pk_capital_balance_now;
	/*
	 * 金额
	 */
	private Double amount;
	/*
	 * 币种
	 */
	private Long pk_currency;
	private String currency_name;
	/*
	 * 状态
	 */
	private String state;
	private Long enterpriseid;
	private Date time_stamp;
	public Long getPk_capital_balance_now() {
		return pk_capital_balance_now;
	}
	public void setPk_capital_balance_now(Long pk_capital_balance_now) {
		this.pk_capital_balance_now = pk_capital_balance_now;
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

}
