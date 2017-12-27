package com.suneee.smf.smf.service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.workflow.common.DealTask;
import com.suneee.scn.basic.workflow.common.IdentityTask;
import com.suneee.scn.basic.workflow.common.QueryTask;
import com.suneee.scn.basic.workflow.model.TaskInfo;
import com.suneee.smf.smf.common.FlowPlan;
import com.suneee.smf.smf.common.ResultMsg;

@Service("activitiUtilService")
public class ActivitiUtilService {
	 
	private static Logger log = LoggerFactory.getLogger(ActivitiUtilService.class);
	
	@Autowired
	private DealTask dealTask;
	
	@Autowired
	private QueryTask queryTask; 
	
	@Autowired
	private IdentityTask identityTask;
	
	/**
	 * 判断当前人是否有当前节点的审批权限 
	 * @param businessId
	 * @param businessType
	 * @param userInfo
	 * @return
	 */
	public String getApproveRoleCodes(Long businessId,String businessType, SystemUserInfoT userInfo) {
//		//根据id获取流程ＩＤ
		String initId = (String) dealTask.findBySQL("select max(task_init) as task_init  from act_task_business_info where business_id ='"+businessId+"' and business_type='"+businessType+"'").get(0).get("task_init");
        String taskId = dealTask.getTaskID(initId);
		TaskInfo info = dealTask.getTaskByID(taskId);
        String activitikey = info.getActivitiKey();
        
        String roleName = businessType+""+activitikey;
		return roleName;
	}
	
	/**
     * 工作流签收任务并完成任务
     * @param taskId
     * @param taskStatus（-1 终止;0 退回;1通过;2驳回）
     * @param userId 下一级审批人id
     */
    public void assigneeAndCompleteTask(String taskId, int taskStatus,String userId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("status", taskStatus);
        try {
            dealTask.assigneeTask(taskId, userId);// 接受
            dealTask.completeTask(taskId, paramMap);// 完成
        } catch (ActivitiTaskAlreadyClaimedException e) {
            throw new RuntimeException("提交不成功，被退回的单据只有原单据提交人可以编辑再提交。");
        }
    }
    
    /**
     * 是否某一个审核节点。
     * @param businessId 业务id
     * @param businessType 业务类型
     * @param nodeKey 节点id
     * @return
     */
    public boolean isNode(Object businessId,String businessType, String nodeKey) {
		String initId = (String) dealTask.findBySQL("select max(task_init) as task_init  from act_task_business_info where business_id ='"+businessId+"' and business_type='"+businessType+"'").get(0).get("task_init");
        String taskId = dealTask.getTaskID(initId);
		TaskInfo info = dealTask.getTaskByID(taskId);
        String activitikey = info.getActivitiKey();
        if (nodeKey.equals(activitikey)) return true;
        return false;
    }
    
    /**
     * 审核意见
     * 
     * @param opinion
     * @param taskId
     * @param initId
     * @throws Exception
     */
    public void setOpinion(String opinion, String taskId, String initId,String msgType,SystemUserInfoT userInfo)
            throws Exception {
        com.suneee.scn.basic.workflow.model.ActivitiMsg am = new com.suneee.scn.basic.workflow.model.ActivitiMsg();
        if (taskId != null) {
            am.setTaskId(taskId);
        }
        am.setInitId(initId);
        am.setMsg(opinion);
        am.setTime(new Date());
        am.setUserId(String.valueOf(userInfo.getUserId()));
        am.setMsgType(msgType);
        if (!dealTask.setMessge(am)) {
            throw new Exception("设置留言失败");
        }
    }
    /**
     * 创建审批
     * @param businessName
     * @param businessId
     * @param businessType
     * @param userInfo
     * @return
     */
    public ResultMsg startApprove(String businessName,String businessId,String businessType,SystemUserInfoT userInfo) {
    	List<Map<String, Object>> initIdList = dealTask.findBySQL("select max(task_init) as task_init  from act_task_business_info where business_id ='"
				+businessId+"' and business_type='"+businessType+"'");
    	log.info("*********startApprove initIdList:"+initIdList+"***************");
		String initId= "";
		if (false == initIdList.isEmpty()) {
			initId = (String) initIdList.get(0).get("task_init");
		}
		log.info("*********startApprove initId:"+initId+"***************");
		
		if (initId == null || initId.isEmpty()) {
			@SuppressWarnings("rawtypes")
			List list =dealTask.findBySQL("select * from act_task_business_info where business_id in ('"+businessId+"') and business_type='"+businessType+"'");
			log.info("*********startApprove list:"+list+"***************");
			if (list.size() > 0) {
				return new ResultMsg("0", "该流程已经在审核中");
			}
			log.info("*********startApprove userInfo.getEnterprisecode():"+userInfo.getEnterprisecode()+"***************");
			//创建审核流程
			com.suneee.scn.basic.workflow.model.ResultMsg rg = dealTask.newTask(businessName,businessType, businessId,userInfo.getEnterprisecode());
			if (rg.getCode() == 1) {
//				Map<String, Object> params = new HashMap<String, Object>();
//				params.put("status", 1);
//				// 提交时创建流程和审核流程
//				rg = dealTask.assigneeTask(rg.getTaskId(), String.valueOf(userInfo.getUserId()));
//				dealTask.completeTask(rg.getTaskId(), params);// 完成
				try {
					setOpinion("提交", rg.getTaskId(), initId,businessType,userInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return new ResultMsg("1", "提交成功");
			} else {
				return new ResultMsg("0", "审核流程创建出错");
			}
		} else {
			String taskId = dealTask.getTaskID(initId);
			try {
				setOpinion("重新提交", taskId, initId,businessType,userInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			assigneeAndCompleteTask(taskId, 1,String.valueOf(userInfo.getUserId()));
		}
		return new ResultMsg("1", "提交成功");
    }

    /**
     * 审批
     * @param businessType 流程id
     * @param businessId 数据id
     * @param option 审批意见
     * @param userId 下一级审批人id
     * @param userInfo 当前用户
     * @param taskstatus 审批流程状态（-1 终止;0 退回;1通过;2驳回）
     * @return
     */
	public void approve(String businessType, String businessId,String option,String userId,SystemUserInfoT userInfo,int taskstatus) {
		String initId = (String) dealTask.findBySQL("select max(task_init) as task_init  from act_task_business_info where business_id ='"
				+businessId+"' and business_type='"+businessType+"'").get(0).get("task_init");
		String taskId = dealTask.getTaskID(initId);
		try {
			setOpinion(option, taskId, initId,businessType,userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assigneeAndCompleteTask(taskId, taskstatus,userId);
	}

	/**
	 * 流程进度参数
	 * @param businessId
	 * @param businessType
	 * @return
	 */
	public ResultMsg getFlowPlan(String businessType, String businessId) {
		ResultMsg msg = new ResultMsg();
		FlowPlan flowPlan = new FlowPlan();
		
		String initId = (String) dealTask.findBySQL("select max(task_init) as task_init  from act_task_business_info where business_id ='"
				+businessId+"' and business_type='"+businessType+"'").get(0).get("task_init");
		
		String pid = queryTask.getProcessDefinitionId(initId);
		
		if (initId == null || "".equals(initId) || pid == null || "".equals(pid)) {
			return new ResultMsg("0", "该数据没有进入流程！");
		}
		
		flowPlan.setInitid(initId);
		flowPlan.setProcessDefinitionId(pid);
		flowPlan.setShowreturn("1");
		List<Object> list = new LinkedList<Object>();
		list.add(flowPlan);
		msg.setCode("1");
		msg.setData(list);
		return msg;
	}
}
