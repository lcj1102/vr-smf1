package com.suneee.smf.smf.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.dao.CapitalSettlementDao;
import com.suneee.smf.smf.model.CapitalSettlementDO;
import com.suneee.smf.smf.model.CapitalSettlementVO;
@Service("capitalSettlementService")
public class CapitalSettlementService {

	
	@Autowired
	private CapitalSettlementDao capitalSettlementDao;
	@Autowired
	private ActivitiUtilService activitiUtilService;
	public Page<CapitalSettlementVO> queryCapitalSettlementByPage(
			Page<CapitalSettlementVO> page, CapitalSettlementVO bean) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("searchValue",bean.getSearchValue());
		map.put("enterprise_name",bean.getEnterprise_name());
		map.put("code",bean.getCode());
		map.put("credit_code",bean.getCredit_code());
		map.put("state",bean.getState());
		map.put("beginDate",bean.getStartDate());
		if (bean.getEndDate() != null && !"".equals(bean.getEndDate())) {
			Calendar c = Calendar.getInstance();
			c.setTime(bean.getEndDate());
			c.add(Calendar.DAY_OF_MONTH, 1);
			Date endTime = c.getTime();
			map.put("endDate", endTime);
		}
		map.put("enterpriseid",bean.getEnterpriseid());
		int startNum = (page.getPageNo()-1)*(page.getPageSize());
		map.put("startNum",startNum);
		map.put("pageSize",page.getPageSize());
		List<CapitalSettlementVO> list = capitalSettlementDao.selectByExample(map);
		page.setResults(list);    //待分页的数据
		int totalCount = (int) capitalSettlementDao.countByExample(map);
		page.setTotalCount(totalCount); // 总条数
		page.setPageCount(totalCount / page.getPageSize() + 1); //总页数
		return page;
	}

	public ResultMsg addCapitalSettlement(CapitalSettlementDO capitalSettlementDO,
			SystemUserInfoT userInfo) {
		
		capitalSettlementDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		capitalSettlementDO.setInputmanname(userInfo.getName());
		capitalSettlementDO.setBookindate(new Date());
		capitalSettlementDao.insert(capitalSettlementDO);
		if (Constant.CAPITALSETTLEMENT_STATE_APPROVING.equals(capitalSettlementDO.getState())) {
			return startTaskForCapitalSettlement(capitalSettlementDO,userInfo);
		}
		return new ResultMsg("1", "保存成功！");
	}
	public CapitalSettlementVO getRestByPrimaryKey(CapitalSettlementVO vo) {
		return capitalSettlementDao.selectOneByPrimaryKey(vo);
	}
    /**
     * 删除选中资金退出结算单
     */
	public int deleteCapitalSettlementDO(CapitalSettlementDO bean,
			SystemUserInfoT userInfo) {
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setCanceldate(new Date());
		bean.setCancelname(userInfo.getName());
		bean.setState(Constant.CAPITALSETTLEMENT_STATE_DELETE);
		return capitalSettlementDao.deleteCapitalSettlementDO(bean);
	}
    /**
     * 修改资金退出结算单
     */
	public ResultMsg modifyCapitalSettlement(
			CapitalSettlementDO capitalSettlementDO, SystemUserInfoT userInfo) {
		capitalSettlementDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		capitalSettlementDO.setModefydate(new Date());
		capitalSettlementDO.setModifername(userInfo.getName());
		System.err.println(capitalSettlementDO.getPk_capital_settlement());
		System.err.println(capitalSettlementDO.getEnterpriseid());
		System.err.println(capitalSettlementDO.getTime_stamp());
		int m = capitalSettlementDao.modifyCapitalSettlement(capitalSettlementDO);
		if (m == 1) {
			if (Constant.CAPITALSETTLEMENT_STATE_APPROVING.equals(capitalSettlementDO.getState())) {
				return startTaskForCapitalSettlement(capitalSettlementDO,userInfo);
			}
			return new ResultMsg("1","保存成功");
		} else {
			return new ResultMsg("0","该数据已失效，请刷新");
		}
	}
	public ResultMsg submitModel(CapitalSettlementVO capitalSettlementVO,
			SystemUserInfoT userInfo) {
		CapitalSettlementDO capitalSettlementDO = new CapitalSettlementDO();
		capitalSettlementDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		capitalSettlementDO.setState(Constant.CAPITALSETTLEMENT_STATE_APPROVING);
		capitalSettlementDO.setPk_capital_settlement(capitalSettlementVO.getPk_capital_settlement());
		capitalSettlementDO.setTime_stamp(capitalSettlementVO.getTime_stamp());
		int m = capitalSettlementDao.submitModel(capitalSettlementDO);
		if (m != 1) {
			return new ResultMsg("0","该数据已失效，请刷新");
		}
		return startTaskForCapitalSettlement(capitalSettlementDO,userInfo);
	}

	//开始审核流程
	public ResultMsg startTaskForCapitalSettlement (CapitalSettlementDO capitalSettlementDO, SystemUserInfoT userInfo) {
		String pk_capital_settlement = String.valueOf(capitalSettlementDO.getPk_capital_settlement());
 		ResultMsg msg = activitiUtilService.startApprove(Constant.CAPITALSETTLEMENT_APPROVE_NAME,pk_capital_settlement, Constant.CAPITALSETTLEMENT_APPROVE_CODE, userInfo);
		if ("1".equals(msg.getCode())) {
			// 设置提交时，补充协议的状态:2-待审核
			capitalSettlementDO.setState(Constant.CAPITALSETTLEMENT_STATE_APPROVING);
			capitalSettlementDao.approve(capitalSettlementDO);
		} else {
			return msg;
		}
		return new ResultMsg("1", "提交成功");
	}
	public String getApproveRoleCodes(Long pk_capital_settlement,
			SystemUserInfoT userInfo) {
		return activitiUtilService.getApproveRoleCodes(pk_capital_settlement, Constant.CAPITALSETTLEMENT_APPROVE_CODE, userInfo);
	}
	public ResultMsg approve(CapitalSettlementVO capitalSettlementVO,
			SystemUserInfoT userInfo) {

		return null;
	}
	public ResultMsg approveSelect(CapitalSettlementVO capitalSettlementVO,
			SystemUserInfoT userInfo) throws MsgException {
		CapitalSettlementDO modelDO = new CapitalSettlementDO();
		modelDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		modelDO.setPk_capital_settlement(capitalSettlementVO.getPk_capital_settlement());
		modelDO.setTime_stamp(capitalSettlementVO.getTime_stamp());
		ResultMsg msg = new ResultMsg();
		msg.setCode("1");
		
		int taskstatus = 0;
		//获取任务
		if ("agree".equals(capitalSettlementVO.getType())) { //通过
			taskstatus = 1;
			boolean isLastNode = activitiUtilService.isNode(capitalSettlementVO.getPk_capital_settlement(),Constant.CAPITALSETTLEMENT_APPROVE_CODE,Constant.CAPITALSETTLEMENT_APPROVE_LASTTASK);
			if (isLastNode) {// 判断审核流程是否结束
				modelDO.setState(Constant.CAPITALSETTLEMENT_STATE_APPROVED);
            } else {
				modelDO.setState(Constant.CAPITALSETTLEMENT_STATE_APPROVING);
            }
		} else if ("disagree".equals(capitalSettlementVO.getType())){ //退回上一层
			taskstatus = 0;
			boolean isFristNode = activitiUtilService.isNode(capitalSettlementVO.getPk_capital_settlement(),Constant.CAPITALSETTLEMENT_APPROVE_CODE,Constant.CAPITALSETTLEMENT_APPROVE_FRISTTASK);
			if (isFristNode) {
				modelDO.setState(Constant.CAPITALSETTLEMENT_STATE_NEW);
			} else {
				modelDO.setState(Constant.CAPITALSETTLEMENT_STATE_APPROVING);
			}
		} else if ("end".equals(capitalSettlementVO.getType())){//终止
			taskstatus = -1;
			modelDO.setState(Constant.CAPITALSETTLEMENT_STATE_END);
		} else if ("reject".equals(capitalSettlementVO.getType())){//驳回
			taskstatus = 2;
			modelDO.setState(Constant.CAPITALSETTLEMENT_STATE_NEW);
        } else {
            throw new MsgException("0", "审核参数不正确！");
        }
		int m = capitalSettlementDao.approve(modelDO);
		if (m != 1) {
			throw new MsgException("0", "该数据已失效，请刷新");
		}
		activitiUtilService.approve(Constant.CAPITALSETTLEMENT_APPROVE_CODE, String.valueOf(capitalSettlementVO.getPk_capital_settlement()), capitalSettlementVO.getOption(), String.valueOf(userInfo.getUserId()), userInfo, taskstatus);
		return msg;
	}
	public ResultMsg approveLog(CapitalSettlementVO modelVO,
			SystemUserInfoT userInfo) {
		return activitiUtilService.getFlowPlan(Constant.CAPITALSETTLEMENT_APPROVE_CODE,String.valueOf(modelVO.getPk_capital_settlement()));
	}
	public void updateCapitalSettlementState(CapitalSettlementDO capitalSettlementDO){
		capitalSettlementDao.updateCapitalSettlementState(capitalSettlementDO);
	}

	public CapitalSettlementDO selectCapitalSettlementByStatus(
			CapitalSettlementDO capitalSettlementDO, SystemUserInfoT userInfo) {
		capitalSettlementDO.setCapital_injection_code(capitalSettlementDO.getCapital_injection_code());
		capitalSettlementDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		return capitalSettlementDao.selectCapitalSettlementByStatus(capitalSettlementDO);
	}
}
