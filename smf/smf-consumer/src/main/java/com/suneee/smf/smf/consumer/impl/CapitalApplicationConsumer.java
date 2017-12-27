package com.suneee.smf.smf.consumer.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.suneee.smf.smf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.scf.api.provider.CodeRuleRestProvider;
import com.suneee.scn.scf.api.provider.EnterpriseRestProvider;
import com.suneee.scn.system.api.provider.RoleProvider;
import com.suneee.scn.system.model.vo.RoleInfo;
import com.suneee.smf.smf.api.provider.AdvanceConfirmProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.service.ActivitiUtilService;
import com.suneee.smf.smf.service.AdvanceConfirmService;
import com.suneee.smf.smf.service.AttachmentsService;
import com.suneee.smf.smf.service.CapitalApplicationService;
import com.suneee.smf.smf.service.EnterpriseSettlementService;
/**
 * @Description: 资金使用申请
 * @author: 致远
 * @date: 2017年12月1日 下午4:07:07
 */
@Service("capitalApplicationConsumer")
public class CapitalApplicationConsumer {

	private static final Logger log= LoggerFactory.getLogger(SaleApplicationConsumer.class);

	@Autowired
	private CapitalApplicationService capitalApplicationService;
	
	@Autowired
	private AttachmentsService attachmentsService;
	
	@Autowired
	private AdvanceConfirmService advanceConfirmService;
	
	@Autowired
	private EnterpriseSettlementService enterpriseSettlementService;
	
	@Reference(version="1.0",url="dubbo://172.16.36.68:20917/com.suneee.scn.system.api.provider.RoleProvider")
	private RoleProvider roleProvider;
	
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.EnterpriseRestProvider")
	private EnterpriseRestProvider enterpriseRestProvider;
	
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;
	
	@Reference(url="dubbo://172.16.36.68:25017/com.suneee.smf.smf.api.provider.AdvanceConfirmProvider")
	private AdvanceConfirmProvider advanceConfirmProvider;
	
	@Autowired
	private ActivitiUtilService activitiUtilService;
	
	public Page<CapitalApplicationVO> listByPage(Page<CapitalApplicationVO> page,CapitalApplicationDO applicationDO) {
		return capitalApplicationService.listByPage(page,applicationDO);
	}

	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg addCapitalApplication(CapitalApplicationDO laDO,SystemUserInfoT userInfo) throws MsgException {
		Map<String,Object> map = codeRuleRestProvider.submitSingleton(userInfo.getEnterpriseid().longValue(), Constant.CAPITALAPPLICATION_CODERULE_CODE);
		if ("1".equals(map.get("code"))) {
			if ("".equals((String) map.get("msg"))) {
				return new ResultMsg("0","生成申请单号失败！！！");
			}
		}
		laDO.setCode((String) map.get("msg"));
		
		return capitalApplicationService.addCapitalApplication(laDO,userInfo);
	}

	public CapitalApplicationVO getRestByPrimaryKey(CapitalApplicationVO vo) {
		return capitalApplicationService.getRestByPrimaryKey(vo);
	}

	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg modifyCapitalApplication(CapitalApplicationDO laDO,String delItemId, SystemUserInfoT userInfo) throws MsgException {
		return capitalApplicationService.modifyCapitalApplication(laDO,delItemId,userInfo);
	}

	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg deleteCapitalApplication(CapitalApplicationInsertDO insertDO, SystemUserInfoT userInfo) throws MsgException {
		int count = 0;
		for (CapitalApplicationDO bean : insertDO.getCapitalApplicationDO()) {
			count += capitalApplicationService.deleteCapitalApplication(bean,userInfo);
		}
		if (count > 0) {
			return new ResultMsg("1", "删除成功！！");
		}
		return new ResultMsg("0", "该数据已失效，请刷新！！");
	}

