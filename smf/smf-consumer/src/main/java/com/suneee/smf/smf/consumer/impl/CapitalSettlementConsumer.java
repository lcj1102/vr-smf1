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
import com.suneee.scn.system.api.provider.RoleProvider;
import com.suneee.scn.system.model.vo.RoleInfo;
import com.suneee.smf.smf.api.provider.AdvanceConfirmProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AdvanceConfirmDO;
import com.suneee.smf.smf.model.AttachmentsDO;
import com.suneee.smf.smf.model.AttachmentsVO;
import com.suneee.smf.smf.model.CapitalSettlementDO;
import com.suneee.smf.smf.model.CapitalSettlementInsertDO;
import com.suneee.smf.smf.model.CapitalSettlementVO;
import com.suneee.smf.smf.service.ActivitiUtilService;
import com.suneee.smf.smf.service.AdvanceConfirmService;
import com.suneee.smf.smf.service.AttachmentsService;
import com.suneee.smf.smf.service.CapitalSettlementService;
@Service("capitalSettlementConsumer")
public class CapitalSettlementConsumer {

	private static final Logger log=LoggerFactory.getLogger(CapitalSettlementConsumer.class);
	@Autowired
	private CapitalSettlementService capitalSettlementService;
	
	@Autowired
	private ActivitiUtilService activitiUtilService;
	
