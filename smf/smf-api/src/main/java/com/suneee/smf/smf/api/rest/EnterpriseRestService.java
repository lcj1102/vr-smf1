package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;

public interface EnterpriseRestService {

	public ResultMsg getEnterpriseByName(String keyword,String sessionId);

	public ResultMsg getEnterpriseByCode(String code,String sessionId);
	
}
