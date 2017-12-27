package com.suneee.smf.smf.consumer.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.scf.api.provider.CodeRuleRestProvider;
import com.suneee.scn.scf.api.provider.EnterpriseRestProvider;
import com.suneee.scn.scf.model.EnterpriseVO;
import com.suneee.scn.system.api.provider.RoleProvider;
import com.suneee.scn.system.model.vo.RoleInfo;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AttachmentsDO;
import com.suneee.smf.smf.model.AttachmentsVO;
import com.suneee.smf.smf.model.EnterpriseSettlementDO;
import com.suneee.smf.smf.model.LoanContractDO;
import com.suneee.smf.smf.model.LoanContractDOList;
import com.suneee.smf.smf.model.LoanContractVO;
import com.suneee.smf.smf.service.ActivitiUtilService;
import com.suneee.smf.smf.service.AttachmentsService;
import com.suneee.smf.smf.service.EnterpriseSettlementService;
import com.suneee.smf.smf.service.LoanContractService;

/**
 * @Description 长期借款合同
 * @author WuXiaoYang
 * @Date 2017-12-8 16:57
 *
 */
@Service("loanContractConsumer")
public class LoanContractConsumer
{
	private static final Logger log = LoggerFactory.getLogger(LoanContractConsumer.class);
	
	@Autowired
	LoanContractService loanContractService;
	
	@Autowired
	private ActivitiUtilService activitiUtilService;
	
	@Autowired
	private AttachmentsService attachmentsService;
	
	@Autowired
	private EnterpriseSettlementService enterpriseSettlementService;
	
	//TODO 测试环境URL
	@Reference(version="1.0",url="dubbo://172.16.36.68:20917/com.suneee.scn.system.api.provider.RoleProvider")
	private RoleProvider roleProvider;
	
	//TODO 测试环境URL
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleProvider;
	
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.EnterpriseRestProvider")
	private EnterpriseRestProvider enterpriseRestProvider;
	/**
	 * 首页查询
	 * @param page
	 * @param loanContractVO
	 * @return
	 */
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public ResultMsg selectLoanContract(Page<LoanContractDO> page,LoanContractVO loanContractVO)
	{
		ResultMsg resultMsg = new ResultMsg();
		List<Object> list = new ArrayList<Object>();
		Page<LoanContractDO> pageResult = loanContractService.selectLoanContract(page, loanContractVO);
		if(pageResult != null)
		{
			resultMsg.setCode("1");
			resultMsg.setMsg("查询成功！");
			list.add(pageResult);
		}else
		{
			resultMsg.setCode("0");
			resultMsg.setMsg("查询结果为空！");
		}
		resultMsg.setData(list);
		return resultMsg;
	}
	
