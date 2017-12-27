package com.suneee.smf.smf.model;

import java.util.List;

public class SaleApplicationInsertDO {
	
	List<SaleApplicationDO> insertDO;

	private String saveType;

	public List<SaleApplicationDO> getInsertDO() {
		return insertDO;
	}

	public void setInsertDO(List<SaleApplicationDO> insertDO) {
		this.insertDO = insertDO;
	}

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
}
