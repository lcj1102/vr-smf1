package com.suneee.smf.smf.common;
/**
 * 用作查询流程进度时参数的封装
 * @author zhiyuan
 */
public class FlowPlan {

	private String initid;
	private String processDefinitionId;
	private String showreturn;
	
	public String getInitid() {
		return initid;
	}
	public void setInitid(String initid) {
		this.initid = initid;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public String getShowreturn() {
		return showreturn;
	}
	public void setShowreturn(String showreturn) {
		this.showreturn = showreturn;
	}
}
