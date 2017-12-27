package com.suneee.smf.smf.model;

import java.util.Date;
/**
 * @Description: 资金使用申请明细
 * @author: 致远
 * @date: 2017年12月1日 下午3:50:05
 */
public class CapitalApplicationItemVO {
	/*
	 * 资金使用申请子表主键	
	 */
	private Long pk_capitalapplication_item;
	/*
	 * 资金使用申请单主键	
	 */
	private Long pk_capital_application;
	/*
	 * 明细行号	
	 */
	private Integer rownumber;
	/*
	 * 商品名称	
	 */
	private String goods_name;
	/*
	 * 规格	
	 */
	private String spec;
	/*
	 * 产地	
	 */
	private String producing_area;
	/*
	 * 品质	
	 */
	private String quality;
	/*
	 * 包装形态	
	 */
	private String packing;
	/*
	 * 计量单位主键	
	 */
	private Long pk_measurement;
	/*
	 * 计量单位名称	
	 */
	private String measurement_name;
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
	 * 数量	
	 */
	private Double number;
	/*
	 * 单价	
	 */
	private Double price;
	/*
	 * 金额	
	 */
	private Double money;
	/*
	 * 批号	
	 */
	private String batch_number;
	/*
	 * 业务组织主键	
	 */
	private Long enterpriseid;
	/*
	 * 当前可申请发货最大数量
	 */
	private Double maxNumber;
	
	
	
	public Double getMaxNumber() {
		return maxNumber;
	}
	public void setMaxNumber(Double maxNumber) {
		this.maxNumber = maxNumber;
	}
	
	public Long getPk_capitalapplication_item() {
		return pk_capitalapplication_item;
	}
	public void setPk_capitalapplication_item(Long pk_capitalapplication_item) {
		this.pk_capitalapplication_item = pk_capitalapplication_item;
	}
	public Long getPk_capital_application() {
		return pk_capital_application;
	}
	public void setPk_capital_application(Long pk_capital_application) {
		this.pk_capital_application = pk_capital_application;
	}
	public Integer getRownumber() {
		return rownumber;
	}
	public void setRownumber(Integer rownumber) {
		this.rownumber = rownumber;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	public String getProducing_area() {
		return producing_area;
	}
	public void setProducing_area(String producing_area) {
		this.producing_area = producing_area;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getPacking() {
		return packing;
	}
	public void setPacking(String packing) {
		this.packing = packing;
	}
	public Long getPk_measurement() {
		return pk_measurement;
	}
	public void setPk_measurement(Long pk_measurement) {
		this.pk_measurement = pk_measurement;
	}
	public String getMeasurement_name() {
		return measurement_name;
	}
	public void setMeasurement_name(String measurement_name) {
		this.measurement_name = measurement_name;
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
	public Double getNumber() {
		return number;
	}
	public void setNumber(Double number) {
		this.number = number;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public String getBatch_number() {
		return batch_number;
	}
	public void setBatch_number(String batch_number) {
		this.batch_number = batch_number;
	}
	public Long getEnterpriseid() {
		return enterpriseid;
	}
	public void setEnterpriseid(Long enterpriseid) {
		this.enterpriseid = enterpriseid;
	}
}
