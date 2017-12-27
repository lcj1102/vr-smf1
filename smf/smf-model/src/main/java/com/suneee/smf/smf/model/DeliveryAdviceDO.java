package com.suneee.smf.smf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * 
 * @Description: 发货通知表DO
 * @author:daiaojie
 * @date: 2017年12月1日 下午4:58:18
 */
public class DeliveryAdviceDO implements Serializable{
	//
	private static final long serialVersionUID = 1238254124711171989L;
	//	发货通知单表体主键	
	private Long pk_delivery_advice;
	//	发货通知单号	
	private String code;
	//	发货通知单名称	
	private String name;
	//	发货企业主键	
	private Long pk_enterprise;
	//	发货企业名称	
	private String enterprise_name;
	//	申请金额	
	private Double application_amount;
	//	支付货款金额	
	private Double amount;
	//	币种主键	
	private Long pk_currency;
	//	币种名称
	private String currency_name;
	//	发货企业联系人	
	private String contact;
	//	发货企业联系电话	
	private String contact_number;
	//	发货通知日期
	private Date busi_date;
	//	状态
	private String state;
	//	登记人主键
	private Long inputmanid;
	//	登记人姓名	
	private String inputmanname;
	//	登记日期
	private Date bookindate;
	//	修改人主键	
	private Long modiferid;
	//	修改人姓名	
	private String modifername;
	//	最后修改日期	
	private Date modefydate;
	//	作废人主键	
	private Long cancelid;
	//	作废人姓名	
	private String cancelname;
	//	作废日期	
	private Date canceldate;
	//	业务组织主键	
	private Long enterpriseid;
	//时间戳
	private Date time_stamp;
	//子表列表
	private List<DeliveryItemDO> deliveryItemList;
	
	public List<DeliveryItemDO> getDeliveryItemList() {
		return deliveryItemList;
	}
	public void setDeliveryItemList(List<DeliveryItemDO> deliveryItemList) {
		this.deliveryItemList = deliveryItemList;
	}
	public Long getPk_delivery_advice() {
		return pk_delivery_advice;
	}
	public void setPk_delivery_advice(Long pk_delivery_advice) {
		this.pk_delivery_advice = pk_delivery_advice;
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
	public Double getApplication_amount() {
		return application_amount;
	}
	public void setApplication_amount(Double application_amount) {
		this.application_amount = application_amount;
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
