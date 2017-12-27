package com.suneee.smf.smf.model;

import java.util.List;

public class AdvanceConfirmInsertDO {
	private List<AdvanceConfirmDO> insertDO;
	private String saveType;
	
	public List<AdvanceConfirmDO> getInsertDO() {
		return insertDO;
	}
	public void setInsertDO(List<AdvanceConfirmDO> insertDO) {
		this.insertDO = insertDO;
	}
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	
}
