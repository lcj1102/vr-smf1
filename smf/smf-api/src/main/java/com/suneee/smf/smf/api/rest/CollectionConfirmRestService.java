package com.suneee.smf.smf.api.rest;


import com.suneee.smf.smf.common.ResultMsg;

public interface CollectionConfirmRestService {

	public ResultMsg list(String code,
						   String length, String state, String pageNum, String searchValue, String sessionId,
						 String enterprise_payment_name,
						   String beginDate1,
						   String endDate1,
						   String beginAmount1,
						  String endAmount1);

	// public ResultMsg insert(CollectionConfirmInsertDO insertDO, String sessionId);

	 public ResultMsg selectByPrimaryKey(Long id, String sessionId);

	// public ResultMsg update(CollectionConfirmInsertDO insertDO, String sessionId);

	 //public ResultMsg deleteSelect(CollectionConfirmInsertDO insertDO, String sessionId);

	//ResultMsg checkApproveById(CollectionConfirmVO advancesVO, String sessionId);

	//ResultMsg approveLog(CollectionConfirmVO advancesVO, String sessionId);

	//public ResultMsg approveSelect(CollectionConfirmVO advancesVO, String sessionId);

	//public ResultMsg submitModel(CollectionConfirmVO advancesVO, String sessionId);
	 
}
