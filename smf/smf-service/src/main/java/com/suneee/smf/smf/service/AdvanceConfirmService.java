package com.suneee.smf.smf.service;

import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.smf.smf.common.*;
import com.suneee.smf.smf.dao.AdvanceConfirmDao;
import com.suneee.smf.smf.dao.EnterpriseSettlementDao;
import com.suneee.smf.smf.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @Description: 放款管理service
 * @author: 择善
 * @date: 2017年12月5日 上午9:10:34
 */
@Service("advanceConfirmService")
public class AdvanceConfirmService {
	
	@Autowired
	private AdvanceConfirmDao advanceConfirmDao;
	
	@Autowired
	private EnterpriseSettlementDao enterpriseSettlementDao;
	
	@Autowired
	private CapitalSettlementService capitalSettlementService;
	
	@Autowired
	private ActivitiUtilService activitiUtilService;

	@Autowired
	private AdvanceConfirmErrorService advanceConfirmErrorService;
	
	@Autowired
	private EnterpriseSettlementService enterpriseSettlementService;

	public Page<AdvanceConfirmVO> queryAdvanceConfirmByPage(Page<AdvanceConfirmVO> page,
			AdvanceConfirmVO bean) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("searchValue",bean.getSearchValue());
		
		map.put("code",bean.getCode());
		map.put("enterprise_application_name",bean.getEnterprise_application_name());
		map.put("contact",bean.getContact());
		
		map.put("beginForApplicationDate",bean.getBeginForApplicationDate());
		if (bean.getEndForApplicationDate() != null) {
			map.put("endForApplicationDate", DateUnit.icDateByDay(bean.getEndForApplicationDate(),1));
		}
		
		map.put("beginForBusiDate",bean.getBeginForBusiDate());
		if (bean.getEndForBusiDate() != null) {
			map.put("endForBusiDate", DateUnit.icDateByDay(bean.getEndForBusiDate(),1));
		}
		
