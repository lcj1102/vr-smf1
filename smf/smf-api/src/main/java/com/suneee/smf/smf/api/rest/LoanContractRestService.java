package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AttachmentsInsertDO;
import com.suneee.smf.smf.model.LoanContractDO;
import com.suneee.smf.smf.model.LoanContractDOList;
import com.suneee.smf.smf.model.LoanContractVO;

public interface LoanContractRestService
{
	/**
	 * 首页查询
	 * @param length 每页显示记录数
	 * @param pageNum 页码
	 * @param searchValue 关键字
	 * @param state 状态
	 * @param code 长期借款合同号
	 * @param name 长期借款合同名称 
	 * @param enterprise_name 借款企业名称
	 * @param starttime 合同签订日期（起始日期）
	 * @param endtime 合同签订日期（终止日期）
	 * @param sessionId
	 * @return
	 */
	ResultMsg selectLoanContract(String length, String pageNum,
								String searchValue, String state,
								String code, String name,
								String enterprise_name, String starttime,
								String endtime, String sessionId);
	
	/**
	 * 新增
	 * @param loanContractVO
	 * @param sessionId
	 * @return
	 */
	ResultMsg insert(LoanContractVO loanContractVO,String sessionId);
	
	/**
	 * 删除（作废）
	 * @param insertList
	 * @param sessionId
	 * @return
	 */
	ResultMsg delete(LoanContractDOList insertList,String sessionId);
	
	/**
	 * 修改
	 * @param loanContractVO
	 * @param sessionId
	 * @return
	 */
	ResultMsg modify(LoanContractVO loanContractVO,String sessionId);
	
	/**
	 * 审核
	 * @param loanContractVO
	 * @param sessionId
	 * @return
	 */
	ResultMsg approve(LoanContractVO loanContractVO,String sessionId);
	
	/**
	 * 提交流程
	 * @param loanContractVO
	 * @param sessionId
	 * @return
	 */
	ResultMsg submitModel(LoanContractDO loanContractDO,String sessionId);
	
	/**
	 * 查看流程进度
	 * @param loanContractVO
	 * @param sessionId
	 * @return
	 */
	ResultMsg viewProcess(LoanContractDO loanContractDO,String sessionId);
	
	/**
	 * 变更
	 * @param loanContractVO
	 * @param sessionId
	 * @return
	 */
	ResultMsg change(LoanContractVO loanContractVO,String sessionId);
	
	/**
	 * 根据ID查询
	 * @param loanContractVO
	 * @param sessionId
	 * @return
	 */
	ResultMsg selectLoanContractByID(Long pk_loan_contract,String sessionId);
	
	/**
	 * 审核之前校验角色
	 * @param loanContractDO
	 * @param sessionId
	 * @return
	 */
	ResultMsg checkApproveById(LoanContractDO loanContractDO, String sessionId);
	
	/**
	 * 获取附件列表
	 * @param capitalInjectionId
	 * @param sessionId
	 * @return
	 */
	public ResultMsg fileListById(Long capitalInjectionId,String sessionId);
	
	/**新增附加
	 * @param file
	 * @param sessionId
	 * @return
	 */
	public ResultMsg insertFile(AttachmentsInsertDO file, String sessionId);
	
	/**
	 * 删除附件
	 * @param file
	 * @param sessionId
	 * @return
	 */
	public ResultMsg deleteFile(AttachmentsInsertDO file, String sessionId);
	/**
	 * 
	 * @Title: getEnterpriseByName 
	 * @Description: 根据企业名称模糊查询
	 * @param keyword
	 * @param sessionId
	 * @return
	 * @return: ResultMsg
	 */
	public ResultMsg getEnterpriseByName(String keyword,String sessionId);
	/**
	 * 
	 * @Title: getEnterpriseByCode 
	 * @Description: 根据企业编码查询企业信息
	 * @param code
	 * @param sessionId
	 * @return
	 * @return: ResultMsg
	 */
	public ResultMsg getEnterpriseByCode(String code,String sessionId);
}
