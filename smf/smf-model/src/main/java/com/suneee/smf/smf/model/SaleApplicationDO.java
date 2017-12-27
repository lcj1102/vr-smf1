package com.suneee.smf.smf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 
 * @Description: TODO 发货（销售）申请DO
 * @author: 崔亚强
 * @date: 2017年12月1日 下午4:49:25
 */
public class SaleApplicationDO implements Serializable{

	
	private static final long serialVersionUID = 5543613544145223846L;

	/**
	  *	发货申请单主键
	  */
	private Long pk_sale_application;

	/**
	  *	发货申请单号
	  */
	private String code;

	/**
	  *	资金使用申请单号
	  */
	private String capital_application_code;

	/**
	  *	资金使用申请主键
	  */
	private Long pk_capital_application;

	/**
	  *	发货申请企业主键
	  */
	private Long pk_enterprise_application;

	/**
	  *	发货申请企业名称
	  */
	private String enterprise_application_name;

	/**
	  *	付款公司主键
	  */
	private Long pk_enterprise_payment;

	/**
	  *	付款公司名称
	  */
	private String enterprise_payment_name;

	/**
	  *	发货申请金额
	  */
	private Double amount;

	/**
	  *	付款金额
	  */
	private Double payment_amount;

	/**
	  *	币种主键
	  */
	private Long pk_currency;
	
	/**
	  *	币种名称
	  */
	private String currency_name;
	
	/**
	  *	申请企业联系人
	  */
	private String contact;
	
	/**
	  *	申请企业联系电话
	  */
	private String contact_number;
	
	/**
	  *	申请日期
	  */
	private Date busi_date;

	/**
	  *	状态
	  */
	private String state;

	/**
	  *	登记人主键
	  */
	private Long inputmanid;

	/**
	  *	登记人姓名
	  */
	private String inputmanname;

	/**
	  *	登记日期
	  */
	private Date bookindate;

	/**
	  *	修改人主键
	  */
	private Long modiferid;

	/**
	  *	修改人姓名
	  */
	private String modifername;

	/**
	  *	最后修改日期
	  */
	private Date modefydate;

	/**
	  *	作废人主键
	  */
	private Long cancelid;

	/**
	  *	作废人姓名
	  */
	private String cancelname;

	/**
	  *	作废日期
	  */
	private Date canceldate;

	/**
	  *	业务组织主键
	  */
	private Long enterpriseid;
	
	/**
	  *	时间戳
	  */
	private Date time_stamp;

	//高级查询用字段
	/**
	 * 最小申请金额
	 */
	private Double minAmount;
	
	/**
	 * 最大申请金额
	 */
	private Double maxAmount;
	
	/**
	 * 最小申请日期
	 */
	private Date beginDate;
	
	/**
	 * 最大申请日期
	 */
	private Date endDate;
	
	/**
	 * 查询框字段
	 */
	private String searchValue;
	
	/**
	 * 对应的子单
	 */
	private List<SaleApplicationItemDO> itemList;
	
	/**
	 * 审批流程用
	 */
	private String type;
	
	/**
	 * 审批意见
	 */
	private String option;

	/**
	 * 物流费用
	 */
	private Double logistics_cost;
	
	/**
	 * 监管费用
	 */
	private Double supervision_cost;
	
	
	
	public Double getLogistics_cost() {
		return logistics_cost;
	}



	public void setLogistics_cost(Double logistics_cost) {
		this.logistics_cost = logistics_cost;
	}



	public Double getSupervision_cost() {
		return supervision_cost;
	}



	public void setSupervision_cost(Double supervision_cost) {
		this.supervision_cost = supervision_cost;
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



	public List<SaleApplicationItemDO> getItemList() {
		return itemList;
	}



	public void setItemList(List<SaleApplicationItemDO> itemList) {
		this.itemList = itemList;
	}



	public Double getMinAmount() {
		return minAmount;
	}



	public void setMinAmount(Double minAmount) {
		this.minAmount = minAmount;
	}



	public Double getMaxAmount() {
		return maxAmount;
	}



	public void setMaxAmount(Double maxAmount) {
		this.maxAmount = maxAmount;
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



	public String getSearchValue() {
		return searchValue;
	}



	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}



	public Long getPk_sale_application() {
		return pk_sale_application;
	}



	public void setPk_sale_application(Long pk_sale_application) {
		this.pk_sale_application = pk_sale_application;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getCapital_application_code() {
		return capital_application_code;
	}



	public void setCapital_application_code(String capital_application_code) {
		this.capital_application_code = capital_application_code;
	}



	public Long getPk_capital_application() {
		return pk_capital_application;
	}



	public void setPk_capital_application(Long pk_capital_application) {
		this.pk_capital_application = pk_capital_application;
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



	public void setEnterprise_application_name(String enterprise_application_name) {
		this.enterprise_application_name = enterprise_application_name;
	}



	public Long getPk_enterprise_payment() {
		return pk_enterprise_payment;
	}



	public void setPk_enterprise_payment(Long pk_enterprise_payment) {
		this.pk_enterprise_payment = pk_enterprise_payment;
	}



	public String getEnterprise_payment_name() {
		return enterprise_payment_name;
	}



	public void setEnterprise_payment_name(String enterprise_payment_name) {
		this.enterprise_payment_name = enterprise_payment_name;
	}



	public Double getAmount() {
		return amount;
	}



	public void setAmount(Double amount) {
		this.amount = amount;
	}



	public Double getPayment_amount() {
		return payment_amount;
	}



	public void setPayment_amount(Double payment_amount) {
		this.payment_amount = payment_amount;
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



	public Date getBusi_date() {
		return busi_date;
	}



	public void setBusi_date(Date busi_date) {
		this.busi_date = busi_date;
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

