package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;

public interface CapitalBalanceRestService {

	void refreshBalanceTask();

    public ResultMsg capiBalanceList(String beginDate,String endDate,String length,String pageNum,String sessionId,String type);

	ResultMsg balanceList(String beginDateStr, String endDateStr, String type,String sessionId);
}