		map.put("state",bean.getState());
		map.put("enterpriseid",bean.getEnterpriseid());
		int startNum = (page.getPageNo()-1)*(page.getPageSize());
		map.put("startNum",startNum);
		map.put("pageSize",page.getPageSize());
		List<AdvanceConfirmVO> list = advanceConfirmDao.selectByExample1(map);
		page.setResults(list);    //待分页的数据
		int totalCount = (int) advanceConfirmDao.countByExample(map);
		page.setTotalCount(totalCount); // 总条数
		page.setPageCount(totalCount / page.getPageSize() + 1); //总页数
		return page;
	}

	public ResultMsg addAdvanceConfirm(AdvanceConfirmDO advanceConfirmDO, SystemUserInfoT userInfo) {
		
		advanceConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		advanceConfirmDO.setInputmanname(userInfo.getName());
		advanceConfirmDO.setBookindate(new Date());
		advanceConfirmDao.insert(advanceConfirmDO);
		if (Constant.ADVANCE_CONFIRM_STATE_APPROVING.equals(advanceConfirmDO.getState())) {
			return startTaskForAdvanceConfirm(advanceConfirmDO,userInfo);
		}
		return new ResultMsg("1", "保存成功！");
		 
	}
	
	/**
	 * 
	 * @Title: pushAdvanceConfirm 
	 * @Description: 其他模块推送到放款确认模块接口
	 * @param advanceConfirmDO {pk_enterprise_application,enterprise_application_name，
	 * 							contact，contact_number，application_amount，pk_currency，
	 * 							currency_name，application_date，pk_capital_application}
	 * @param userInfo
	 * @return
	 * @return: int
	 * @throws InterruptedException 
	 */
	public int pushAdvanceConfirm(AdvanceConfirmDO advanceConfirmDO, SystemUserInfoT userInfo) throws MsgException, InterruptedException{
		advanceConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		advanceConfirmDO.setInputmanname(userInfo.getName());
		advanceConfirmDO.setBookindate(new Date());
		advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVED);
		advanceConfirmDO.setBooked_flag(Constant.ADVANCE_CONFIRM_BOOK_FLAG_NO);
		int m= advanceConfirmDao.insert(advanceConfirmDO);
		
		if(m == 1){
			if(Constant.ADVANCE_CONFIRM_CAPITAL_APPLICATION.equals(advanceConfirmDO.getSourcebilltype())){
				//资金使用申请来源
				//企业结算表加锁
				enterpriseSettlementService.lockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
				try{
					
					EnterpriseSettlementDO enterpriseSettlementDO = new EnterpriseSettlementDO();
					enterpriseSettlementDO.setPk_enterprise(advanceConfirmDO.getPk_enterprise_application());
					enterpriseSettlementDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
					EnterpriseSettlementVO enterpriseSettlementVO = enterpriseSettlementDao.getByEnterprise(enterpriseSettlementDO);
					
					if(null == enterpriseSettlementVO){
						//企业结算表解锁
						enterpriseSettlementService.unlockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
						throw new MsgException("0","未找到改企业的企业结算记录，请确定该企业是否签订长期借款合同！");
					}
					
					enterpriseSettlementVO.setAmount(enterpriseSettlementVO.getAmount()  + advanceConfirmDO.getAdvances_amount());
					//向企业结算表累加借款金额
					enterpriseSettlementDao.updateAmount(enterpriseSettlementVO);
					//企业结算表解锁
					enterpriseSettlementService.unlockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
					
				}catch(MsgException e){
					//企业结算表解锁
					enterpriseSettlementService.unlockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
					throw e;
				}catch(Exception e){
					//企业结算表解锁
					enterpriseSettlementService.unlockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
					throw new MsgException("0", "向企业结算表累加借款金额出现未知异常");
				}

			}else if(Constant.ADVANCE_CONFIRM_CAPITAL_SETTLEMENT.equals(advanceConfirmDO.getSourcebilltype())){
				//资金退出结算
				//回写资金注入合同状态
				CapitalSettlementDO capitalSettlementDO = new CapitalSettlementDO();
				capitalSettlementDO.setPk_capital_settlement(advanceConfirmDO.getSourcebillid());
				capitalSettlementDO.setEnterpriseid(advanceConfirmDO.getEnterpriseid());
				capitalSettlementDO.setState(Constant.CAPITALSETTLEMENT_STATE_FINSIH);
				capitalSettlementService.updateCapitalSettlementState(capitalSettlementDO);
			}
		}
		
		return m;
	}
	/**
	 * @Description: ${放款确认表和异常表数据插入}
	 * @author: 超杰
	 * @date: ${date} ${time}
	 * ${tags}
	 */

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg add(AdvanceConfirmDO advanceConfirmDO, Long enterprised) throws MsgException  {

		int m=advanceConfirmDao.insert(advanceConfirmDO);
		if (m != 1) {
			throw new MsgException("0", "插入放款确认单失败");
		}
		//插入收款异常表
		advanceConfirmErrorService.insertAdvanceconfirmError(advanceConfirmDO);


		return new ResultMsg("1", "保存成功！");
	}


	public AdvanceConfirmVO getRestByPrimaryKey(AdvanceConfirmVO vo) {
		return advanceConfirmDao.selectOneByPrimaryKey(vo);
	}

	public ResultMsg modifyAdvanceConfirm(AdvanceConfirmDO advanceConfirmDO, SystemUserInfoT userInfo) {
		advanceConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		advanceConfirmDO.setModefydate(new Date());
		advanceConfirmDO.setModifername(userInfo.getName());
		int m = advanceConfirmDao.modifyAdvanceConfirm(advanceConfirmDO);
		if (m == 1) {
			if (Constant.ADVANCE_CONFIRM_STATE_APPROVING.equals(advanceConfirmDO.getState())) {
				return startTaskForAdvanceConfirm(advanceConfirmDO,userInfo);
			}
			return new ResultMsg("1","保存成功");
		} else {
			return new ResultMsg("0","该数据已失效，请刷新");
		}
	}

	public int deleteAdvanceConfirmDO(AdvanceConfirmDO bean, SystemUserInfoT userInfo) {
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setCanceldate(new Date());
		bean.setCancelname(userInfo.getName());
		bean.setState(Constant.ADVANCE_CONFIRM_STATE_DELETE);
		return advanceConfirmDao.deleteAdvanceConfirmDO(bean);
	}

	
	public long selectCountByCode(AdvanceConfirmDO advanceConfirmDO,SystemUserInfoT userInfo) {
		advanceConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		return advanceConfirmDao.selectCountByCode(advanceConfirmDO);
	}


	public int approveAdvanceConfirmDO(AdvanceConfirmDO bean, SystemUserInfoT userInfo) {
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVED);
		return advanceConfirmDao.updateAdvanceConfirmState(bean);
	}
	
	//开始审核流程
	public ResultMsg startTaskForAdvanceConfirm (AdvanceConfirmDO advanceConfirmDO, SystemUserInfoT userInfo) {
//		String advanceConfirmId = String.valueOf(advanceConfirmDO.getPk_advance_confirm());
// 		ResultMsg msg = activitiUtilService.startApprove(Constant.ADVANCE_CONFIRM_APPROVE_NAME,advanceConfirmId, Constant.ADVANCE_CONFIRM_APPROVE_CODE, userInfo);
//		if ("1".equals(msg.getCode())) {
//			// 设置提交时:2-待审核
			advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVING);
			advanceConfirmDao.updateAdvanceConfirmState(advanceConfirmDO);
