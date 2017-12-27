package com.suneee.smf.smf.model;

import java.util.Date;

public class CapitalSettlementVO {
	//资金退出结算单主键
	private Long pk_capital_settlement;
	//资金退出结算单号
	private String code;
	//资金注入合同号	
	private String capital_injection_code;
	//撤资企业主键
	private Long pk_enterprise;
	//撤资企业名称	
	private String enterprise_name;
	//注资日期	
	private Date injection_date;	
	//撤资结算日期	
	private Date busi_date;
	//注资金额（万元）	
	private Double amount;
	//结算金额（万元）	
	private Double settlement_amount;
	//币种主键
	private Long pk_currency;
	//币种名称	
	private String currency_name;
	//年化利率	
	private Double interest_rate;
	//注资企业注册地址	
	private String address;
	//统一社会信用代码	
	private String credit_code;
	//注资企业联系人	
	private String contact;
	//联系电话	
	private String contact_number;
	//状态	
	private String state;
	//登记人主键	
	private Long inputmanid;
	//登记人姓名	
	private String inputmanname;
	//登记日期	
	private Date bookindate;
	//修改人主键	
	private Long modiferid;
	//修改人姓名	
	private String modifername;
	//最后修改日期	
	private Date modefydate;
	//作废人主键	
	private Long cancelid;
	//作废人姓名	
	private String cancelname;
	//作废日期	
	private Date canceldate;
	//业务组织主键
	private Long enterpriseid;
	//时间戳
	private Date time_stamp;
	
	//查询条件	
	private String searchValue;
	
	private String type;
	private String option;
	private Date startDate;
	private Date endDate;
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public Long getPk_capital_settlement() {
		return pk_capital_settlement;
	}
	public void setPk_capital_settlement(Long pk_capital_settlement) {
		this.pk_capital_settlement = pk_capital_settlement;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getCapital_injection_code() {
		return capital_injection_code;
	}
	public void setCapital_injection_code(String capital_injection_code) {
		this.capital_injection_code = capital_injection_code;
	}
	public Long getPk_enterprise() {
		return pk_enterprise;
	}
	public void setPk_enterprise(Long pk_enterprise) {
		this.pk_enterprise = pk_enterprise;
	}
	public String getEnterprise_name() {
		return enterprise_name;
	}
	public void setEnterprise_name(String enterprise_name) {
		this.enterprise_name = enterprise_name;
	}
	public Date getInjection_date() {
		return injection_date;
	}
	public void setInjection_date(Date injection_date) {
		this.injection_date = injection_date;
	}
	public Date getBusi_date() {
		return busi_date;
	}
	public void setBusi_date(Date busi_date) {
		this.busi_date = busi_date;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getSettlement_amount() {
		return settlement_amount;
	}
	public void setSettlement_amount(Double settlement_amount) {
		this.settlement_amount = settlement_amount;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCredit_code() {
		return credit_code;
	}
	public void setCredit_code(String credit_code) {
		this.credit_code = credit_code;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getContact_number() {
		return contact_number;
	}
	public void setContact_number(String contact_number) {
		this.contact_number = contact_number;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getInputmanid() {
		return inputmanid;
	}
	public void setInputmanid(Long inputmanid) {
		this.inputmanid = inputmanid;
	}
	public String getInputmanname() {
		return inputmanname;
	}
	public void setInputmanname(String inputmanname) {
		this.inputmanname = inputmanname;
	}
	public Date getBookindate() {
		return bookindate;
	}
	public void setBookindate(Date bookindate) {
		this.bookindate = bookindate;
	}
	public Long getModiferid() {
		return modiferid;
	}
	public void setModiferid(Long modiferid) {
		this.modiferid = modiferid;
	}
	public String getModifername() {
		return modifername;
	}
	public void setModifername(String modifername) {
		this.modifername = modifername;
	}
	public Date getModefydate() {
		return modefydate;
	}
	public void setModefydate(Date modefydate) {
		this.modefydate = modefydate;
	}
	public Long getCancelid() {
		return cancelid;
	}
	public void setCancelid(Long cancelid) {
		this.cancelid = cancelid;
	}
	public String getCancelname() {
		return cancelname;
	}
	public void setCancelname(String cancelname) {
		this.cancelname = cancelname;
	}
	public Date getCanceldate() {
		return canceldate;
	}
	public void setCanceldate(Date canceldate) {
		this.canceldate = canceldate;
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
