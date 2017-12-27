package com.suneee.smf.smf.model;

import java.util.Date;

/**
 * 
 * @Description: 企业计息
 * @author: 张礼佳
 * @date: 2017年12月8日 上午10:32:51
 */
public class InterestCalculationVO {

	/** 企业计息表主键 */
	private Long pk_interest_calculation;
	/** 贷款申请企业主键 */
	private Long pk_enterprise_application;
	/** 贷款申请企业名称 */
	private String enterprise_application_name;
	/** 计息日期 */
	private Date interest_date;
	/** 当天借款结余 */
	private Double days_principal;
	/** 当日利息 */
	private Double days_interest;
	/** 累计利息 */
	private Double accumulated_interest;
	/** 币种主键 */
	private Long pk_currency;
	/** 币种名称 */
	private String currency_name;
	/** 日利率 */
	private Double interest_rate;
	/** 状态 */
	private String state;
	/** 业务组织主键 */
	private Long enterpriseid;
	//时间戳
	private Date time_stamp;

	private String searchValue;

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public Long getPk_interest_calculation() {
		return pk_interest_calculation;
	}

	public void setPk_interest_calculation(Long pk_interest_calculation) {
		this.pk_interest_calculation = pk_interest_calculation;
	}

	public Long getPk_enterprise_application() {
		return pk_enterprise_application;
	}

	public void setPk_enterprise_application(Long pk_enterprise_application) {
		this.pk_enterprise_application = pk_enterprise_application;
	}

	public String getEnterprise_application_name() {
		return enterprise_application_name;
	}

	public void setEnterprise_application_name(
			String enterprise_application_name) {
		this.enterprise_application_name = enterprise_application_name;
	}

	public Date getInterest_date() {
		return interest_date;
	}

	public void setInterest_date(Date interest_date) {
		this.interest_date = interest_date;
	}

	public Double getDays_principal() {
		return days_principal;
	}

	public void setDays_principal(Double days_principal) {
		this.days_principal = days_principal;
	}

	public Double getDays_interest() {
		return days_interest;
	}

	public void setDays_interest(Double days_interest) {
		this.days_interest = days_interest;
	}

	public Double getAccumulated_interest() {
		return accumulated_interest;
	}

	public void setAccumulated_interest(Double accumulated_interest) {
		this.accumulated_interest = accumulated_interest;
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

	public Double getInterest_rate() {
		return interest_rate;
	}

	public void setInterest_rate(Double interest_rate) {
		this.interest_rate = interest_rate;
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