//		} else {
//			return msg;
//		}
		return new ResultMsg("1", "提交成功");
	}
	/** 
	 * 审批 
	 * @param advanceConfirmVO
	 * @param userInfo
	 * @return
	 */
	public ResultMsg approve(AdvanceConfirmVO advanceConfirmVO, SystemUserInfoT userInfo) {
		AdvanceConfirmDO advanceConfirmDO = new AdvanceConfirmDO();
		advanceConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		advanceConfirmDO.setPk_advance_confirm(advanceConfirmVO.getPk_advance_confirm());
		advanceConfirmDO.setTime_stamp(advanceConfirmVO.getTime_stamp());
		ResultMsg msg = new ResultMsg();
		msg.setCode("1");
		
		int taskstatus = 0;
		//获取任务
		if ("agree".equals(advanceConfirmVO.getType())) { //通过
			taskstatus = 1;
//			boolean isLastNode = activitiUtilService.isNode(advanceConfirmVO.getPk_advance_confirm(),Constant.ADVANCE_CONFIRM_APPROVE_CODE,Constant.ADVANCE_CONFIRM_APPROVE_LASTTASK);
//			if (isLastNode) {// 判断审核流程是否结束
				advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVED);
//            } else {
//            	advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVING);
//            }
		} else if ("disagree".equals(advanceConfirmVO.getType())){ //退回上一层
			taskstatus = 0;
//			boolean isFristNode = activitiUtilService.isNode(advanceConfirmVO.getPk_advance_confirm(),Constant.ADVANCE_CONFIRM_APPROVE_CODE,Constant.ADVANCE_CONFIRM_APPROVE_LASTTASK);
//			if (isFristNode) {
				advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_NEW);
//			} else {
//				advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVING);
//			}
		} else if ("end".equals(advanceConfirmVO.getType())){//终止
			taskstatus = -1;
			advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_END);
		} else if ("reject".equals(advanceConfirmVO.getType())){//驳回
			taskstatus = 2;
			advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_NEW);
        } else {
        	msg.setCode("0");
            msg.setMsg("审核参数不正确！");
            return msg;
        }
		int m = advanceConfirmDao.updateAdvanceConfirmState(advanceConfirmDO);
		if (m != 1) {
			return new ResultMsg("0","该数据已失效，请刷新");
		}
//		activitiUtilService.approve(Constant.ADVANCE_CONFIRM_STATE_APPROVED, String.valueOf(advanceConfirmVO.getPk_advance_confirm()), advanceConfirmVO.getOption(), String.valueOf(userInfo.getUserId()), userInfo, taskstatus);
		return msg;
	}

	
	public ResultMsg approveLog(AdvanceConfirmVO advanceConfirmVO, SystemUserInfoT userInfo) {
		return activitiUtilService.getFlowPlan(Constant.ADVANCE_CONFIRM_STATE_APPROVED,String.valueOf(advanceConfirmVO.getPk_advance_confirm()));
	}

	public AdvanceConfirmVO selectByCode(AdvanceConfirmVO vo) {
		return advanceConfirmDao.selectByCode(vo);
	}

	public boolean checkAdvanceConfirmTimeStamp(AdvanceConfirmDO bean){
		return advanceConfirmDao.checkAdvanceConfirmTimeStamp(bean)<1;
	}
	
	public String getApproveRoleCodes(Long id, SystemUserInfoT userInfo) {
		return activitiUtilService.getApproveRoleCodes(id, Constant.ADVANCE_CONFIRM_STATE_APPROVED, userInfo);
	}

	public ResultMsg submitModel(AdvanceConfirmVO advanceConfirmVO,SystemUserInfoT userInfo) {
		// 
		AdvanceConfirmDO advanceConfirmDO = new AdvanceConfirmDO();
		advanceConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVING);
		advanceConfirmDO.setPk_advance_confirm(advanceConfirmVO.getPk_advance_confirm());
		advanceConfirmDO.setTime_stamp(advanceConfirmVO.getTime_stamp());
		int m = advanceConfirmDao.updateAdvanceConfirmState(advanceConfirmDO);
		if (m != 1) {
			return new ResultMsg("0","该数据已失效，请刷新");
		}
