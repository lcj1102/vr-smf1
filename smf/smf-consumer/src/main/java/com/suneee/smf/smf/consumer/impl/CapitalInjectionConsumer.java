package com.suneee.smf.smf.consumer.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.suneee.smf.smf.api.provider.CollectionConfirmProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AttachmentsDO;
import com.suneee.smf.smf.model.AttachmentsVO;
import com.suneee.smf.smf.model.CapitalInjectionDO;
import com.suneee.smf.smf.model.CapitalInjectionInsertDO;
import com.suneee.smf.smf.model.CapitalInjectionVO;
import com.suneee.smf.smf.model.CollectionConfirmDO;
import com.suneee.smf.smf.service.ActivitiUtilService;
import com.suneee.smf.smf.service.AttachmentsService;
import com.suneee.smf.smf.service.CapitalInjectionService;


@Service("capitalInjectionConsumer")
public class CapitalInjectionConsumer {
	
	private static final Logger log=LoggerFactory.getLogger(CapitalInjectionConsumer.class);
	
	@Autowired
	private CapitalInjectionService capitalInjectionService;
	
	 @Autowired
	private ActivitiUtilService activitiUtilService;
	
	@Autowired
	private AttachmentsService attachmentsService;
	
	@Reference(version="1.0",url="dubbo://172.16.36.68:20917/com.suneee.scn.system.api.provider.RoleProvider")
	private RoleProvider roleProvider;
	
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;
	
	//测试环境url
  	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.EnterpriseRestProvider")   
  	private EnterpriseRestProvider enterpriseRestProvider;
  	
    //测试环境url
  	@Reference(url="dubbo://172.16.36.68:25017/com.suneee.smf.smf.api.provider.CollectionConfirmProvider")
  	private CollectionConfirmProvider collectionConfirmProvider;
	
	/**
	 * 分页查询
	 */
	@Transactional(readOnly = true)
	public Page<CapitalInjectionVO> queryPage(Page<CapitalInjectionVO> page, CapitalInjectionVO bean) {
		return capitalInjectionService.queryPage(page,bean);
	}
	
	/**
	 * 新增
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg insert(CapitalInjectionDO capitalInjectionDO, SystemUserInfoT userInfo) {
		if (capitalInjectionDO == null) {
            return new ResultMsg("0", "参数不能为空");
        }
		
		Map<String,Object> map = codeRuleRestProvider.submitSingleton(userInfo.getEnterpriseid().longValue(), Constant.CAPITAL_INJECTION_CODERULE_CODE);
		if ("1".equals(map.get("code"))) {
			if ("".equals((String) map.get("msg"))) {
				return new ResultMsg("0","生成资金注入单号失败！！！");
			}
			capitalInjectionDO.setCode((String) map.get("msg"));
			return capitalInjectionService.insert(capitalInjectionDO,userInfo);
		}
		return new ResultMsg("0","新增失败");
		
	}
	
	/**
	 * 删除
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg delete(CapitalInjectionInsertDO insertDO, SystemUserInfoT userInfo) {
		
		int count = 0;
		for (CapitalInjectionDO bean : insertDO.getInsertDO()) {
			
			
			int m = capitalInjectionService.delete(bean,userInfo);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
			count += m;
		}
		if (count > 0) {
			return new ResultMsg("1", "删除成功！！");
		} 
		return new ResultMsg("0", "删除失败！！");
	}
	
	/**
	 * 根据id查询任务
	 */
	@Transactional(readOnly = true)
	public CapitalInjectionVO getById(CapitalInjectionDO capitalInjectionDO) {
		
		return capitalInjectionService.getById(capitalInjectionDO);
		
	}
	
	/**
	 * 编辑
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg modify(CapitalInjectionDO capitalInjectionDO,SystemUserInfoT userInfo) {
		
		return capitalInjectionService.modify(capitalInjectionDO,userInfo);
		
	}
	
	/**
	 * 提交进入审批流程
	 */
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg submitModel(CapitalInjectionVO capitalInjectionVO, SystemUserInfoT userInfo) {
		return capitalInjectionService.submitModel(capitalInjectionVO, userInfo);
	}
	

