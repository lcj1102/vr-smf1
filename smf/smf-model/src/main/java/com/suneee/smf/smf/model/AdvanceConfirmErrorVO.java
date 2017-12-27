package com.suneee.smf.smf.model;

import java.util.Date;
/**
 * @Description: 放款确认异常表
 * @author 畅照宇
 * @date: 2017年12月19日 上午11:22:59
 */

public class AdvanceConfirmErrorVO {

	/*
	 *放款确认异常主键
	 */
	private Long pk_advanceconfirm_error;
	/*
	 * 放款确认单主键
	 */
	private Long pk_advance_confirm;
	/*
	 * 放款确认单号
	 */
	private String code;
	/*
	 * 申请企业主键
	 */
	private Long pk_enterprise_application;
	/*
	 * 申请企业名称
	 */
	private String enterprise_application_name;
	/*
	 * 申请企业联系人
	 */
	private String contact;
	/*
	 * 申请企业联系电话
	 */
	private String contact_number;
	
	/*
	 * 申请金额
	 */
	private Double application_amount;
	/*
	 * 支付金额
	 */
	private Double  advances_amount;
	/*
	 * 币种主键
	 */
	private Long pk_currency;
	/*
	 * 币种名称
	 */
	private String currency_name;
	/*
	 * 异常发生原因
	 */
	private String  error_msg;
	/*
	 * 申请日期
	 */
	private Date application_date;
	/*
	 * 支付时间
	 */
	private Date busi_date;
	/*
	 * 付款方式
	 */
	private String payment_method;
	/*
	 * 付款凭证号
	 */
	private String payment_billno;
	/*
	 * 收款银行账号
	 */
	private String bank_account;
	/*
	 * 开户行
	 */
	private String bank_deposit;
	
	/*
	 * 来源单据类型
	 */
	private String sourcebilltype;
	/*
	 * 来源单据主表主键
	 */
	private Long sourcebillid;
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
	 * 异常发生时间
	 */
	private Date time_stamp;
	/**
	 * 查询框字段
	 */
	private String searchValue;
	/**
	 * 异常发生最小日期
	 */
	private Date beginDateTS;
	
	/**
	 * 异常发生最大日期
	 */
	private Date endDateTS;
	
	/**
	 * 最小支付日期
	 */
	private Date beginDateBD;
	
	/**
	 * 最大支付日期
	 */
	private Date endDateBD;
	
	/**
	 * 未处理
	 */
	private String state_new;
	
	/**
	 * 已处理
	 */
	private String state_end;

	public Long getPk_advanceconfirm_error() {
		return pk_advanceconfirm_error;
	}

	public void setPk_advanceconfirm_error(Long pk_advanceconfirm_error) {
		this.pk_advanceconfirm_error = pk_advanceconfirm_error;
	}

	public Long getPk_advance_confirm() {
		return pk_advance_confirm;
	}

	public void setPk_advance_confirm(Long pk_advance_confirm) {
		this.pk_advance_confirm = pk_advance_confirm;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public Double getApplication_amount() {
		return application_amount;
	}

	public void setApplication_amount(Double application_amount) {
		this.application_amount = application_amount;
	}

	public Double getAdvances_amount() {
		return advances_amount;
	}

	public void setAdvances_amount(Double advances_amount) {
		this.advances_amount = advances_amount;
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

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}

	public Date getApplication_date() {
		return application_date;
	}

	public void setApplication_date(Date application_date) {
		this.application_date = application_date;
	}

	public Date getBusi_date() {
		return busi_date;
	}

	public void setBusi_date(Date busi_date) {
		this.busi_date = busi_date;
	}

	public String getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}

	public String getPayment_billno() {
		return payment_billno;
	}

	public void setPayment_billno(String payment_billno) {
		this.payment_billno = payment_billno;
	}

	public String getBank_account() {
		return bank_account;
	}

	public void setBank_account(String bank_account) {
		this.bank_account = bank_account;
	}

	public String getBank_deposit() {
		return bank_deposit;
	}

	public void setBank_deposit(String bank_deposit) {
		this.bank_deposit = bank_deposit;
	}

	public String getSourcebilltype() {
		return sourcebilltype;
	}

	public void setSourcebilltype(String sourcebilltype) {
		this.sourcebilltype = sourcebilltype;
	}

	public Long getSourcebillid() {
		return sourcebillid;
	}

	public void setSourcebillid(Long sourcebillid) {
		this.sourcebillid = sourcebillid;
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

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public Date getBeginDateTS() {
		return beginDateTS;
	}

	public void setBeginDateTS(Date beginDateTS) {
		this.beginDateTS = beginDateTS;
	}

	public Date getEndDateTS() {
		return endDateTS;
	}

	public void setEndDateTS(Date endDateTS) {
		this.endDateTS = endDateTS;
	}

	public Date getBeginDateBD() {
		return beginDateBD;
	}

	public void setBeginDateBD(Date beginDateBD) {
		this.beginDateBD = beginDateBD;
	}

	public Date getEndDateBD() {
		return endDateBD;
	}

	public void setEndDateBD(Date endDateBD) {
		this.endDateBD = endDateBD;
	}

	public String getState_new() {
		return state_new;
	}

	public void setState_new(String state_new) {
		this.state_new = state_new;
	}

	public String getState_end() {
		return state_end;
	}

	public void setState_end(String state_end) {
		this.state_end = state_end;
	}
	
	
}
