package com.suneee.smf.smf.model;

import java.util.List;

public class CapitalSettlementInsertDO {
	
	private List<CapitalSettlementDO> capitalSettlementDOList;
	private String saveType;
	
	public List<CapitalSettlementDO> getCapitalSettlementDOList() {
		return capitalSettlementDOList;
	}
	public void setCapitalSettlementDOList(
			List<CapitalSettlementDO> capitalSettlementDOList) {
		this.capitalSettlementDOList = capitalSettlementDOList;
	}
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	
}
