package com.suneee.smf.smf.model;

import java.util.Date;
import java.util.List;
/**
 * @Description: 资金使用申请
 * @author: 致远
 * @date: 2017年12月1日 下午3:42:04
 */
public class CapitalApplicationDO {
	/*
	 * 资金使用申请单主键	
	 */
	private Long pk_capital_application;
	/*
	 * 资金使用申请单号	
	 */
	private String code;
	/*
	 * 资金使用申请单名称	
	 */
	private String name;
	/*
	 * 申请企业主键	
	 */
	private Long pk_enterprise;
	/*
	 * 申请企业名称	
	 */
	private String enterprise_name;
	/*
	 * 申请日期	
	 */
	private Date busi_date;
	/*
	 * 申请金额	
	 */
	private Double amount;
	/*
	 * 币种主键	
	 */
	private Long pk_currency;
	/*
	 * 币种名称	
	 */
	private String currency_name;
	/*
	 * 申请企业注册地址	
	 */
	private String address;
	/*
	 * 申请企业统一社会信用代码	
	 */
	private String credit_code;
	/*
	 * 申请企业联系人	
	 */
	private String contact;
	/*
	 * 申请企业联系电话	
	 */
	private String contact_number;
	/*
	 * 状态	
	 */
	private String state;
	/*
	 * 登记人主键	
	 */
	private Long inputmanid;
	/*
	 * 登记人姓名	
	 */
	private String inputmanname;
	/*
	 * 登记日期	
	 */
	private Date bookindate;
	/*
	 * 修改人主键	
	 */
	private Long modiferid;
	/*
	 * 修改人姓名	
	 */
	private String modifername;
	/*
	 * 最后修改日期	
	 */
	private Date modefydate;
	/*
	 * 作废人主键	
	 */
	private Long cancelid;
	/*
	 * 作废人姓名	
	 */
	private String cancelname;
	/*
	 * 作废日期	
	 */
	private Date canceldate;
	/*
	 * 业务组织主键	
	 */
	private Long enterpriseid;
	/*
	 * 时间戳	
	 */
	private Date time_stamp;
	/*
	 * 销售标识
	 */
	private String sale_flag;
	
	private List<CapitalApplicationItemDO> itemList;
	
	private String searchValue;
	private Date startDate;
	private Date endDate;
	
	private String type;
	private String option;
	
	public String getSale_flag() {
		return sale_flag;
	}
	public void setSale_flag(String sale_flag) {
		this.sale_flag = sale_flag;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
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
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public List<CapitalApplicationItemDO> getItemList() {
		return itemList;
	}
	public void setItemList(List<CapitalApplicationItemDO> itemList) {
		this.itemList = itemList;
	}
	public Long getPk_capital_application() {
		return pk_capital_application;
	}
	public void setPk_capital_application(Long pk_capital_application) {
		this.pk_capital_application = pk_capital_application;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
