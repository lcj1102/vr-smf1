package com.suneee.smf.smf.rest.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.component.cache.CacheUtils;
import com.suneee.smf.smf.api.rest.AdvancesConfirmRestService;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.AdvanceConfirmComsumer;
import com.suneee.smf.smf.model.AdvanceConfirmDO;
import com.suneee.smf.smf.model.AdvanceConfirmInsertDO;
import com.suneee.smf.smf.model.AdvanceConfirmVO;

@Service//指明dubbo服务
@Path("/advanceconfirm")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class AdvancesConfirmRestServiceImpl implements AdvancesConfirmRestService{

	@Autowired
	private AdvanceConfirmComsumer advanceConfirmComsumer;
	
	@GET
    @Path("/advanceconfirmList")
    @Produces("application/json")
	@Override
	public ResultMsg advanceConfirmList(@QueryParam("searchValue") String searchValue,
			@QueryParam("enterprise_application_name")String enterprise_application_name, 
			@QueryParam("code")String code, 
			@QueryParam("contact")String contact, 
			@QueryParam("beginForApplicationDate") String beginForApplicationDate,
			@QueryParam("endForApplicationDate") String endForApplicationDate,
			@QueryParam("beginForBusiDate") String beginForBusiDate,
			@QueryParam("endForBusiDate") String endForBusiDate,
			@QueryParam("state")String state,
			@QueryParam("length")String length, 
			@QueryParam("pageNum")String pageNum, 
			@HeaderParam("sessionId")String sessionId) {
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	// 初始化
         	List<Object> list = new ArrayList<Object>();
        	Page<AdvanceConfirmVO> page = new Page<AdvanceConfirmVO>();
        	AdvanceConfirmVO bean = new AdvanceConfirmVO();
        	// 设置page属性
        	page.setPageSize(Integer.parseInt(length));// 每页条数
        	page.setPageNo(Integer.parseInt(pageNum));// 当前页
        	// 设置bean属性（查询条件）
        	bean.setSearchValue(searchValue);
			bean.setCode(code);
			bean.setEnterprise_application_name(enterprise_application_name);
			bean.setContact(contact);
			if (beginForBusiDate != null && false == "".equals(beginForBusiDate)) {
	    		bean.setBeginForBusiDate(DateUnit.string2date(beginForBusiDate, "yyyy-MM-dd"));
	    	}
	    	if (endForBusiDate != null && false == "".equals(endForBusiDate)) {
	    		bean.setEndForBusiDate(DateUnit.string2date(endForBusiDate, "yyyy-MM-dd"));
	    	}
	    	
	    	if (beginForApplicationDate != null && false == "".equals(beginForApplicationDate)) {
	    		bean.setBeginForApplicationDate(DateUnit.string2date(beginForApplicationDate, "yyyy-MM-dd"));
	    	}
	    	if (endForApplicationDate != null && false == "".equals(endForApplicationDate)) {
	    		bean.setEndForApplicationDate(DateUnit.string2date(endForApplicationDate, "yyyy-MM-dd"));
	    	}
			
			
        	bean.setState(state);
        	bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        	list.add(advanceConfirmComsumer.queryEnterpriseByPage(page, bean));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
	}

	@POST
    @Path("/insert")
    @Produces("application/json")
    @Override
	public ResultMsg insert(AdvanceConfirmInsertDO insertDO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        try {
	        	
	        	AdvanceConfirmDO advanceConfirmDO = insertDO.getInsertDO().get(0);
	        	if ("submit".equals(insertDO.getSaveType())) {
	        		advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVING);
	        	} else {
	        		advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_NEW);
	        	}
	        	msg = advanceConfirmComsumer.addAdvanceConfirm(advanceConfirmDO,userInfo);
	        } catch (Exception e) {
	            msg.setMsg("新增失败！！");
	            e.printStackTrace();
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
	}

	@GET
    @Path("/byid")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg selectByPrimaryKey(@QueryParam("id")Long id, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        List<Object> pb = new ArrayList<Object>();//返回集合
	        if (id != null && !"".equals(id)) {
	        	AdvanceConfirmVO vo = new AdvanceConfirmVO();
	        	vo.setPk_advance_confirm(id);
	        	vo.setEnterpriseid(userInfo.getEnterpriseid().longValue());
	            pb.add(advanceConfirmComsumer.getRestByPrimaryKey(vo));
	            msg.setData(pb);
	        } else {
	            msg.setData(pb);
	            msg.setMsg("查询失败");
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
	}

	@POST
    @Path("/update")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
	public ResultMsg update(AdvanceConfirmInsertDO insertDO, @HeaderParam("sessionId") String sessionId) {
		 ResultMsg msg = new ResultMsg();
	     SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
	     if (userInfo != null && userInfo.getEnterpriseid() != null) {
		        try {
		        	AdvanceConfirmDO advanceConfirmDO = insertDO.getInsertDO().get(0);
		        	if ("submit".equals(insertDO.getSaveType())) {
		        		advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVING);
		        	} else {
		        		advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_NEW);
		        	}
		        	msg = advanceConfirmComsumer.modifyAdvanceConfirm(advanceConfirmDO,userInfo);
		        } catch (Exception e) {
		           msg.setMsg("保存失败！！");
		            e.printStackTrace();
		        }
	      } else {
	        	msg.setMsg("用户登陆过期，请重新登陆！");
	      } 
	       
	      return msg;
	}

	@POST
    @Path("/deleteSelect")
    @Produces("application/json")
	@Override
	public ResultMsg deleteSelect(AdvanceConfirmInsertDO insertDO,
			@HeaderParam("sessionId") String sessionId) {
		 ResultMsg msg = new ResultMsg();
	     SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
	     if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        if (insertDO == null || insertDO.getInsertDO() == null) {
	        	msg.setMsg("删除失败！！");
	        } else {
	        		
				msg =advanceConfirmComsumer.deleteAdvanceConfirmDO(insertDO, userInfo);
	        }
	      } else {
	        	msg.setMsg("用户登陆过期，请重新登陆！");
	      }
	      return msg;   
	}

	

	@POST
    @Path("/approve")
    @Produces("application/json")
	@Override
	public ResultMsg approve(AdvanceConfirmInsertDO insertDO,
			@HeaderParam("sessionId") String sessionId) {
		 ResultMsg msg = new ResultMsg();
	     SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
	     if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        if (insertDO == null || insertDO.getInsertDO() == null) {
	        	msg.setMsg("审核失败！！");
	        } else {
	        	msg = advanceConfirmComsumer.approveAdvanceConfirmDO(insertDO, userInfo);
	        }
	      } else {
	        msg.setMsg("用户登陆过期，请重新登陆！");
	      }
	      return msg;		
	}

	
	/**
	 * 查看当前用户是否有对应数据的审批权限
	 */
	@POST
    @Path("/checkApproveById")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg checkApproveById(AdvanceConfirmVO advanceConfirmVO,
			@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	msg = advanceConfirmComsumer.checkApproveById(advanceConfirmVO,userInfo);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
	}

	@POST
	@Path("/approveLog")//为空则其完整路径就是类路径
	@Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approveLog(AdvanceConfirmVO advanceConfirmVO,
			@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			msg =advanceConfirmComsumer.approveLog(advanceConfirmVO, userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}

	@POST
    @Path("/approveSelect")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approveSelect(AdvanceConfirmVO advanceConfirmVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			msg =advanceConfirmComsumer.approve(advanceConfirmVO, userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
	

	/**
	 * 提交数据
	 */
	@POST
    @Path("/submitModel")
    @Produces("application/json")
	@Override
	public ResultMsg submitModel(AdvanceConfirmVO advanceConfirmVO,
			@HeaderParam("sessionId")String sessionId) {
		
		 ResultMsg msg = new ResultMsg();
	     SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
	     if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        msg = advanceConfirmComsumer.submitModel(advanceConfirmVO,userInfo);
	     } else {
	        msg.setMsg("用户登陆过期，请重新登陆！");
	     }
	     return msg;
	}

}
