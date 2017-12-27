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
import com.suneee.scn.scf.common.DateUnit;
import com.suneee.smf.smf.api.rest.CapitalSettlementRestService;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.CapitalSettlementConsumer;
import com.suneee.smf.smf.model.AttachmentsDO;
import com.suneee.smf.smf.model.AttachmentsInsertDO;
import com.suneee.smf.smf.model.CapitalSettlementDO;
import com.suneee.smf.smf.model.CapitalSettlementInsertDO;
import com.suneee.smf.smf.model.CapitalSettlementVO;


@Service//指明dubbo服务
@Path("capitalSettlement")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class CapitalSettlementRestServiceImpl implements CapitalSettlementRestService {

    @Autowired
    private CapitalSettlementConsumer capitalSettlementConsumer;
    /**
     * 获取资金退出结算单列表
     */
    @GET
    @Path("/capitalSettlementList")
    @Produces("application/json")
	public ResultMsg enterpriseList(@QueryParam("searchValue") String searchValue,
			@QueryParam("enterprise_name") String enterprise_name,
			@QueryParam("code") String code,
			@QueryParam("credit_code") String credit_code,
			@QueryParam("state") String state,
			@QueryParam("beginDate") String beginDate,
			@QueryParam("endDate") String endDate,
            @QueryParam("length") String length,
            @QueryParam("pageNum") String pageNum,
            @HeaderParam("sessionId") String sessionId) {
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	// 初始化
        	List<Object> list = new ArrayList<Object>();
        	Page<CapitalSettlementVO> page = new Page<CapitalSettlementVO>();
        	CapitalSettlementVO bean = new CapitalSettlementVO();
        	// 设置page属性
        	page.setPageSize(Integer.parseInt(length));// 每页条数
        	page.setPageNo(Integer.parseInt(pageNum));// 当前页
        	// 设置bean属性（查询条件）
        	bean.setSearchValue(searchValue);
        	bean.setEnterprise_name(enterprise_name);
			bean.setCode(code);
			bean.setCredit_code(credit_code);
        	bean.setState(state);
        	bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        	if (beginDate != null && false == "".equals(beginDate)) {
        		bean.setStartDate(DateUnit.string2date(beginDate, "yyyy-MM-dd"));
        	}
        	if (endDate != null && false == "".equals(endDate)) {
        		bean.setEndDate(DateUnit.string2date(endDate, "yyyy-MM-dd"));
        	}
        	list.add(capitalSettlementConsumer.queryCapitalSettlementByPage(page, bean));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
    }
    /**
     * 添加资金退出结算单
     */
    @POST
    @Path("/insert")
    @Produces("application/json")
    @Override
    public ResultMsg insert(CapitalSettlementInsertDO capitalSettlementDO,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        try {
	        	CapitalSettlementDO laDO = capitalSettlementDO.getCapitalSettlementDOList().get(0);
	        	if ("submit".equals(capitalSettlementDO.getSaveType())) {
	        		laDO.setState(Constant.CAPITALSETTLEMENT_STATE_APPROVING);
	        	} else {
	        		laDO.setState(Constant.CAPITALSETTLEMENT_STATE_NEW);
	        	}
	        	msg = capitalSettlementConsumer.addCapitalSettlement(laDO,userInfo);
	        } catch (Exception e) {
	            msg.setMsg("新增失败！！");
	            e.printStackTrace();
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
    }
    /**
     * 根据id获取资金退出结算单
     */
    @GET
    @Path("/byid")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
    public ResultMsg selectByPrimaryKey(@QueryParam("id") Long id,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        List<Object> pb = new ArrayList<Object>();//返回集合
	        if (id != null && !"".equals(id)) {
	        	CapitalSettlementVO vo = new CapitalSettlementVO();
	        	vo.setPk_capital_settlement(id);
	        	vo.setEnterpriseid(userInfo.getEnterpriseid().longValue());
	            pb.add(capitalSettlementConsumer.getRestByPrimaryKey(vo));
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
    /**
     * 删除选中资金退出结算单
     */
    @POST
    @Path("/deleteSelect")
    @Produces("application/json")
	@Override
    public ResultMsg deleteSelect(CapitalSettlementInsertDO capitalSettlementDO,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	/*int count = 0;*/
        	if (capitalSettlementDO == null || capitalSettlementDO.getCapitalSettlementDOList() == null) {
        		msg.setMsg("作废失败！！");
        	} else {
				msg =capitalSettlementConsumer.deleteCapitalSettlementDO(capitalSettlementDO, userInfo);
        	}
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }
    /**
     * 修改资金退出结算单
     */
    @POST
    @Path("/update")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
    public ResultMsg update(CapitalSettlementInsertDO capitalSettlementDO,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        try {
	        	CapitalSettlementDO laDO = capitalSettlementDO.getCapitalSettlementDOList().get(0);
	        	if ("submit".equals(capitalSettlementDO.getSaveType())) {
	        		laDO.setState(Constant.CAPITALSETTLEMENT_STATE_APPROVING);
	        	} else {
	        		laDO.setState(Constant.CAPITALSETTLEMENT_STATE_NEW);
	        	}
	        	msg = capitalSettlementConsumer.modifyCapitalSettlement(laDO,userInfo);
	        } catch (Exception e) {
	            msg.setMsg("保存失败！！");
	            e.printStackTrace();
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
       
        return msg;
    }
    
	/**
	 * 根据主键查询附件
	 */
	@GET
	@Path("/fileListById")
	@Produces("application/json")
	@Override
	public ResultMsg fileListById(@QueryParam("capitalSettlementId") Long capitalSettlementId,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null) {
			msg = capitalSettlementConsumer.getFileListById(capitalSettlementId,userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
	/**
	 * 新增合同附件
	 */
	@POST
    @Path("/insertFile")
    @Produces("application/json")
	@Override
	public ResultMsg insertFile(AttachmentsInsertDO file, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null) {
	        try {
	        	msg = capitalSettlementConsumer.addFile(file.getFile().get(0),userInfo);
	            msg.setMsg("新增成功！！");
	        } catch (MsgException e) {
	        	msg.setMsg(e.getMessage());
	            msg.setCode(e.getCode());
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
	}
	/**
	 * 删除合同附件
	 */
	@POST
    @Path("/deleteFile")
    @Produces("application/json")
	@Override
	public ResultMsg deleteFile(AttachmentsInsertDO file, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null) {
        	int count = 0;
        	if (file == null || file.getFile() == null) {
        		msg.setMsg("删除失败！！");
        	} else {
        		try {
	        		for (AttachmentsDO bean : file.getFile()) {
							count += capitalSettlementConsumer.deleteFile(bean,userInfo);
	        		}
        		} catch (MsgException e) {
        			msg.setMsg(e.getMessage());
    	            msg.setCode(e.getCode());
        		}
        		if (count > 0) {
        			msg.setMsg("删除成功！！");
        		} else {
        			msg.setMsg("删除失败！！");
        		}
        	}
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
    public ResultMsg submitModel(CapitalSettlementVO capitalSettlementVO,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	msg = capitalSettlementConsumer.submitModel(capitalSettlementVO,userInfo);
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
	public ResultMsg checkApproveById(CapitalSettlementVO capitalSettlementVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	msg = capitalSettlementConsumer.checkApproveById(capitalSettlementVO,userInfo);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
	}
	/**
	 * 审批
	 */
	@POST
    @Path("/approveSelect")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approve(CapitalSettlementVO capitalSettlementVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			try {
				msg =capitalSettlementConsumer.approve(capitalSettlementVO, userInfo);
			} catch (MsgException e) {
				e.printStackTrace();
			}
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
	/**
	 * 查询审批流程进度
	 */
	@POST
	@Path("/approveLog")//为空则其完整路径就是类路径
	@Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approveLog(CapitalSettlementVO modelVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			msg =capitalSettlementConsumer.approveLog(modelVO, userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}

	
}