	/**
	 * 新增
	 * @param loanContractVO
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg insert(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		//1、规则:同一家企业只能有一张单据处于“新建”和“审批中”状态
		boolean canInsert = loanContractService.canInsert(loanContractVO, userInfo);
		if(!canInsert)
		{
			return new ResultMsg("-1","已存在该企业合同记录！");
		}
		
		//2、生成贷款合同编号
		Long enterpriseid = new Long(userInfo.getEnterpriseid());
		Map<String, Object> submitSingleton = codeRuleProvider.submitSingleton(enterpriseid, Constant.LOAN_CONTRACT_CODERULE_CODE);
		String code = (String)submitSingleton.get("msg");
		
		//3、判断贷款合同编号是否已经存在
		boolean codeExists = loanContractService.isCodeExists(code, userInfo);
		if(codeExists)
		{
			return new ResultMsg("-2","该贷款合同单号已经存在！");
		}
		
		//插入操作
		loanContractVO.setCode(code);
		int line = loanContractService.insert(loanContractVO, userInfo);
		
		String Pk_loan_contract = loanContractVO.getPk_loan_contract().toString();
		String workFlowName = Constant.LOAN_CONTRACT_APPROVE_NAME;
		String workFlowCode = Constant.LOAN_CONTRACT_APPROVE_CODE;
		
		if(Constant.LOAN_CONTRACT_STATE_APPROVING.equals(loanContractVO.getState()))
		{
			//创建审核流程
			ResultMsg resultMsg = activitiUtilService.startApprove(workFlowName, Pk_loan_contract, workFlowCode, userInfo);
			if(!"1".equals(resultMsg.getCode()))
			{
				return new ResultMsg("0","创建审核流程失败！");
			}else
			{
				return new ResultMsg("1","保存并提交成功！");
			}
		}else
		{
			if(line == 1)
			{
				return new ResultMsg("1","保存成功！");
			}else
			{
				return new ResultMsg("0","保存失败！");
			}
		}
	}
	
	/**
	 * 删除（作废）
	 * @param loanContractDO
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg delete(LoanContractDOList loanContractDOList, SystemUserInfoT userInfo)
	{
		List<LoanContractDO> deleteList = loanContractDOList.getLoanContractDOList();
		if(deleteList == null || deleteList.size() <= 0)
		{
			return new ResultMsg("0","删除失败！");
		}
		int line = loanContractService.delete(deleteList, userInfo);
		if(line >= 1)
		{
			return new ResultMsg("1","删除成功！");
		}else
		{
			return new ResultMsg("0","删除失败！");
		}
	}
	
	/**
	 * 编辑
	 * @param loanContractDO
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg modify(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		int line = loanContractService.modify(loanContractVO, userInfo);
		
		String Pk_loan_contract = loanContractVO.getPk_loan_contract().toString();
		String workFlowName = Constant.LOAN_CONTRACT_APPROVE_NAME;
		String workFlowCode = Constant.LOAN_CONTRACT_APPROVE_CODE;
		
		if(Constant.LOAN_CONTRACT_STATE_APPROVING.equals(loanContractVO.getState()))
		{
			//创建审核流程
			ResultMsg resultMsg = activitiUtilService.startApprove(workFlowName, Pk_loan_contract, workFlowCode, userInfo);
			if(!"1".equals(resultMsg.getCode()))
			{
				return new ResultMsg("0","创建审核流程失败！");
			}else
			{
				return new ResultMsg("1","保存并提交成功！");
			}
		}else
		{
			if(line == 1)
			{
				return new ResultMsg("1","保存成功！");
			}else
			{
				return new ResultMsg("0","保存失败！");
			}
		}
	}
	
	/**
	 * 提交流程
	 * @param loanContractDO
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg submitModel(LoanContractDO loanContractDO,	SystemUserInfoT userInfo) 
	{
		String pk_loan_contract = loanContractDO.getPk_loan_contract().toString();
		String workFlowName = Constant.LOAN_CONTRACT_APPROVE_NAME;
		String workFlowCode = Constant.LOAN_CONTRACT_APPROVE_CODE;
		LoanContractVO loanContractVO = new LoanContractVO();
		BeanUtils.copyProperties(loanContractDO, loanContractVO);
		loanContractVO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		
		ResultMsg resultMsg = activitiUtilService.startApprove(workFlowName, pk_loan_contract, workFlowCode, userInfo);
		if ("1".equals(resultMsg.getCode())) 
		{
			loanContractVO.setState(Constant.LOAN_CONTRACT_STATE_APPROVING);
			int line = loanContractService.submitModel(loanContractVO, userInfo);
			if(1 != line)
			{
				return new ResultMsg("0", "提交失败！");
			}
		} else 
		{
			return resultMsg;
		}
		return new ResultMsg("1", "提交成功");
	}
	
	/**
	 * 审核
	 * @param loanContractVO
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg approve(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		//------------------------------------最后一次审核通过------------------------------------------------
		if ("agree".equals(loanContractVO.getType()))
		{
			String pk_loan_contract = loanContractVO.getPk_loan_contract().toString();
			String workFlowCode = Constant.LOAN_CONTRACT_APPROVE_CODE;
			String lastNodeName = Constant.LOAN_CONTRACT_APPROVE_LASTTASK;
			String enterpriseName = loanContractVO.getEnterprise_name() == null ? "" : loanContractVO.getEnterprise_name();
			boolean isLastNode = activitiUtilService.isNode(pk_loan_contract, workFlowCode, lastNodeName);
			if (isLastNode)
			{
				//1、将当前企业状态为已审核的贷款合同更新为作废状态(规则:同一家企业只能有一张单据处于“已审核”状态。)
				LoanContractVO tempVO = loanContractVO;
				tempVO.setState(Constant.LOAN_CONTRACT_STATE_DELETE);//设置状态为已作废
				tempVO.setCancelid(new Long(userInfo.getUserId()));
				tempVO.setCancelname(userInfo.getUserName());
				tempVO.setCanceldate(new Date());
				int cancelLine = loanContractService.cancelByEnterpriseID(tempVO, userInfo);
				if(cancelLine > 0)
				{
					log.info("----------------作废一条企业贷款合同记录！-----------------");
				}else
				{
					log.info("----------------企业[" + enterpriseName + "]没有需要作废的贷款合同记录！-----------------");
				}
				
				//2、最后一次审核通过向分账结算表（smf_enterprise_settlement）里面插入一条记录，若分账结算表中已有该企业的信息，则不需要插入
				boolean x = enterpriseSettlementService.isExistsGivenEnterprise(loanContractVO.getPk_enterprise(),
						new Long(userInfo.getEnterpriseid()));
				if(!x)
				{
					//--------------------------生成企业结算单号--------------------------------------
					String enterpriseSettlementCode = "";//企业结算单号
					Long enterpriseid = new Long(userInfo.getEnterpriseid());
					Map<String, Object> submitSingleton = codeRuleProvider.submitSingleton(enterpriseid, Constant.ENTERPRISE_SETTLEMENT_CODERULE_CODE);
					enterpriseSettlementCode = (String)submitSingleton.get("msg");
					//--------------------------生成企业结算单号--------------------------------------
					boolean codeExists = enterpriseSettlementService.isCodeExists(enterpriseSettlementCode, userInfo);
					if(codeExists)
					{
						return new ResultMsg("0", "企业结算单号重复！");
					}
					if("".equals(enterpriseSettlementCode) || enterpriseSettlementCode == null)
					{
						return new ResultMsg("0", "企业结算单号为空！");
					}
					EnterpriseSettlementDO enterpriseSettlementDO = new EnterpriseSettlementDO();
					enterpriseSettlementDO.setCode(enterpriseSettlementCode);//企业结算单号
					enterpriseSettlementDO.setName("");//企业结算单名称
					enterpriseSettlementDO.setState("");
					enterpriseSettlementDO.setAmount(new Double(0.0));//必填项
					enterpriseSettlementDO.setLogistics_cost(new Double(0.0));
					enterpriseSettlementDO.setSupervision_cost(new Double(0.0));
					enterpriseSettlementDO.setPk_enterprise(loanContractVO.getPk_enterprise());//必填项
					enterpriseSettlementDO.setEnterprise_code(loanContractVO.getEnterprise_code());
					enterpriseSettlementDO.setEnterprise_name(enterpriseName);//必填项
					enterpriseSettlementDO.setAddress(loanContractVO.getAddress());
					enterpriseSettlementDO.setContact(loanContractVO.getContact());
					enterpriseSettlementDO.setContact_number(loanContractVO.getContact_number());
					enterpriseSettlementDO.setCurrency_name(loanContractVO.getCurrency_name());//必填项
					enterpriseSettlementDO.setPk_currency(loanContractVO.getPk_currency());
					enterpriseSettlementDO.setCredit_code(loanContractVO.getCredit_code());
					enterpriseSettlementDO.setBookindate(new Date());
					enterpriseSettlementDO.setBusi_date(new Date());//必填项
					enterpriseSettlementDO.setInputmanid(new Long(userInfo.getUserId()));
					enterpriseSettlementDO.setInputmanname(userInfo.getUserName());
					enterpriseSettlementDO.setEnterpriseid(new Long(userInfo.getEnterpriseid()));
					int insertLine = enterpriseSettlementService.insert(enterpriseSettlementDO, userInfo);
					if(insertLine != 1)
					{
						return new ResultMsg("0", "插入企业结算表失败！");
					}
					log.info("------------------------成功向企业结算表插入一条记录！----------------------------");
				}
			}
		}
		//------------------------------------最后一次审核通过------------------------------------------------
		
		ResultMsg resultMsg = loanContractService.approve(loanContractVO, userInfo);
		return resultMsg;
	}
	
	/**
	 * 变更
	 * @param loanContractDO
	 * @param userInfo
	 * @return
	 */
	/*@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg change(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		ResultMsg resultMsg = insert(loanContractVO, userInfo);
		return resultMsg;
		
	}*/
	
