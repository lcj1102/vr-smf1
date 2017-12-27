package com.suneee.smf.smf.api.rest;



import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AttachmentsInsertDO;
import com.suneee.smf.smf.model.CapitalInjectionInsertDO;
import com.suneee.smf.smf.model.CapitalInjectionVO;


public interface CapitalInjectionRestService {

	//列表查询
	public ResultMsg list(String code, String name, String beginDateForBusiDate, String endDateForBusiDate,String state,
			String length, String pageNum, String searchValue, String sessionId);

	//新增
	public ResultMsg insert(CapitalInjectionInsertDO insertDO, String sessionId);

	//删除
	public ResultMsg deleteSelect(CapitalInjectionInsertDO insertDO, String sessionId);

	//编辑
	public ResultMsg update(CapitalInjectionInsertDO insertDO, String sessionId);
	
	//根据主键查询
	public ResultMsg selectById(Long id, String sessionId);
	
	//提交审批
	public ResultMsg submitModel(CapitalInjectionVO bean, String sessionId);
	
	//查看审批进度
	public ResultMsg approveLog(CapitalInjectionVO bean, String sessionId);
	
	//查看当前用户是否有对应数据的审批权限
	public ResultMsg checkApproveById(CapitalInjectionVO bean, String sessionId);
	
	//审批
	public ResultMsg approveSelect(CapitalInjectionVO bean, String sessionId);
	
	//获取附件列表
	public ResultMsg fileListById(Long capitalInjectionId,String sessionId);
	
	//新增附加
	public ResultMsg insertFile(AttachmentsInsertDO file, String sessionId);
	
	//删除附件
	public ResultMsg deleteFile(AttachmentsInsertDO file, String sessionId);
	
	 
	public ResultMsg getCapitalInjectionByCode(String code ,String sessionId);

	public ResultMsg getCapitalInjectionByName(String keyword, String sessionId);
	
	public ResultMsg selectEnterpriseByName(String keyword,String sessionId);
	    
	public ResultMsg getEnterpriseByCode(String code,String sessionId);	
	
}
