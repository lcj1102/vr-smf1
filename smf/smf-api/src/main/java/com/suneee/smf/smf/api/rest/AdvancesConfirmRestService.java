package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AdvanceConfirmInsertDO;
import com.suneee.smf.smf.model.AdvanceConfirmVO;


public interface AdvancesConfirmRestService {

	public ResultMsg advanceConfirmList(String searchValue,String enterprise_application_name,String code,String contact,String beginForApplicationDate, String endForApplicationDate,String beginDateForBusiDate, String endDateForBusiDate,String state,String length,String pageNum,String sessionId);

	 public ResultMsg insert(AdvanceConfirmInsertDO insertDO,String sessionId);

	 public ResultMsg selectByPrimaryKey(Long id,String sessionId);

	 public ResultMsg update(AdvanceConfirmInsertDO insertDO,String sessionId);

	 public ResultMsg deleteSelect(AdvanceConfirmInsertDO insertDO,String sessionId);

	 public ResultMsg approve(AdvanceConfirmInsertDO insertDO, String sessionId);

	 
	 public ResultMsg checkApproveById(AdvanceConfirmVO advanceConfirmVO, String sessionId);

	public ResultMsg approveLog(AdvanceConfirmVO advanceConfirmVO, String sessionId);

	 public ResultMsg approveSelect(AdvanceConfirmVO advanceConfirmVO,String sessionId);

	 public ResultMsg submitModel(AdvanceConfirmVO advanceConfirmVO, String sessionId);
}
