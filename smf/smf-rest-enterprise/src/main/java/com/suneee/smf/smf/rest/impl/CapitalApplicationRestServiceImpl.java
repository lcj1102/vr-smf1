package com.suneee.smf.smf.rest.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import com.suneee.smf.smf.api.rest.CapitalApplicationRestService;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.CapitalApplicationConsumer;
import com.suneee.smf.smf.model.AttachmentsDO;
import com.suneee.smf.smf.model.AttachmentsInsertDO;
import com.suneee.smf.smf.model.CapitalApplicationDO;
import com.suneee.smf.smf.model.CapitalApplicationInsertDO;
import com.suneee.smf.smf.model.CapitalApplicationVO;
/**
 * @Description: 资金使用申请
 * @author: 致远
 * @date: 2017年12月1日 下午4:08:11
 */
@Service//指明dubbo服务
@Path("capitalApplication")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class CapitalApplicationRestServiceImpl implements CapitalApplicationRestService {

	@Autowired
	private CapitalApplicationConsumer capitalApplicationConsumer;
	/**
	 * 查询资金使用申请（分页）
	 */
	@GET
    @Path("/list")
    @Produces("application/json")
    @Override
    public ResultMsg list(@QueryParam("code") String code,
    		@QueryParam("name") String name,
    		@QueryParam("enterprise_name") String enterprise_name,
    		@QueryParam("searchValue") String searchValue,
    		@QueryParam("beginDate") String beginDate,
    		@QueryParam("endDate") String endDate,
    		@QueryParam("state") String state,
    		@QueryParam("length") String length,
            @QueryParam("pageNum") String pageNum,
            @HeaderParam("sessionId") String sessionId) {
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	List<Object> list = new ArrayList<Object>();
        	Page<CapitalApplicationVO> page = new Page<CapitalApplicationVO>();
        	page.setPageSize(Integer.parseInt(length));// 每页条数
        	page.setPageNo(Integer.parseInt(pageNum));// 当前页
        	
        	CapitalApplicationDO applicationDO = new CapitalApplicationDO();
        	applicationDO.setName(name);
        	applicationDO.setCode(code);
        	applicationDO.setSearchValue(searchValue);
        	applicationDO.setEnterprise_name(enterprise_name);
        	applicationDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        	applicationDO.setState(state);
        	if (beginDate != null && false == "".equals(beginDate)) {
        		applicationDO.setStartDate(DateUnit.string2date(beginDate, "yyyy-MM-dd"));
        	}
        	if (endDate != null && false == "".equals(endDate)) {
        		applicationDO.setEndDate(DateUnit.string2date(endDate, "yyyy-MM-dd"));
        	}
        	list.add(capitalApplicationConsumer.listByPage(page,applicationDO));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
    }
	/**
	 * 插入资金使用申请
	 */
	@POST
    @Path("/insert")
    @Produces("application/json")
    @Override
    public ResultMsg insert(CapitalApplicationInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        try {
	        	CapitalApplicationDO laDO = insertDO.getCapitalApplicationDO().get(0);
	        	if ("submit".equals(insertDO.getSaveType())) {
	        		laDO.setState(Constant.CAPITALAPPLICATION_STATE_APPROVING);
	        	} else {
	        		laDO.setState(Constant.CAPITALAPPLICATION_STATE_NEW);
	        	}
	        	msg = capitalApplicationConsumer.addCapitalApplication(laDO,userInfo);
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
	 * 根据id查询资金使用申请
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
	        	CapitalApplicationVO vo = new CapitalApplicationVO();
	        	vo.setPk_capital_application(id);
	        	vo.setEnterpriseid(userInfo.getEnterpriseid().longValue());
	            pb.add(capitalApplicationConsumer.getRestByPrimaryKey(vo));
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
	 * 修改资金使用申请
	 */
	@POST
    @Path("/update")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
    public ResultMsg update(CapitalApplicationInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        try {
	        	CapitalApplicationDO laDO = insertDO.getCapitalApplicationDO().get(0);
	        	if ("submit".equals(insertDO.getSaveType())) {
	        		laDO.setState(Constant.CAPITALAPPLICATION_STATE_APPROVING);
	        	} else {
	        		laDO.setState(Constant.CAPITALAPPLICATION_STATE_NEW);
	        	}
	        	msg = capitalApplicationConsumer.modifyCapitalApplication(laDO,insertDO.getDeleteItemId(),userInfo);
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
	 * 删除
	 */
	@POST
    @Path("/deleteSelect")
    @Produces("application/json")
	@Override
    public ResultMsg deleteSelect(CapitalApplicationInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	/*int count = 0;*/
        	if (insertDO == null || insertDO.getCapitalApplicationDO() == null) {
        		msg.setMsg("删除失败！！");
        	} else {
				try {
					msg =capitalApplicationConsumer.deleteCapitalApplication(insertDO, userInfo);
				} catch (MsgException e) {
					msg.setMsg(e.getMessage());
		            msg.setCode(e.getCode());
				}
        	}
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }
	/**
	 * 提交审批
	 */
	@POST
	@Path("/submitModel")
	@Produces("application/json")
	@Override
	public ResultMsg submitModel(CapitalApplicationInsertDO insertDO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		//判断用户登录是否过期
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo == null || userInfo.getEnterpriseid() == null) {
			msg.setCode("0");
			msg.setMsg("用户登陆过期，请重新登陆！");
			return msg;
		}
		//调用consumer提交
		try {
			msg = capitalApplicationConsumer.submitModel(insertDO,userInfo);
		} catch (MsgException e) {
			msg.setCode(e.getCode());
			msg.setMsg(e.getMessage());
		}
		
		return msg;
	}
	/**
	 * 查询当前用户是否可以审批该数据
	 */
	@POST
    @Path("/checkApproveById")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg checkApproveById(CapitalApplicationVO modelVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	msg = capitalApplicationConsumer.checkApproveById(modelVO,userInfo);
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
	public ResultMsg approveSelect(CapitalApplicationVO modelVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			try {
				msg =capitalApplicationConsumer.approveSelect(modelVO, userInfo);
			} catch (MsgException e) {
				msg.setCode(e.getCode());
				msg.setMsg(e.getMessage());
			}
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
	/**
	 * @Title: approveLog 
	 * @author:致远
	 * @Description: 审批进度
	 * @param enterpriseVO
	 * @param sessionId
	 * @return
	 * @return: ResultMsg
	 */
	@POST
	@Path("/approveLog")//为空则其完整路径就是类路径
	@Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approveLog(CapitalApplicationVO modelVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			msg =capitalApplicationConsumer.approveLog(modelVO, userInfo);
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
	public ResultMsg fileListById(@QueryParam("capitalApplicationId") Long capitalApplicationId,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null) {
			msg = capitalApplicationConsumer.getFileListById(capitalApplicationId,userInfo);
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
	        	msg = capitalApplicationConsumer.addFile(file,userInfo);
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
							count += capitalApplicationConsumer.deleteFile(bean,userInfo);
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
	 * 查询企业列表
	 */
	@GET
    @Path("/getEnterpriseList")//为空则其完整路径就是类路径
	@Produces("application/json")
	@Override
	public ResultMsg getEnterpriseList(@QueryParam("keyword") String keyword,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	if (keyword != null && false == "".equals(keyword)) {
        		try {
					keyword = URLDecoder.decode(keyword, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
        	}
        	msg = capitalApplicationConsumer.getEnterpriseList(keyword,userInfo);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
	}
	/**
	 * 根据code查询企业信息
	 */
	@GET
    @Path("/getEnterpriseByCode")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
    public ResultMsg getEnterpriseByCode(@QueryParam("code") String code,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        List<Object> pb = new ArrayList<Object>();//返回集合
	        if (code != null && !"".equals(code)) {
	            pb.add(capitalApplicationConsumer.getEnterpriseByCode(code,userInfo));
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
	 * 根据code模糊查询资金使用申请列表
	 */
	@GET
    @Path("/getCapitalApplicationList")//为空则其完整路径就是类路径
	@Produces("application/json")
	@Override
	public ResultMsg getCapitalApplicationList(@QueryParam("enterpriseId") String enterpriseId,@QueryParam("keyword") String keyword,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	if (keyword != null && false == "".equals(keyword)) {
        		try {
					keyword = URLDecoder.decode(keyword, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
        	}
        	Long pk_enterprise = null;
        	if (enterpriseId != null && false == "".equals(enterpriseId)) {
        		pk_enterprise = Long.valueOf(enterpriseId);
        	}
        	msg = capitalApplicationConsumer.getCapitalApplicationList(keyword,pk_enterprise,userInfo);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
	}
	/**
	 * 根据code查询资金使用申请
	 */
	@GET
    @Path("/getCapitalApplicationByCode")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
    public ResultMsg getCapitalApplicationByCode(@QueryParam("code") String code,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        List<Object> pb = new ArrayList<Object>();//返回集合
	        if (code != null && !"".equals(code)) {
	        	CapitalApplicationDO vo = new CapitalApplicationDO();
	        	vo.setCode(code);
	        	vo.setEnterpriseid(userInfo.getEnterpriseid().longValue());
	            pb.add(capitalApplicationConsumer.getCapitalApplicationByCode(vo));
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
	 * 根据code查询资金使用申请,细单中的数量为可发货最大数量
	 */
	@GET
    @Path("/selectCapitalApplicationByCode")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
    public ResultMsg selectCapitalApplicationByCode(@QueryParam("code") String code,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        List<Object> pb = new ArrayList<Object>();//返回集合
	        if (code != null && !"".equals(code)) {
	        	CapitalApplicationDO vo = new CapitalApplicationDO();
	        	vo.setCode(code);
	        	vo.setEnterpriseid(userInfo.getEnterpriseid().longValue());
	            pb.add(capitalApplicationConsumer.selectCapitalApplicationByCode(vo));
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
}