	/**
	 * 查看流程进度
	 * @param loanContractDO
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg viewProcess(LoanContractDO loanContractDO, SystemUserInfoT userInfo) 
	{
		if(loanContractService.checkLoanContractTimeStamp(loanContractDO, userInfo))
		{
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		ResultMsg resultMsg = activitiUtilService.getFlowPlan(Constant.LOAN_CONTRACT_APPROVE_CODE,String.valueOf(loanContractDO.getPk_loan_contract()));
		
		return resultMsg;
	}
	
	/**
	 * 根据ID查询贷款合同
	 * @param loanContractVO
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly = true,rollbackFor = Exception.class)
	public LoanContractDO selectLoanContractByID(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		LoanContractDO loanContractDO = loanContractService.selectLoanContractByID(loanContractVO, userInfo);
		return loanContractDO;
	}
	
	/**
	 * 审核角色校验
	 * @param loanContractDO
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public ResultMsg checkApproveById(LoanContractDO loanContractDO, SystemUserInfoT userInfo) 
	{
		if(loanContractService.checkLoanContractTimeStamp(loanContractDO, userInfo))
		{
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		
		String roleName = activitiUtilService.getApproveRoleCodes(loanContractDO.getPk_loan_contract(), Constant.LOAN_CONTRACT_APPROVE_CODE, userInfo);
		log.info("***************** 调用roleProvider ***************************");
		String userRoleCodes = roleProvider.getRoleByUser(userInfo.getUserName());
		log.info("***************** 调用roleProvider结果 ***************************" + userRoleCodes);
		
		String[] strs = userRoleCodes.split(",");
		boolean hasRole = false;
		for (String str : strs) 
		{
			RoleInfo role = roleProvider.getRoleByRoleId(Integer.valueOf(str));
			if (roleName.equals(role.getRoleCode())) 
			{
				hasRole = true;
			}
		}
		log.info("***************** hasRole结果 ***************************" + hasRole);
		
		if (hasRole) 
		{
			return new ResultMsg("1", "可以审批!");
		} else 
		{
			return new ResultMsg("0", "该用户不能审批该数据！");
		}
	}
	
	/**
	 * 获取附件列表
	 */
	@Transactional(readOnly = true,rollbackFor = Exception.class)
	public ResultMsg getFileListById(Long capitalInjectionId, SystemUserInfoT userInfo) 
	{
		ResultMsg msg = new ResultMsg();
		List<AttachmentsVO> list = attachmentsService.getFileList(capitalInjectionId,Constant.LOAN_CONTRACT_FILE_CODE,userInfo);
		List<Object> objList = new LinkedList<Object>();
		objList.addAll(list);
		msg.setCode("1");
		msg.setData(objList);
		msg.setMsg("查询成功");
		return msg;
	}
	
	/**
	 * 添加附件
	 */
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg addFile(AttachmentsDO attachmentsDO, SystemUserInfoT userInfo) throws MsgException 
	{
		attachmentsDO.setSourcebilltype(Constant.LOAN_CONTRACT_FILE_CODE);
		int m = attachmentsService.addFile(attachmentsDO, userInfo);
		if (m != 1) 
		{
			return new ResultMsg("0","保存附件失败");
		}
		return new ResultMsg("1","保存附件成功");
	}

	/**
	 * 删除附件
	 */
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public int deleteFile(AttachmentsDO bean, SystemUserInfoT userInfo) throws MsgException 
	{
		return attachmentsService.deleteFile(bean, userInfo);
	}
	
	@Transactional(readOnly = true)
    public List<EnterpriseVO> getEnterpriseByName(String keyword,Long enterpriseid) {
		return enterpriseRestProvider.selectByName(keyword, enterpriseid,Constant.ENTERPRISE_OPERATION);
	}
	
	@Transactional(readOnly = true)
    public EnterpriseVO getEnterpriseByCode(String code,Long enterpriseid) {
		return enterpriseRestProvider.selectByCode(code, enterpriseid);
	}
	
}
