package com.suneee.smf.smf.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 企业结算表
 * @author: 曹京航
 */
public class EnterpriseSettlementDO implements Serializable {
	private static final long serialVersionUID = 1L;
	// 企业结算单id
	private Long pk_enterprise_settlement;
	// 企业结算单编码
	private String code;
	// 企业结算单名称
	private String name;
	// 结算企业主键
	private Long pk_enterprise;
	//企业编码
	private String enterprise_code;
	// 结算企业名称
	private String enterprise_name;
	// 开始结算日期
	private Date busi_date;
	// 借贷金额
	private Double amount;
	// 物流费用
	private Double logistics_cost;
	// 监管费用
	private Double supervision_cost;
	// 币种主键
	private Long pk_currency;
	// 币种名称
	private String currency_name;
	// 注资企业注册地址
	private String address;
	// 注资企业统一社会信用代码
	private String credit_code;
	// 结算企业联系人
	private String contact;
	// 结算企业联系电话
	private String contact_number;
	// 状态
	private String state;
	// 登记人主键
	private Long inputmanid;
	// 登记人姓名
	private String inputmanname;
	// 登记日期
	private Date bookindate;
	// 修改人主键
	private Long modiferid;
	// 修改人姓名
	private String modifername;
	// 最后修改日期
	private Date modefydate;
	// 作废人主键
	private Long cancelid;
	// 作废人姓名
	private String cancelname;
	// 作废日期
	private Date canceldate;
	// 业务组织主键
	private Long enterpriseid;
	// 时间戳
	private Date time_stamp;

	public Long getPk_enterprise_settlement() {
		return pk_enterprise_settlement;
	}

	public void setPk_enterprise_settlement(Long pk_enterprise_settlement) {
		this.pk_enterprise_settlement = pk_enterprise_settlement;
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

	public Date getBookindate() {
		return bookindate;
	}

	public void setBookindate(Date bookindate) {
		this.bookindate = bookindate;
	}

	public Date getModefydate() {
		return modefydate;
	}

	public void setModefydate(Date modefydate) {
		this.modefydate = modefydate;
	}

	public Date getCanceldate() {
		return canceldate;
	}

	public void setCanceldate(Date canceldate) {
		this.canceldate = canceldate;
	}

	public String getEnterprise_code()
	{
		return enterprise_code;
	}

	public void setEnterprise_code(String enterprise_code)
	{
		this.enterprise_code = enterprise_code;
	}

}
