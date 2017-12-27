package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AttachmentsInsertDO;
import com.suneee.smf.smf.model.CapitalApplicationInsertDO;
import com.suneee.smf.smf.model.CapitalApplicationVO;

public interface CapitalApplicationRestService {

	ResultMsg insert(CapitalApplicationInsertDO insertDO, String sessionId);

	ResultMsg selectByPrimaryKey(Long id, String sessionId);

	ResultMsg update(CapitalApplicationInsertDO insertDO, String sessionId);

	ResultMsg deleteSelect(CapitalApplicationInsertDO insertDO, String sessionId);

	ResultMsg checkApproveById(CapitalApplicationVO modelVO, String sessionId);

	ResultMsg approveSelect(CapitalApplicationVO modelVO, String sessionId);

	ResultMsg approveLog(CapitalApplicationVO modelVO, String sessionId);

	ResultMsg fileListById(Long capitalApplicationId, String sessionId);

	ResultMsg deleteFile(AttachmentsInsertDO file, String sessionId);

	ResultMsg insertFile(AttachmentsInsertDO file, String sessionId);

	ResultMsg getEnterpriseList(String keyword, String sessionId);

	ResultMsg getEnterpriseByCode(String code, String sessionId);

	ResultMsg submitModel(CapitalApplicationInsertDO insertDO, String sessionId);

	ResultMsg getCapitalApplicationByCode(String code, String sessionId);
	
	ResultMsg selectCapitalApplicationByCode(String code, String sessionId);

	ResultMsg getCapitalApplicationList(String enterpriseId, String keyword,String sessionId);

	ResultMsg list(String code, String name, String enterprise_name,String searchValue, String beginDate, String endDate, String state,String length, String pageNum, String sessionId);

}
