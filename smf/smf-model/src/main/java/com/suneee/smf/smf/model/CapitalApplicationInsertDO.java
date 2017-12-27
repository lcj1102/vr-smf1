package com.suneee.smf.smf.model;

import java.util.List;

public class CapitalApplicationInsertDO {
	
	private String saveType;
	
	private String deleteItemId;
	
	private List<CapitalApplicationDO> capitalApplicationDO;

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	public String getDeleteItemId() {
		return deleteItemId;
	}

	public void setDeleteItemId(String deleteItemId) {
		this.deleteItemId = deleteItemId;
	}

	public List<CapitalApplicationDO> getCapitalApplicationDO() {
		return capitalApplicationDO;
	}

	public void setCapitalApplicationDO(
			List<CapitalApplicationDO> capitalApplicationDO) {
		this.capitalApplicationDO = capitalApplicationDO;
	}

}
