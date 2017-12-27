package com.suneee.smf.smf.model;

import java.util.List;

public class CollectionConfirmInsertDO {

	private List<CollectionConfirmDO> insertDO;

	private String saveType;

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	public List<CollectionConfirmDO> getInsertDO() {
		return insertDO;
	}

	public void setInsertDO(List<CollectionConfirmDO> insertDO) {
		this.insertDO = insertDO;
	}
}
