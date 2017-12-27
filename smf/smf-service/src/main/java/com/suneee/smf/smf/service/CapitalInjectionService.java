package com.suneee.smf.smf.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.scf.api.provider.CodeRuleRestProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.dao.CapitalInjectionDao;
import com.suneee.smf.smf.model.CapitalInjectionDO;
import com.suneee.smf.smf.model.CapitalInjectionVO;



@Service("capitalInjectionService")
public class CapitalInjectionService {

	@Autowired
	private CapitalInjectionDao capitalInjectionDao;
	
	@Autowired
	private ActivitiUtilService activitiUtilService;

	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;
	/**
	 * 列表分页查询
	 */
	public Page<CapitalInjectionVO> queryPage(Page<CapitalInjectionVO> page, CapitalInjectionVO bean) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		//设置查询字段
		map.put("code",bean.getCode());
		map.put("name",bean.getName());
		map.put("beginForBusiDate",bean.getStartForBusiDate());
		if (bean.getEndForBusiDate() != null) {
			map.put("endForBusiDate", DateUnit.icDateByDay(bean.getEndForBusiDate(),1));
		}
		map.put("state",bean.getState());
		map.put("enterpriseid", bean.getEnterpriseid());
		
		//设置分页
		int startNum = (page.getPageNo()-1)*(page.getPageSize());
		map.put("startNum",startNum);
		map.put("pageSize",page.getPageSize());
		//设置搜索框模糊查询
		map.put("searchValue",bean.getSearchValue());
		
        List<CapitalInjectionVO> list = capitalInjectionDao.selectByExample(map);
        page.setResults(list);    //待分页的数据
        int totalCount = (int) capitalInjectionDao.countByExample(map);
        page.setTotalCount(totalCount); // 总条数
        page.setPageCount(totalCount / page.getPageSize() + 1); //总页数
        
