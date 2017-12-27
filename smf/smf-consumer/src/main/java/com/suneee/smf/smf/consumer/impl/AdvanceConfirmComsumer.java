package com.suneee.smf.smf.consumer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.system.api.provider.RoleProvider;
import com.suneee.scn.system.model.vo.RoleInfo;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AdvanceConfirmDO;
import com.suneee.smf.smf.model.AdvanceConfirmInsertDO;
import com.suneee.smf.smf.model.AdvanceConfirmVO;
import com.suneee.smf.smf.service.AdvanceConfirmService;

/**
 * 
 * @Description: 放款管理consumer
 * @author: 择善
 * @date: 2017年12月5日 上午9:12:10
 */
@Service("advanceConfirmComsumer")
public class AdvanceConfirmComsumer {

	private static final Logger log=LoggerFactory.getLogger(AdvanceConfirmComsumer.class);
	
	@Autowired
	private AdvanceConfirmService advanceConfirmService;
	
	
	@Reference(version="1.0",url="dubbo://172.16.36.68:20917/com.suneee.scn.system.api.provider.RoleProvider")
	private RoleProvider roleProvider;
	
	@Transactional(readOnly = true)
	public Page<AdvanceConfirmVO> queryEnterpriseByPage(Page<AdvanceConfirmVO> page,
			AdvanceConfirmVO bean) {
		return advanceConfirmService.queryAdvanceConfirmByPage(page, bean);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg addAdvanceConfirm(AdvanceConfirmDO advanceConfirmDO, SystemUserInfoT userInfo) {
		if (advanceConfirmDO == null) {
            return new ResultMsg("0", "数据为空");
        } 
		
        return advanceConfirmService.addAdvanceConfirm(advanceConfirmDO,userInfo);
  
	}

	public AdvanceConfirmVO getRestByPrimaryKey(AdvanceConfirmVO vo) {
		return advanceConfirmService.getRestByPrimaryKey(vo);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg modifyAdvanceConfirm(AdvanceConfirmDO advanceConfirmDO, SystemUserInfoT userInfo) {
		if (advanceConfirmDO == null) {
			return new ResultMsg("0", "数据为空");
		} 
		advanceConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());

		if (advanceConfirmService.selectCountByCode(advanceConfirmDO,userInfo) > 0) {
			return new ResultMsg("0", "放款确认单号重复");
		}
        return advanceConfirmService.modifyAdvanceConfirm(advanceConfirmDO, userInfo);
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg deleteAdvanceConfirmDO(AdvanceConfirmInsertDO advanceConfirmInsertDO, SystemUserInfoT userInfo) {
		int count = 0;
		for (AdvanceConfirmDO bean : advanceConfirmInsertDO.getInsertDO()) {
			int m = advanceConfirmService.deleteAdvanceConfirmDO(bean, userInfo);
			if (m == 1) {
				count += m;
			} else {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		}
		if (count > 0) {
			return new ResultMsg("1", "删除成功！！");
		}
		return new ResultMsg("0", "该数据已失效，请刷新！！");
	}

	
	
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg approveAdvanceConfirmDO(AdvanceConfirmInsertDO advanceConfirmInsertDO, SystemUserInfoT userInfo) {
		int count = 0;
		for (AdvanceConfirmDO bean : advanceConfirmInsertDO.getInsertDO()) {
			int m = advanceConfirmService.approveAdvanceConfirmDO(bean,userInfo);
			if (m == 1) {
				count += m;
			} else {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		}
		if (count > 0) {
			return new ResultMsg("1", "审核成功！！");
		}
		return new ResultMsg("0", "审核失败！！");
	}


	@Transactional(readOnly = true)
	public ResultMsg checkApproveById(AdvanceConfirmVO advanceConfirmVO, SystemUserInfoT userInfo) {
		AdvanceConfirmDO bean = new AdvanceConfirmDO();
		bean.setPk_advance_confirm(advanceConfirmVO.getPk_advance_confirm());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setTime_stamp(advanceConfirmVO.getTime_stamp());
		if(advanceConfirmService.checkAdvanceConfirmTimeStamp(bean)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		String roleName = advanceConfirmService.getApproveRoleCodes(advanceConfirmVO.getPk_advance_confirm(),userInfo);
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
	
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg approve(AdvanceConfirmVO advanceConfirmVO, SystemUserInfoT userInfo) {
		return advanceConfirmService.approve(advanceConfirmVO,userInfo);
	}

	public ResultMsg approveLog(AdvanceConfirmVO advanceConfirmVO, SystemUserInfoT userInfo) {
		AdvanceConfirmDO bean = new AdvanceConfirmDO();
		bean.setPk_advance_confirm(advanceConfirmVO.getPk_advance_confirm());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setTime_stamp(advanceConfirmVO.getTime_stamp());
		if(advanceConfirmService.checkAdvanceConfirmTimeStamp(bean)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		return advanceConfirmService.approveLog(advanceConfirmVO,userInfo);
	}
	
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg submitModel(AdvanceConfirmVO advanceConfirmVO, SystemUserInfoT userInfo) {
		return advanceConfirmService.submitModel(advanceConfirmVO, userInfo);
	}
	
	
}
