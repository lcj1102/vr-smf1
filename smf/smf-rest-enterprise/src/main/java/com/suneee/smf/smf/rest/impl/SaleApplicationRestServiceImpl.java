package com.suneee.smf.smf.rest.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.component.cache.CacheUtils;
import com.suneee.scn.scf.model.EnterpriseVO;
import com.suneee.smf.smf.api.rest.SaleApplicationRestService;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.SaleApplicationConsumer;
import com.suneee.smf.smf.model.SaleApplicationDO;
import com.suneee.smf.smf.model.SaleApplicationInsertDO;
import com.suneee.smf.smf.model.SaleApplicationVO;

/**
 * 
 * @Description: TODO 发货申请rest
 * @author: 崔亚强
 * @date: 2017年12月7日 下午3:53:27
 */
@Service//指明dubbo服务
@Path("saleApplication")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class SaleApplicationRestServiceImpl implements SaleApplicationRestService {
	private static final Logger log= LoggerFactory.getLogger(SaleApplicationRestServiceImpl.class);
    @Autowired
    private SaleApplicationConsumer saleApplicationConsumer;

    /**
     * 分页查询
     */
    @GET
    @Path("/list")//为空则其完整路径就是类路径
    @Produces("application/json")
    @Override
    public ResultMsg selectByPage(@QueryParam("code") String code,
    		@QueryParam("capital_application_code") String capital_application_code,
    		@QueryParam("enterprise_application_name") String enterprise_application_name,
    		@QueryParam("enterprise_payment_name") String enterprise_payment_name,
    		@QueryParam("minAmount") String minAmount,
			@QueryParam("maxAmount") String maxAmount,
			@QueryParam("beginDate") String beginDate,
			@QueryParam("endDate") String endDate,
			@QueryParam("searchValue") String searchValue,
			@QueryParam("state") String state,
            @QueryParam("length") String length,
            @QueryParam("pageNum") String pageNum,
            @HeaderParam("sessionId") String sessionId){
    	SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	// 初始化
        	List<Object> list = new ArrayList<Object>();
        	Page<SaleApplicationVO> page = new Page<SaleApplicationVO>();
        	SaleApplicationDO bean = new SaleApplicationDO();
        	// 设置page属性
        	page.setPageSize(Integer.parseInt(length));// 每页条数
        	page.setPageNo(Integer.parseInt(pageNum));// 当前页
        	// 设置bean属性（查询条件）
        	bean.setCode(code);
        	bean.setCapital_application_code(capital_application_code);
        	bean.setEnterprise_application_name(enterprise_application_name);
        	bean.setEnterprise_payment_name(enterprise_payment_name);
        	bean.setSearchValue(searchValue);
        	bean.setState(state);
			bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        	if (minAmount != null && false == "".equals(minAmount)) {
        		bean.setMinAmount(Double.valueOf(minAmount));
        	}
        	if (maxAmount != null && false == "".equals(maxAmount)) {
        		bean.setMaxAmount(Double.valueOf(maxAmount));
        	}
        	if (beginDate != null && false == "".equals(beginDate)) {
        		bean.setBeginDate(DateUnit.string2date(beginDate, "yyyy-MM-dd"));
        	}
        	if (endDate != null && false == "".equals(endDate)) {
        		bean.setEndDate(DateUnit.icDateByDay(DateUnit.string2date(endDate, "yyyy-MM-dd"), 1));
        	}
        	list.add(saleApplicationConsumer.selectByPage(bean, page));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
    }

    /**
     * 新增
     */
    @POST
    @Path("/insertDO")
    @Produces("application/json")
    @Override
    public ResultMsg insertDO(SaleApplicationInsertDO insertDO,@HeaderParam("sessionId") String sessionId){
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        try {
	        	SaleApplicationDO saleApplicationDO = insertDO.getInsertDO().get(0);
	        	if ("insertAndSubmit".equals(insertDO.getSaveType())) {
	        		saleApplicationDO.setState(Constant.SALEAPPLICATION_STATE_APPROVING);
	        	} else {
	        		saleApplicationDO.setState(Constant.SALEAPPLICATION_STATE_NEW);
	        	}
	        	msg = saleApplicationConsumer.insertDO(saleApplicationDO,userInfo);
	        } catch (MsgException e) {
	            msg.setMsg(e.getMessage());
	            e.printStackTrace();
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
    }

    /**
     * 作废
     */
    @POST
    @Path("/delete")
    @Produces("application/json")
    @Override
    public ResultMsg delete(SaleApplicationInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        msg = saleApplicationConsumer.delete(insertDO, userInfo);
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
    public ResultMsg checkApproveById(SaleApplicationVO saleApplicationVO, @HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
            msg = saleApplicationConsumer.checkApproveById(saleApplicationVO,userInfo);
        } else {
            msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }

    /**
     * 审批
     */
    @POST
    @Path("/approve")
    @Produces("application/json")
    @Override
    public ResultMsg approve(SaleApplicationInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	SaleApplicationDO saleApplicationDO = insertDO.getInsertDO().get(0);
        	try{
        		 msg = saleApplicationConsumer.approve(saleApplicationDO, userInfo, sessionId);
        	}catch (MsgException e) {
    			e.printStackTrace();
    			msg.setCode("0");
    			msg.setMsg(e.getMessage());
    		}catch(Exception e){
    			e.printStackTrace();
    			log.info("saleApplaction aprove ================"+e.getMessage());
    			msg.setCode("0");
    			msg.setMsg("审核失败");
    		}
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }
    
    /**
     * 再次触发收款(测试)
     */
    @POST
    @Path("/reCollection")
    @Produces("application/json")
    @Override
    public ResultMsg reCollection(SaleApplicationInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	SaleApplicationDO saleApplicationDO = insertDO.getInsertDO().get(0);
        	try{
        		 msg = saleApplicationConsumer.reCollection(saleApplicationDO, userInfo, sessionId);
        	}catch (MsgException e) {
    			e.printStackTrace();
    			msg.setCode("0");
    			msg.setMsg(e.getMessage());
    		}catch(Exception e){
    			e.printStackTrace();
    			msg.setCode("0");
    			msg.setMsg("收款确认失败");
    		}
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }

    /**
     * 根据id唯一查询
     */
    @GET
    @Path("/getDOByPrimaryKey")
    @Produces("application/json")
	@Override
	public ResultMsg getDOByPrimaryKey(@QueryParam("pk_sale_application")Long pk_sale_application,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        List<Object> list = new ArrayList<Object>();//返回集合
	        if (pk_sale_application != null && !"".equals(pk_sale_application)) {
	        	SaleApplicationDO saleApplicationDO = new SaleApplicationDO();
	        	saleApplicationDO.setPk_sale_application(pk_sale_application);
	        	saleApplicationDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
	        	list.add(saleApplicationConsumer.getDOByPrimaryKey(saleApplicationDO));
	            msg.setData(list);
	        } else {
	            msg.setMsg("查询失败");
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
	}
    
    /**
     * 修改
     */
    @POST
    @Path("/update")
    @Produces("application/json")
    @Override
    public ResultMsg update(SaleApplicationInsertDO insertDO,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	SaleApplicationDO saleApplicationDO = insertDO.getInsertDO().get(0);
        	if ("saveAndSubmit".equals(insertDO.getSaveType())) {
        		saleApplicationDO.setState(Constant.SALEAPPLICATION_STATE_APPROVING);
        	} else {
        		saleApplicationDO.setState(Constant.SALEAPPLICATION_STATE_NEW);
        	}
        	msg = saleApplicationConsumer.update(saleApplicationDO, userInfo);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
    }

    /**
     * 提交
     */
    @POST
    @Path("/submitModel")
    @Produces("application/json")
   	@Override
   	public ResultMsg submitModel(SaleApplicationDO saleApplicationDO,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
    	SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
    	if (userInfo != null && userInfo.getEnterpriseid() != null) {
    		msg = saleApplicationConsumer.submitModel(saleApplicationDO,userInfo);
    	} else {
    		msg.setMsg("用户登陆过期，请重新登陆！");
    	}
    	return msg;
    }
    
    /**
	 * 查看审批流程
	 */
	@POST
    @Path("/approveLog")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approveLog(SaleApplicationVO saleApplicationVO,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class,sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			msg = saleApplicationConsumer.approveLog(saleApplicationVO, userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
	
	@GET
    @Path("/getEnterpriseByName")
    @Produces("application/json")
    @Override
    public ResultMsg getEnterpriseByName(@QueryParam("keyword") String keyword,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	try {
				keyword = URLDecoder.decode(keyword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	List<Object> list = new ArrayList<Object>();
            List<EnterpriseVO> relist = saleApplicationConsumer.getEnterpriseByName(keyword, userInfo.getEnterpriseid().longValue());
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
            List<EnterpriseVO> relist = saleApplicationConsumer.selectEnterpriseByName(keyword, userInfo.getEnterpriseid().longValue());
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
            EnterpriseVO enterpriseVO = saleApplicationConsumer.getEnterpriseByCode(code, userInfo.getEnterpriseid().longValue());
            list.add(enterpriseVO);
            msg.setData(list);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
	}
	

}
