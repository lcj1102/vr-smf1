package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AttachmentsInsertDO;
import com.suneee.smf.smf.model.CapitalSettlementInsertDO;
import com.suneee.smf.smf.model.CapitalSettlementVO;

public interface CapitalSettlementRestService {

	public ResultMsg enterpriseList(String searchValue,String name,String code,String credit_code,String state,String beginDate,String endDate,String length,String pageNum,String sessionId);
	public ResultMsg insert(CapitalSettlementInsertDO capitalSettlementInsertDO,String sessionId);
	public ResultMsg selectByPrimaryKey(Long id,String sessionId);
	public ResultMsg deleteSelect(CapitalSettlementInsertDO capitalSettlementInsertDO,String sessionId);
	public ResultMsg update(CapitalSettlementInsertDO capitalSettlementDO,String sessionId);
	public ResultMsg fileListById(Long capitalSettlementId,String sessionId);
	public ResultMsg insertFile(AttachmentsInsertDO file, String sessionId);
	public ResultMsg deleteFile(AttachmentsInsertDO file, String sessionId);
    public ResultMsg submitModel(CapitalSettlementVO capitalSettlementVO,String sessionId);
	public ResultMsg checkApproveById(CapitalSettlementVO capitalSettlementVO, String sessionId);
	public ResultMsg approve(CapitalSettlementVO capitalSettlementVO, String sessionId);
	public ResultMsg approveLog(CapitalSettlementVO modelVO, String sessionId);
}