	@Autowired
	private AttachmentsService attachmentsService;
	@Reference(version="1.0",url="dubbo://172.16.36.68:20917/com.suneee.scn.system.api.provider.RoleProvider")
	private RoleProvider roleProvider;
	@Autowired
	private AdvanceConfirmService advanceConfirmService;
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.EnterpriseRestProvider")
	private EnterpriseRestProvider enterpriseRestProvider;
	@Reference(url="dubbo://172.16.36.68:25017/com.suneee.smf.smf.api.provider.AdvanceConfirmProvider")   
	private AdvanceConfirmProvider advanceConfirmProvider;
	//列表查询
	@Transactional(readOnly = true)
	public Page<CapitalSettlementVO> queryCapitalSettlementByPage(Page<CapitalSettlementVO> page,
			CapitalSettlementVO bean) {
		return capitalSettlementService.queryCapitalSettlementByPage(page,bean);
	}
	//增加
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg addCapitalSettlement(CapitalSettlementDO capitalSettlementDO,
			SystemUserInfoT userInfo) {
		if (capitalSettlementDO == null) {
            return new ResultMsg("0", "数据为空");
        } 
		if (capitalSettlementService.selectCapitalSettlementByStatus(capitalSettlementDO,userInfo) != null) {
			return new ResultMsg("0", "已存在该资金注入合同号退资申请单");
		} 

		Map<String,Object> map = codeRuleRestProvider.submitSingleton(userInfo.getEnterpriseid().longValue(), Constant.CAPITALSETTLEMENT_CODERULE_CODE);
		if ("1".equals(map.get("code"))) {
			if ("".equals((String) map.get("msg"))) {
				return new ResultMsg("0","生成申请单号失败！！！");
			}
			capitalSettlementDO.setCode((String) map.get("msg"));
			return capitalSettlementService.addCapitalSettlement(capitalSettlementDO,userInfo);
		}
        return new ResultMsg("0","新增失败");
	}
	public CapitalSettlementVO getRestByPrimaryKey(CapitalSettlementVO vo) {
		return capitalSettlementService.getRestByPrimaryKey(vo);
	}
    /**
     * 删除选中资金退出结算单
     */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg deleteCapitalSettlementDO(
			CapitalSettlementInsertDO capitalSettlementDO,
			SystemUserInfoT userInfo) {
		int count = 0;
		for (CapitalSettlementDO bean : capitalSettlementDO.getCapitalSettlementDOList()) {
			count += capitalSettlementService.deleteCapitalSettlementDO(bean,userInfo);
		}
		if (count > 0) {
			return new ResultMsg("1", "作废成功！！");
		}
		return new ResultMsg("0", "该数据已失效，请刷新！！");
	}
    /**
     * 修改资金退出结算单
     */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg modifyCapitalSettlement(CapitalSettlementDO capitalSettlementDO,
			SystemUserInfoT userInfo) {
		if (capitalSettlementDO == null) {
			return new ResultMsg("0", "数据为空");
		} 
		capitalSettlementDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
//		if (capitalSettlementService.checkEnterpriseTimeStamp(capitalSettlementDO)){
//			return new ResultMsg("0","该数据已失效，请刷新");
//		}

        return capitalSettlementService.modifyCapitalSettlement(capitalSettlementDO,userInfo);
	}
	//查询附件列表
	public ResultMsg getFileListById(Long capitalSettlementId,
			SystemUserInfoT userInfo) {
		ResultMsg msg = new ResultMsg();
		List<AttachmentsVO> list = attachmentsService.getFileList(capitalSettlementId,Constant.CAPITALSETTLEMENT_FILE_CODE,userInfo);
		List<Object> objList = new LinkedList<Object>();
		objList.addAll(list);
		msg.setCode("1");
		msg.setData(objList);
		msg.setMsg("查询成功");
		return msg;
	}
	//添加附件
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg addFile(AttachmentsDO attachmentsDO,SystemUserInfoT userInfo) throws MsgException{
		attachmentsDO.setSourcebilltype(Constant.CAPITALSETTLEMENT_FILE_CODE);
		int m = attachmentsService.addFile(attachmentsDO, userInfo);
		if (m != 1) {
			return new ResultMsg("1","保存附件失败");
		}
		return new ResultMsg("1","保存附件成功");
	}
	//删除附件
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public int deleteFile(AttachmentsDO bean, SystemUserInfoT userInfo) throws MsgException {
		return attachmentsService.deleteFile(bean, userInfo);
	}
	//提交
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg submitModel(CapitalSettlementVO capitalSettlementVO,
			SystemUserInfoT userInfo) {
		return capitalSettlementService.submitModel(capitalSettlementVO, userInfo);
	}
	//验证是否有审核权限
	@Transactional(readOnly = true)
	public ResultMsg checkApproveById(CapitalSettlementVO capitalSettlementVO,
			SystemUserInfoT userInfo) {
		String roleName = capitalSettlementService.getApproveRoleCodes(capitalSettlementVO.getPk_capital_settlement(),userInfo);
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
	public ResultMsg approve(CapitalSettlementVO capitalSettlementVO,
			SystemUserInfoT userInfo) throws MsgException {
		if ("agree".equals(capitalSettlementVO.getType())) { 
			boolean isLastNode = activitiUtilService.isNode(capitalSettlementVO.getPk_capital_settlement(),Constant.CAPITALSETTLEMENT_APPROVE_CODE,Constant.CAPITALSETTLEMENT_APPROVE_LASTTASK);
			if (isLastNode) {// 判断审核流程是否结束
				CapitalSettlementVO csVO = capitalSettlementService.getRestByPrimaryKey(capitalSettlementVO);
				
				AdvanceConfirmDO confirmDO = new AdvanceConfirmDO();
				
				//编码后面provider接口
/*				Map<String,Object> map = codeRuleRestProvider.submitSingleton(userInfo.getEnterpriseid().longValue(), Constant.ADVANCE_CONFIRM_CODERULE_CODE);
				if ("1".equals((String)map.get("code"))) {
					if ("".equals((String) map.get("msg"))) {
						throw new MsgException("0", "生成放款确认单号失败！！！");
					}
					confirmDO.setCode((String) map.get("msg"));
				}else{
					throw new MsgException("0", "生成放款确认单号失败！！！");
				}*/
				
				confirmDO.setPk_enterprise_application(csVO.getPk_enterprise());
				confirmDO.setEnterprise_application_name(csVO.getEnterprise_name());
				confirmDO.setContact(csVO.getContact());
				confirmDO.setContact_number(csVO.getContact_number());
				confirmDO.setApplication_amount(csVO.getSettlement_amount()*10000);
				confirmDO.setAdvances_amount(csVO.getSettlement_amount()*10000);
				confirmDO.setPk_currency(csVO.getPk_currency());
				confirmDO.setCurrency_name(csVO.getCurrency_name());
				confirmDO.setApplication_date(csVO.getBusi_date());
				confirmDO.setBusi_date(new Date());
				confirmDO.setBank_account("111111111111");
				confirmDO.setBank_deposit("泽善银行");
				confirmDO.setSourcebillid(csVO.getPk_capital_settlement());
				confirmDO.setSourcebilltype(Constant.ADVANCE_CONFIRM_CAPITAL_SETTLEMENT);
				confirmDO.setInputmanname(csVO.getInputmanname());
				confirmDO.setBookindate(csVO.getBookindate());
				confirmDO.setPayment_billno(new Random().nextInt(500)+"");
				confirmDO.setPayment_method("银行转账");
				ResultMsg m = advanceConfirmProvider.add(confirmDO,Constant.ADVANCE_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL);
			
				if("0".equals(m.getCode())){
					throw new MsgException("0",m.getMsg());
				}
				
	        } 
		}
		
		return capitalSettlementService.approveSelect(capitalSettlementVO,userInfo);
	}
	public ResultMsg approveLog(CapitalSettlementVO modelVO,
			SystemUserInfoT userInfo) {
		return capitalSettlementService.approveLog(modelVO,userInfo);
	}


}