	/**
	 * 根据id判断当前登录页面是否具有审批权限
	 */
	@Transactional(readOnly = true)
	public ResultMsg checkApproveById(CapitalInjectionVO capitalInjectionVO, SystemUserInfoT userInfo) {
		
		CapitalInjectionDO bean = new CapitalInjectionDO();
		bean.setPk_capital_injection(capitalInjectionVO.getPk_capital_injection());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setTime_stamp(capitalInjectionVO.getTime_stamp());
		
		if(capitalInjectionService.checkTimeStamp(bean)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		
		String roleName = capitalInjectionService.getApproveRoleCodes(capitalInjectionVO.getPk_capital_injection(),userInfo);
		log.info("*****************调用roleProvider***************************");
		String userRoleCodes = roleProvider.getRoleByUser(userInfo.getUserName());
		log.info("*****************调用roleProvider结果***************************"+userRoleCodes);
		String[] strs = userRoleCodes.split(",");
		boolean hasRole = false;
		for (String str : strs) {
			RoleInfo role = roleProvider.getRoleByRoleId(Integer.valueOf(str));
			if (roleName.equals(role.getRoleCode())) {
				hasRole = true;
			}
		}
		log.info("*****************hasRole结果***************************"+hasRole);
		if (hasRole) {
			return new ResultMsg("1", "可以审批");
		} else {
			return new ResultMsg("0", "该用户不能审批该数据！");
		}
	}
	
	/**
	 * 审批
	 */
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg approve(CapitalInjectionVO capitalInjectionVO, SystemUserInfoT userInfo) throws MsgException {
		
		if ("agree".equals(capitalInjectionVO.getType())) { 
			boolean isLastNode = activitiUtilService.isNode(capitalInjectionVO.getPk_capital_injection(),Constant.CAPITALINJECTION_APPROVE_CODE,Constant.CAPITALINJECTION_APPROVE_LASTTASK);
			if (isLastNode) {// 判断审核流程是否结束
				CapitalInjectionDO capitalInjectionDO = new CapitalInjectionDO();
				capitalInjectionDO.setPk_capital_injection(capitalInjectionVO.getPk_capital_injection());
				capitalInjectionDO.setEnterpriseid(capitalInjectionVO.getEnterpriseid());
				
				CapitalInjectionVO ciVO = capitalInjectionService.getById(capitalInjectionDO);
				CollectionConfirmDO collectionConfirmDO = new CollectionConfirmDO();
				
				Date date = new Date();
				collectionConfirmDO.setBooked_flag("未记账");
				collectionConfirmDO.setBookindate(date);
				collectionConfirmDO.setBusi_date(date);
				collectionConfirmDO.setCollection_amount(ciVO.getAmount() * 10000);
				collectionConfirmDO.setContact(ciVO.getContact());
				collectionConfirmDO.setContact_number(ciVO.getContact_number());
				collectionConfirmDO.setCurrency_name(ciVO.getCurrency_name());
				collectionConfirmDO.setEnterprise_payment_name(ciVO.getEnterprise_name());
				collectionConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
				collectionConfirmDO.setInputmanid(ciVO.getInputmanid());
				collectionConfirmDO.setInputmanname(ciVO.getInputmanname());
				collectionConfirmDO.setLogistics_cost(0d);
				collectionConfirmDO.setOverflow_amount(0d);
				collectionConfirmDO.setPayment_billno("" + new Random().nextInt(500));
				collectionConfirmDO.setPayment_method("银行转账");
				collectionConfirmDO.setPk_enterprise_payment(ciVO.getPk_enterprise());
				collectionConfirmDO.setRepayment_interest(0d);
				collectionConfirmDO.setRepayment_principal(0d);
				collectionConfirmDO.setSourcebillid(ciVO.getPk_capital_injection());
				collectionConfirmDO.setSourcebilltype(Constant.COLLECTION_SOURCE_BILL_TYPE_CAPITALINJECTION);
				collectionConfirmDO.setState(Constant.COLLECTION_STATE_APPROVED);
				collectionConfirmDO.setSupervision_cost(0d);
				
				ResultMsg providerMsg = collectionConfirmProvider.add(collectionConfirmDO, Constant.COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL);
				if("0".equals(providerMsg.getCode())){
					throw new MsgException("0",providerMsg.getMsg());
				}
			}
		}
		
		return capitalInjectionService.approve(capitalInjectionVO,userInfo);
		
	}
	
	/**
	 * 查看审批进度
	 */
	public ResultMsg approveLog(CapitalInjectionVO capitalInjectionVO, SystemUserInfoT userInfo) {
		CapitalInjectionDO capitalInjectionDO = new CapitalInjectionDO();
		capitalInjectionDO.setPk_capital_injection(capitalInjectionVO.getPk_capital_injection());
		capitalInjectionDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		capitalInjectionDO.setTime_stamp(capitalInjectionVO.getTime_stamp());
		
		if(capitalInjectionService.checkTimeStamp(capitalInjectionDO)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		
		return capitalInjectionService.approveLog(capitalInjectionVO);
	}
	
	/**
	 * 获取附件列表
	 */
	public ResultMsg getFileListById(Long capitalInjectionId,SystemUserInfoT userInfo) {
		ResultMsg msg = new ResultMsg();
		List<AttachmentsVO> list = attachmentsService.getFileList(capitalInjectionId,Constant.CAPITALINJECTION_FILE_CODE,userInfo);
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
	public ResultMsg addFile(AttachmentsDO attachmentsDO,SystemUserInfoT userInfo) throws MsgException {
		attachmentsDO.setSourcebilltype(Constant.CAPITALINJECTION_FILE_CODE);
		int m = attachmentsService.addFile(attachmentsDO, userInfo);
		if (m != 1) {
			return new ResultMsg("0","保存附件失败");
		}
		return new ResultMsg("1","保存附件成功");
	}

	/**
	 * 删除附件
	 */
	public int deleteFile(AttachmentsDO bean, SystemUserInfoT userInfo) throws MsgException {
		return attachmentsService.deleteFile(bean, userInfo);
	}
	
	 public List<CapitalInjectionVO> getCapitalInjectionByName(String name, Long enterprised){
		 return capitalInjectionService.selectByName(name,enterprised);
	 }
	 
	 public CapitalInjectionVO getCapitalInjectionByCode(String name, Long enterprised){
		 return capitalInjectionService.selectByCode(name,enterprised);
	 }
	
	 @Transactional(readOnly = true)
		public List<EnterpriseVO> selectEnterpriseByName(String keyword,Long enterpriseid) {
			return enterpriseRestProvider.selectByName(keyword, enterpriseid,Constant.ENTERPRISE_INVESTMENT);
		}
		
		@Transactional(readOnly = true)
	    public EnterpriseVO getEnterpriseByCode(String code,Long enterpriseid) {
			return enterpriseRestProvider.selectByCode(code, enterpriseid);
		}
		
}
