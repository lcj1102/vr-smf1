package com.suneee.smf.smf.model;

import java.util.List;

public class DeliveryAdviceList {
	private List<DeliveryAdviceVO> deliveryAdviceList;
	private String delIds;
	
	public String getDelIds() {
		return delIds;
	}
	public void setDelIds(String delIds) {
		this.delIds = delIds;
	}
	
	public List<DeliveryAdviceVO> getDeliveryAdviceList() {
		return deliveryAdviceList;
	}
	public void setDeliveryAdviceList(List<DeliveryAdviceVO> deliveryAdviceList) {
		this.deliveryAdviceList = deliveryAdviceList;
	}

}
