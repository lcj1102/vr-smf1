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
import com.suneee.scn.scf.model.EnterpriseVO;
import com.suneee.smf.smf.api.rest.CapitalInjectionRestService;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.CapitalInjectionConsumer;
import com.suneee.smf.smf.consumer.impl.CollectionConfirmConsumer;
import com.suneee.smf.smf.model.AttachmentsDO;
import com.suneee.smf.smf.model.AttachmentsInsertDO;
import com.suneee.smf.smf.model.CapitalInjectionDO;
import com.suneee.smf.smf.model.CapitalInjectionInsertDO;
import com.suneee.smf.smf.model.CapitalInjectionVO;

@Service//指明dubbo服务
@Path("/capitalInjection")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class CapitalInjectionRestServiceImpl implements CapitalInjectionRestService{

	@Autowired
	private CapitalInjectionConsumer capitalInjectionConsumer;
	
	@Autowired
	private CollectionConfirmConsumer collectionConfirmConsumer;

	/**
	 * 列表查询
	 */
	@GET
    @Path("/list")
    @Produces("application/json")
	@Override
	public ResultMsg list(@QueryParam("code") String code,
			@QueryParam("name") String name,
			@QueryParam("beginForBusiDate") String beginForBusiDate,
			@QueryParam("endForBusiDate") String endForBusiDate,
			@QueryParam("state") String state,
            @QueryParam("length") String length,
            @QueryParam("pageNum") String pageNum,
            @QueryParam("searchValue") String searchValue,
            @HeaderParam("sessionId") String sessionId) {
		
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        
        if (userInfo == null || userInfo.getEnterpriseid() == null) {
        	result.setMsg("用户登陆过期，请重新登陆！");
        	result.setCode("0");
        	return result;
        }
    	// 初始化
    	List<Object> list = new ArrayList<Object>();
    	Page<CapitalInjectionVO> page = new Page<CapitalInjectionVO>();
    	
    	CapitalInjectionVO bean = new CapitalInjectionVO();
    	// 设置page属性
    	page.setPageSize(Integer.parseInt(length));// 每页条数
    	page.setPageNo(Integer.parseInt(pageNum));// 当前页
    	
    	// 设置bean属性（查询条件）
    	bean.setCode(code);
    	bean.setName(name);
    	bean.setState(state);
    	bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
    	bean.setSearchValue(searchValue);
    	
    	if (beginForBusiDate != null && false == "".equals(beginForBusiDate)) {
    		bean.setStartForBusiDate(DateUnit.string2date(beginForBusiDate, "yyyy-MM-dd"));
    	}
    	if (endForBusiDate != null && false == "".equals(endForBusiDate)) {
    		bean.setEndForBusiDate(DateUnit.string2date(endForBusiDate, "yyyy-MM-dd"));
    	}
    	
    	//调用方法，封装返回数据
    	list.add(capitalInjectionConsumer.queryPage(page, bean));
    	result.setData(list);
    
        return result;
    }
	
	/**
	 * 新增
	 */
	@POST
    @Path("/insert")
    @Produces("application/json")
    @Override
    public ResultMsg insert(CapitalInjectionInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
        
		ResultMsg msg = new ResultMsg();
        //判断用户登录是否过期
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null) {
        	msg.setCode("0");
        	msg.setMsg("用户登陆过期，请重新登陆！");
        	return msg;
        }
        //调用consumer实现审批
        try {
        	CapitalInjectionDO capitalInjectionDO = insertDO.getInsertDO().get(0);
        	if ("submit".equals(insertDO.getSaveType())) {
        		capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_APPROVING);
			}else{
				capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_NEW);
			}
        	msg = capitalInjectionConsumer.insert(capitalInjectionDO, userInfo);
            
        } catch (Exception e) {
        	msg.setCode("0");
            msg.setMsg("新增失败！！");
            e.printStackTrace();
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
    public ResultMsg deleteSelect(CapitalInjectionInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        
        //判断用户登录是否过期
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null) {
        	msg.setCode("0");
        	msg.setMsg("用户登陆过期，请重新登陆！");
        	return msg;
        }
        //判断参数是否为空
        if (insertDO == null || insertDO.getInsertDO() == null) {
        	msg.setCode("0");
        	msg.setMsg("删除失败，参数不能为空！！");
        	return msg;
        }
        //调用consumer实现删除功能
        msg = capitalInjectionConsumer.delete(insertDO, userInfo);
        
        return msg;
    }
	
	
	/**
	 * 编辑
	 */
	@POST
	@Path("/update")
	@Produces("application/json")
	@Consumes("application/json")
	@Override
	public ResultMsg update(CapitalInjectionInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		
		//判断用户登录状态
		if (userInfo == null || userInfo.getEnterpriseid() == null) {
			msg.setCode("0");
			msg.setMsg("用户登陆过期，请重新登陆！");
			return msg;
		}
		try{
			CapitalInjectionDO capitalInjectionDO = insertDO.getInsertDO().get(0);
			if ("submit".equals(insertDO.getSaveType())) {
				capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_APPROVING);
			}else{
				capitalInjectionDO.setState(Constant.CAPITALINJECTION_STATE_NEW);
			}
			msg = capitalInjectionConsumer.modify(capitalInjectionDO, userInfo);
			
		}catch(Exception e){
			msg.setCode("0");
			msg.setMsg("修改失败");
		}
		return msg;
	}
	
	
	/**
	 * 根据主键查询
	 */
	@GET
    @Path("/byid")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
    public ResultMsg selectById(@QueryParam("pk_capital_injection") Long pk_capital_injection,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        //判断用户登录是否过期
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null) {
        	msg.setCode("0");
        	msg.setMsg("用户登陆过期，请重新登陆！");
        	return msg;
        }
        //判断主键不能 为空
        if (pk_capital_injection == null || "".equals(pk_capital_injection.toString())) {
        	msg.setCode("0");
        	msg.setMsg("任务主键不能为空");
        	return msg;
        }
        //调用consumer查询回购单
        List<Object> pb = new ArrayList<Object>();//返回集合
        //封装数据
    	CapitalInjectionDO capitalInjectionDO = new CapitalInjectionDO();
    	capitalInjectionDO.setPk_capital_injection(pk_capital_injection);
    	capitalInjectionDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
    	pb.add(capitalInjectionConsumer.getById(capitalInjectionDO));
    	msg.setCode("1");
    	msg.setData(pb);
    	
        return msg;
    }

	/**
	 * 提交审批
	 */
	@POST
	@Path("/submitModel")
	@Produces("application/json")
	@Override
	public ResultMsg submitModel(CapitalInjectionVO capitalInjectionVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		//判断用户登录是否过期
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo == null || userInfo.getEnterpriseid() == null) {
			msg.setCode("0");
			msg.setMsg("用户登陆过期，请重新登陆！");
			return msg;
		}
		//调用consumer提交
		msg = capitalInjectionConsumer.submitModel(capitalInjectionVO,userInfo);
		
		return msg;
	}
	
	/**
	 * 查看当前审批进度
	 */
	@POST
    @Path("/approveLog")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approveLog(CapitalInjectionVO capitalInjectionVO, @HeaderParam("sessionId") String sessionId) {
		
		ResultMsg msg = new ResultMsg();
		
		//判断用户登录是否过期
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null) {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        	return msg;
        } 
        
        //调用相应方法
        msg = capitalInjectionConsumer.approveLog(capitalInjectionVO,userInfo);
        return msg;
	}
	
	/**
	 * 查看当前用户是否有对应数据的审批权限
	 */
	@POST
    @Path("/checkApproveById")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg checkApproveById(CapitalInjectionVO bean, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		//判断用户登录是否过期
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null) {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        	return msg;
        }
        
        //调用查看权限方法
        msg = capitalInjectionConsumer.checkApproveById(bean,userInfo);
        
        return msg;
	}
	
	/**
	 * 审批
	 */
	@POST
    @Path("/approveSelect")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approveSelect(CapitalInjectionVO capitalInjectionVO, @HeaderParam("sessionId") String sessionId) {
		
		ResultMsg msg = new ResultMsg();
		//判断用户登录是否过期
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo == null || userInfo.getEnterpriseid() == null) {
			msg.setCode("0");
			msg.setMsg("用户登陆过期，请重新登陆！");
			return msg;
		}	
		//调用审批方法
		try{
			msg =capitalInjectionConsumer.approve(capitalInjectionVO, userInfo);
			
		} catch (MsgException e) {
			e.printStackTrace();
			msg.setCode("0");
			msg.setMsg(e.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			msg.setCode("0");
			msg.setMsg("审核失败");
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
	public ResultMsg fileListById(@QueryParam("capitalInjectionId") Long capitalInjectionId,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null) {
			msg = capitalInjectionConsumer.getFileListById(capitalInjectionId,userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
	
	/**
	 * 新增附件
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
	        	msg = capitalInjectionConsumer.addFile(file.getFile().get(0),userInfo);
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
	 * 删除附件
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
							count += capitalInjectionConsumer.deleteFile(bean,userInfo);
	        		}
        		} catch (MsgException e) {
        			msg.setMsg(e.getMessage());
    	            msg.setCode(e.getCode());
    	            return msg;
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
	
	
	@GET
    @Path("/getCapitalInjectionByName")
    @Produces("application/json")
	@Override
	   public ResultMsg getCapitalInjectionByName(@QueryParam("keyword") String keyword,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	try {
				keyword = URLDecoder.decode(keyword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	List<Object> list = new ArrayList<Object>();
            List<CapitalInjectionVO> relist = capitalInjectionConsumer.getCapitalInjectionByName(keyword, userInfo.getEnterpriseid().longValue());
            list.addAll(relist);
            msg.setData(list);
            msg.setCode("1");
            msg.setMsg("查询成功");
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }

	@GET
    @Path("/getCapitalInjectionByCode")
    @Produces("application/json")
	@Override
	public ResultMsg getCapitalInjectionByCode(@QueryParam("code") String code,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	List<Object> list = new ArrayList<Object>();
        	CapitalInjectionVO capitalInjectionVO = capitalInjectionConsumer.getCapitalInjectionByCode(code, userInfo.getEnterpriseid().longValue());
            list.add(capitalInjectionVO);
            msg.setData(list);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
	}

	@GET
    @Path("/selectEnterpriseByName")
    @Produces("application/json")
    @Override
    public ResultMsg selectEnterpriseByName(@QueryParam("keyword") String keyword,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	try {
				keyword = URLDecoder.decode(keyword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	List<Object> list = new ArrayList<Object>();
            List<EnterpriseVO> relist = capitalInjectionConsumer.selectEnterpriseByName(keyword, userInfo.getEnterpriseid().longValue());
            list.addAll(relist);
            msg.setData(list);
            msg.setCode("1");
            msg.setMsg("查询成功");
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }
	
	@GET
    @Path("/getEnterpriseByCode")
    @Produces("application/json")
    @Override
	public ResultMsg getEnterpriseByCode(@QueryParam("code")String code,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	List<Object> list = new ArrayList<Object>();
            EnterpriseVO enterpriseVO = capitalInjectionConsumer.getEnterpriseByCode(code, userInfo.getEnterpriseid().longValue());
            list.add(enterpriseVO);
            msg.setData(list);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
	}
	
	
}