        return page;
	}

	
	/**
	 * 新增
	 */
	public ResultMsg insert(CapitalInjectionDO capitalInjectionDO, SystemUserInfoT userInfo) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("code",capitalInjectionDO.getCode());
		map.put("enterpriseid",userInfo.getEnterpriseid());
		map.put("pk_capital_injection",capitalInjectionDO.getPk_capital_injection());
		
		if (capitalInjectionDao.countByCode(map) > 0) {
			return new ResultMsg("5", "任务编号重复");
		}
	
		//生成回购单单号
		/*ResultMsg msg = codeRuleUtilService.submitSingleton(userInfo.getEnterpriseid().longValue(), Constant.CAPITALINJECTION_CODERULE_CODE);
		if ("1".equals(msg.getCode())) {
			capitalInjectionDO.setCode(msg.getMsg());
		} else {
			return msg;
		}*/
		
		capitalInjectionDO.setInputmanid(userInfo.getUserId().longValue());
		capitalInjectionDO.setInputmanname(userInfo.getUserName());
		capitalInjectionDO.setBookindate(new Date());
		capitalInjectionDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		capitalInjectionDao.insert(capitalInjectionDO);

		
		if (Constant.CAPITALINJECTION_STATE_APPROVING.equals(capitalInjectionDO.getState())) {
			return startTask(capitalInjectionDO,userInfo);
		}
		
		return new ResultMsg("1", "保存成功");
	}
	
	
	/**
	 * 删除
	 */
	public int delete(CapitalInjectionDO bean, SystemUserInfoT userInfo) {
		
		Date date = new Date();
		bean.setState(Constant.CAPITALINJECTION_STATE_DELETE);
		bean.setCanceldate(date);
		bean.setCancelid(userInfo.getUserId().longValue());
		bean.setCancelname(userInfo.getUserName());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		
		return capitalInjectionDao.delete(bean);
	}
	
	/**
	 * 根据id查询任务信息
	 */
	public CapitalInjectionVO getById(CapitalInjectionDO capitalInjectionDO) {
		return capitalInjectionDao.getById(capitalInjectionDO);
	}
	
	/**
	 * 编辑
	 */
	public ResultMsg modify(CapitalInjectionDO capitalInjectionDO,SystemUserInfoT userInfo) {
		
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("code",capitalInjectionDO.getCode());
		map.put("enterpriseid",userInfo.getEnterpriseid());
		map.put("pk_capital_injection",capitalInjectionDO.getPk_capital_injection());
		if (capitalInjectionDao.countByCode(map) > 0) {
			return new ResultMsg("5", "资金注入单号重复");
		}
		
		Date date = new Date();
		capitalInjectionDO.setModefydate(date);
		capitalInjectionDO.setModiferid(userInfo.getUserId().longValue());
		capitalInjectionDO.setModifername(userInfo.getUserName());
		capitalInjectionDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		
		int m = capitalInjectionDao.update(capitalInjectionDO);
		if (m != 1) {
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		
		
		if (Constant.CAPITALINJECTION_STATE_APPROVING.equals(capitalInjectionDO.getState())) {
			return startTask(capitalInjectionDO,userInfo);
		}
		return new ResultMsg("1", "修改成功！");
	}
	
	/**
	 * 提交开启审批
	 */
	public ResultMsg submitModel(CapitalInjectionVO capitalInjectionVO,SystemUserInfoT userInfo) {
		CapitalInjectionDO capitalInjectionDO = new CapitalInjectionDO();
		capitalInjectionDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_APPROVING);
		capitalInjectionDO.setPk_capital_injection(capitalInjectionVO.getPk_capital_injection());
		capitalInjectionDO.setTime_stamp(capitalInjectionVO.getTime_stamp());
		int m = capitalInjectionDao.updateState(capitalInjectionDO);
		if (m != 1) {
			return new ResultMsg("0","该数据已失效，请刷新");
		}
		return startTask(capitalInjectionDO,userInfo);
	}
	

	/** 
	 * 审批 
	 */
	public ResultMsg approve(CapitalInjectionVO capitalInjectionVO, SystemUserInfoT userInfo){
		
		CapitalInjectionDO capitalInjectionDO = new CapitalInjectionDO();
		capitalInjectionDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		capitalInjectionDO.setPk_capital_injection(capitalInjectionVO.getPk_capital_injection());
		capitalInjectionDO.setTime_stamp(capitalInjectionVO.getTime_stamp());
		ResultMsg msg = new ResultMsg();
		msg.setCode("1");
		
		int taskstatus = 0;
		//获取任务
		if ("agree".equals(capitalInjectionVO.getType())) { //通过
			taskstatus = 1;
			boolean isLastNode = activitiUtilService.isNode(capitalInjectionVO.getPk_capital_injection(),Constant.CAPITALINJECTION_APPROVE_CODE,Constant.CAPITALINJECTION_APPROVE_LASTTASK);
			if (isLastNode) {// 判断审核流程是否结束
				capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_APPROVED);
				
				msg.setHtml("1");
				
            } else {
				capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_APPROVING);
            }
		} else if ("disagree".equals(capitalInjectionVO.getType())){ //退回上一层
			taskstatus = 0;
			boolean isFristNode = activitiUtilService.isNode(capitalInjectionVO.getPk_capital_injection(),Constant.CAPITALINJECTION_APPROVE_CODE,Constant.CAPITALINJECTION_APPROVE_FRISTTASK);
			if (isFristNode) {
				capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_NEW);
			} else {
				capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_APPROVING);
			}
		} else if ("end".equals(capitalInjectionVO.getType())){//终止
			taskstatus = -1;
			capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_END);
		} else if ("reject".equals(capitalInjectionVO.getType())){//驳回
			taskstatus = 2;
			capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_NEW);
        } else {
        	msg.setCode("0");
            msg.setMsg("审核参数不正确！");
            return msg;
        }
		int m = capitalInjectionDao.updateState(capitalInjectionDO);
		if (m != 1) {
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		activitiUtilService.approve(Constant.CAPITALINJECTION_APPROVE_CODE, String.valueOf(capitalInjectionVO.getPk_capital_injection()), capitalInjectionVO.getOption(), String.valueOf(userInfo.getUserId()), userInfo, taskstatus);
		return msg;
		
	}

	/**
	 *查看审批进度 
	 */
	public ResultMsg approveLog(CapitalInjectionVO capitalInjectionVO) {
		
		return activitiUtilService.getFlowPlan(Constant.CAPITALINJECTION_APPROVE_CODE,String.valueOf(capitalInjectionVO.getPk_capital_injection()));
	}
	
	/**
	 * 开启审批流程
	 */
	public ResultMsg startTask (CapitalInjectionDO capitalInjectionDO, SystemUserInfoT userInfo) {
		
		String capitalInjectionId = String.valueOf(capitalInjectionDO.getPk_capital_injection());
		ResultMsg msg = activitiUtilService.startApprove(Constant.CAPITALINJECTION_APPROVE_NAME,capitalInjectionId, Constant.CAPITALINJECTION_APPROVE_CODE, userInfo);
		if ("1".equals(msg.getCode())) {
			// 设置提交时，补充协议的状态:2-待审核
			capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_APPROVING);
			capitalInjectionDao.updateState(capitalInjectionDO);
		} else {
			return msg;
		}
		return new ResultMsg("1", "提交成功");
	}
	
	/**
	 * 根据记录主键查询审批所需的权限
	 */
	public String getApproveRoleCodes(Long id, SystemUserInfoT userInfo) {
	
		return activitiUtilService.getApproveRoleCodes(id, Constant.CAPITALINJECTION_APPROVE_CODE, userInfo);
	}
	
	/**
	 * 添加时间戳，解决多线程并发修改问题
	 */
	public boolean checkTimeStamp(CapitalInjectionDO capitalInjectionDO){
		return capitalInjectionDao.checkTimeStamp(capitalInjectionDO) < 1;
	}
	
	/***
	 * 资金退出结算接口
	 */
	 public List<CapitalInjectionVO> selectByName(String name, Long enterpriseid){
		 Map<String,Object> map = new HashMap<String, Object>();
		 map.put("code", name);
		 map.put("state", Constant.CAPITALINJECTION_STATE_APPROVED);
		 map.put("enterpriseid", enterpriseid);
		 return capitalInjectionDao.selectByName(map);
	 }
	 
	 public CapitalInjectionVO selectByCode(String code, Long enterpriseid){
		 CapitalInjectionVO bean = new CapitalInjectionVO();
		 bean.setCode(code);
		 bean.setEnterpriseid(enterpriseid);
		 return capitalInjectionDao.selectByCode(bean);
	 }
	
	
}