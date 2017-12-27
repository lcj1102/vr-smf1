package com.suneee.smf.smf.model;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * @Description: TODO 发货申请子单DO
 * @author: 崔亚强
 * @date: 2017年12月1日 下午5:07:54
 */
public class SaleApplicationItemDO implements Serializable{

	private static final long serialVersionUID = 2590223767359162184L;

	/**
	  *	发货申请单表体主键
	  */
	private Long pk_saleapplication_item;
	
	/**
	  *	发货申请单主键
	  */
	private Long pk_sale_application;

	/**
	  *	明细行号
	  */
	private Long rownumber;

	/**
	  *	商品名称
	  */
	private String goods_name;

	/**
	  *	规格
	  */
	private String spec;

	/**
	  *	产地
	  */
	private String producing_area;

	/**
	  *	品质
	  */
	private String quality;

	/**
	  *	包装形态
	  */
	private String packing;

	/**
	 * 计量单位主键
	 */
	private Long pk_measurement;

	/**
	 * 计量单位名称
	 */
	private String measurement_name;

	/**
	 * 发货数量	
	 */
	private Double number;

	/**
	 * 单价	
	 */
	private Double price;

	/**
	 * 金额	
	 */
	private Double money;

	/**
	 * 批号	
	 */
	private String batch_number;
	
	/**
	 * 存储区域	
	 */
	private String storage_location;
	
	/**
	 * 来源单据类型
	 */
	private String sourcebilltype;
	
	/**
	 * 来源单据主表主键
	 */
	private Long sourcebillid;
		
	/**
	 * 来源单据子表主键
	 */
	private Long sourcebillitemid;
	
	/**
	 * 业务组织主键
	 */
	private Long enterpriseid;

	/**
	 * 作废人主键	
	 */
	private Long cancelid;

	/**
	 * 作废人姓名	
	 */
	private String cancelname;

	/**
	 * 作废日期	
	 */
	private Date canceldate;
	
	
	

	public Long getPk_saleapplication_item() {
		return pk_saleapplication_item;
	}

	public void setPk_saleapplication_item(Long pk_saleapplication_item) {
		this.pk_saleapplication_item = pk_saleapplication_item;
	}

	public Long getPk_sale_application() {
		return pk_sale_application;
	}

	public void setPk_sale_application(Long pk_sale_application) {
		this.pk_sale_application = pk_sale_application;
	}

	public Long getRownumber() {
		return rownumber;
	}

	public void setRownumber(Long rownumber) {
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

	public String getStorage_location() {
		return storage_location;
	}

	public void setStorage_location(String storage_location) {
		this.storage_location = storage_location;
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

	public Long getSourcebillitemid() {
		return sourcebillitemid;
	}

	public void setSourcebillitemid(Long sourcebillitemid) {
		this.sourcebillitemid = sourcebillitemid;
	}

	public Long getEnterpriseid() {
		return enterpriseid;
	}

	public void setEnterpriseid(Long enterpriseid) {
		this.enterpriseid = enterpriseid;
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
	

}