	public ResultMsg checkApproveById(CapitalApplicationVO modelVO,SystemUserInfoT userInfo) {
		String roleName = capitalApplicationService.getApproveRoleCodes(modelVO.getPk_capital_application(),userInfo);
		String userRoleCodes = roleProvider.getRoleByUser(userInfo.getUserName());
		String[] strs = userRoleCodes.split(",");
		boolean hasRole = false;
		for (String str : strs) {
			RoleInfo role = roleProvider.getRoleByRoleId(Integer.valueOf(str));
			if (roleName.equals(role.getRoleCode())) {
				hasRole = true;
			}
		}
		if (hasRole) {
			return new ResultMsg("1", "可以审批");
		} else {
			return new ResultMsg("0", "该用户不能审批该数据！");
		}
	}

	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg approveSelect(CapitalApplicationVO modelVO,SystemUserInfoT userInfo) throws MsgException {
		ResultMsg approveMsg = capitalApplicationService.approveSelect(modelVO,userInfo);
		if("0".equals(approveMsg.getCode())){
			return approveMsg;
		}
		long start = 0L;
		if ("agree".equals(modelVO.getType())) {
			boolean isLastNode = activitiUtilService.isNode(modelVO.getPk_capital_application(),Constant.CAPITALAPPLICATION_APPROVE_CODE,Constant.CAPITALAPPLICATION_APPROVE_LASTTASK);
			if (isLastNode) {
				modelVO = capitalApplicationService.getRestByPrimaryKey(modelVO);
				AdvanceConfirmDO confirmDO = new AdvanceConfirmDO();
				//编码后面provider接口
				/*Map<String,Object> map = codeRuleRestProvider.submitSingleton(userInfo.getEnterpriseid().longValue(), Constant.ADVANCE_CONFIRM_CODERULE_CODE);
				if ("1".equals((String)map.get("code"))) {
					if ("".equals((String) map.get("msg"))) {
						throw new MsgException("0", "生成放款确认单号失败！！！");
					}
					confirmDO.setCode((String) map.get("msg"));
				}else{
					throw new MsgException("0", "生成放款确认单号失败！！！");
				}*/
				confirmDO.setPk_enterprise_application(modelVO.getPk_enterprise());
				confirmDO.setEnterprise_application_name(modelVO.getEnterprise_name());
				confirmDO.setContact(modelVO.getContact());
				confirmDO.setContact_number(modelVO.getContact_number());
				confirmDO.setApplication_amount(modelVO.getAmount());
				confirmDO.setAdvances_amount(modelVO.getAmount());
				confirmDO.setBusi_date(new Date());
				confirmDO.setPk_currency(modelVO.getPk_currency());
				confirmDO.setCurrency_name(modelVO.getCurrency_name());
				confirmDO.setApplication_date(modelVO.getBusi_date());
				confirmDO.setSourcebillid(modelVO.getPk_capital_application());
				confirmDO.setSourcebilltype(Constant.ADVANCE_CONFIRM_CAPITAL_APPLICATION);
				confirmDO.setBank_account("63225885522552212112");
				confirmDO.setBank_deposit("上海银行嘉定支行");
				confirmDO.setInputmanname(modelVO.getInputmanname());
				confirmDO.setBookindate(modelVO.getBookindate());
				confirmDO.setPayment_billno(""+new Random().nextInt(500));
				confirmDO.setPayment_method("银行转账");
				log.info("调用放款确认provider==================================");

				start = System.currentTimeMillis();
				ResultMsg providerMsg = advanceConfirmProvider.add(confirmDO,Constant.ADVANCE_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL);
				log.info("调用放款确认成功==================================");
				log.info("调用放款确认成功后返回数据:"+providerMsg.toString()+"===============");
				if("0".equals(providerMsg.getCode())){
					throw new MsgException("0",providerMsg.getMsg());
				}
			}
		}
		activitiUtilService.approve(Constant.CAPITALAPPLICATION_APPROVE_CODE, String.valueOf(modelVO.getPk_capital_application()), modelVO.getOption(), String.valueOf(userInfo.getUserId()), userInfo, Integer.parseInt(approveMsg.getHtml()));
		long diff = start - System.currentTimeMillis();
		log.info("本地需要睡眠时间================================" + diff);
		return new ResultMsg("1", "审核成功！");
	}

	public ResultMsg approveLog(CapitalApplicationVO modelVO,SystemUserInfoT userInfo) {
		return capitalApplicationService.approveLog(modelVO,userInfo);
	}

	public ResultMsg getFileListById(Long capitalApplicationId,SystemUserInfoT userInfo) {
		ResultMsg msg = new ResultMsg();
		List<AttachmentsVO> list = attachmentsService.getFileList(capitalApplicationId,Constant.CAPITALAPPLICATION_FILE_CODE,userInfo);
		List<Object> objList = new LinkedList<Object>();
		objList.addAll(list);
		msg.setCode("1");
		msg.setData(objList);
		msg.setMsg("查询成功");
		return msg;
	}

	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg addFile(AttachmentsInsertDO file,SystemUserInfoT userInfo) throws MsgException {
		
		int m = attachmentsService.addFile(file, userInfo);
		if (m != 1) {
			return new ResultMsg("1","保存附件失败");
		}
		return new ResultMsg("1","保存附件成功");
	}

	public int deleteFile(AttachmentsDO bean, SystemUserInfoT userInfo) throws MsgException {
		return attachmentsService.deleteFile(bean, userInfo);
	}

	public ResultMsg getEnterpriseList(String keyword, SystemUserInfoT userInfo) {
		ResultMsg msg = new ResultMsg();
		List<EnterpriseSettlementVO> list = enterpriseSettlementService.selectByName(keyword, userInfo.getEnterpriseid().longValue());
		List<Object> objList = new LinkedList<Object>();
		objList.addAll(list);
		msg.setCode("1");
		msg.setData(objList);
		msg.setMsg("查询成功");
		return msg;
	}

	public Object getEnterpriseByCode(String code, SystemUserInfoT userInfo) {
		return enterpriseSettlementService.selectByCode(code, userInfo.getEnterpriseid().longValue());
	}

	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg submitModel(CapitalApplicationInsertDO insertDO,SystemUserInfoT userInfo) throws MsgException {
		int count = 0;
		for (CapitalApplicationDO bean : insertDO.getCapitalApplicationDO()) {
			count += capitalApplicationService.submitModel(bean,userInfo);
		}
		if (count > 0) {
			return new ResultMsg("1", "提交成功！！");
		}
		return new ResultMsg("0", "该数据已失效，请刷新！！");
	}

	public ResultMsg getCapitalApplicationList(String keyword,Long enterpriseId, SystemUserInfoT userInfo) {
		ResultMsg msg = new ResultMsg();
		List<CapitalApplicationVO> list = capitalApplicationService.getAllList(keyword,enterpriseId,userInfo);
		List<Object> objList = new LinkedList<Object>();
		objList.addAll(list);
		msg.setCode("1");
		msg.setData(objList);
		msg.setMsg("查询成功");
		return msg;
	}

	public CapitalApplicationVO getCapitalApplicationByCode(CapitalApplicationDO vo) {
		return capitalApplicationService.getCapitalApplicationByCode(vo);
	}
	
	/*
	 * 获取细单中的数量为可发货最大数量
	 */
	public CapitalApplicationVO selectCapitalApplicationByCode(CapitalApplicationDO vo) {
		return capitalApplicationService.selectCapitalApplicationByCode(vo);
	}
}