//		return startTaskForAdvanceConfirm(advanceConfirmDO,userInfo);
		return new ResultMsg("1", "提交成功");
	}
	
	
	/**
	 * 
	 * @Title: updateSourceState 
	 * @Description: 回写状态
	 * @param advanceConfirmDO
	 * @return
	 * @throws MsgException
	 * @throws InterruptedException
	 * @return: ResultMsg
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg updateSourceState(AdvanceConfirmDO advanceConfirmDO) throws MsgException, InterruptedException{
		if(Constant.ADVANCE_CONFIRM_CAPITAL_APPLICATION.equals(advanceConfirmDO.getSourcebilltype())){
			//资金使用申请来源
			//企业结算表加锁
			enterpriseSettlementService.lockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
			try{
				
				EnterpriseSettlementDO enterpriseSettlementDO = new EnterpriseSettlementDO();
				enterpriseSettlementDO.setPk_enterprise(advanceConfirmDO.getPk_enterprise_application());
				enterpriseSettlementDO.setEnterpriseid(advanceConfirmDO.getEnterpriseid());
				EnterpriseSettlementVO enterpriseSettlementVO = enterpriseSettlementDao.getByEnterprise(enterpriseSettlementDO);
				
				if(null == enterpriseSettlementVO){
					//企业结算表解锁
					enterpriseSettlementService.unlockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
					throw new MsgException("0","未找到改企业的企业结算记录，请确定该企业是否签订长期借款合同！");
				}
				
				enterpriseSettlementVO.setAmount(enterpriseSettlementVO.getAmount()  + advanceConfirmDO.getAdvances_amount());
				//向企业结算表累加借款金额
				enterpriseSettlementDao.updateAmount(enterpriseSettlementVO);
				//企业结算表解锁
				enterpriseSettlementService.unlockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
				
			}catch(MsgException e){
				//企业结算表解锁
				enterpriseSettlementService.unlockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
				throw e;
			}catch(Exception e){
				//企业结算表解锁
				enterpriseSettlementService.unlockEnterpriseSettlement(advanceConfirmDO.getPk_enterprise_application(), advanceConfirmDO.getEnterpriseid());
				throw new MsgException("0", "向企业结算表累加借款金额出现未知异常");
			}

		}else if(Constant.ADVANCE_CONFIRM_CAPITAL_SETTLEMENT.equals(advanceConfirmDO.getSourcebilltype())){
			//资金退出结算
			//回写资金注入合同状态
			CapitalSettlementDO capitalSettlementDO = new CapitalSettlementDO();
			capitalSettlementDO.setPk_capital_settlement(advanceConfirmDO.getSourcebillid());
			capitalSettlementDO.setEnterpriseid(advanceConfirmDO.getEnterpriseid());
			capitalSettlementDO.setState(Constant.CAPITALSETTLEMENT_STATE_FINSIH);
			capitalSettlementService.updateCapitalSettlementState(capitalSettlementDO);
		}
		
		return new ResultMsg("1","状态回写成功");
	}

	public AdvanceConfirmVO queryAdvanceBySource(Long sourcebillid, String sourcebilltype, Long enterpriseid  ){
		return advanceConfirmDao.queryAdvanceBySource(sourcebillid,sourcebilltype,enterpriseid);
	}

	public int insert(AdvanceConfirmDO advanceConfirmDO){
		return advanceConfirmDao.insert(advanceConfirmDO);

	}

}
