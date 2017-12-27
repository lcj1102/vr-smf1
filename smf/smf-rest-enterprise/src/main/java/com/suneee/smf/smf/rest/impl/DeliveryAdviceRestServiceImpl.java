package com.suneee.smf.smf.rest.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
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
import com.suneee.smf.smf.api.rest.DeliveryAdviceRestService;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.DeliveryAdviceConsumer;
import com.suneee.smf.smf.model.DeliveryAdviceDO;
import com.suneee.smf.smf.model.DeliveryAdviceList;
import com.suneee.smf.smf.model.DeliveryAdviceVO;
/**
 * 
 * @Description: 发货通知rest
 * @author:daiaojie
 * @date: 2017年12月4日 上午9:29:12
 */
@Service
@Path("/deliveryAdvice")
public class DeliveryAdviceRestServiceImpl implements DeliveryAdviceRestService {

    private static Logger log = LoggerFactory.getLogger(DeliveryAdviceRestServiceImpl.class);
    
    @Autowired
    private DeliveryAdviceConsumer deliveryAdviceConsumer;
    
    /**
     * 查询列表
     */
    @GET
    @Path("/queryDeliveryAdvice")
    @Produces("application/json")
    @Override
    public ResultMsg deliveryAdviceList(@QueryParam("length") String length,
								@QueryParam("pageNum") String pageNum,
								@QueryParam("searchValue") String searchValue,
								@QueryParam("state") String state,
								@QueryParam("code") String code,
								@QueryParam("name") String name,
								@QueryParam("enterprise_name") String enterprise_name,
								@QueryParam("starttime") String starttime,
								@QueryParam("endtime") String endtime,
								@HeaderParam("sessionId") String sessionId) {
    	SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	List<Object> list = new ArrayList<Object>();
        	//分页
        	Page<DeliveryAdviceVO> page = new Page<DeliveryAdviceVO>();
        	DeliveryAdviceVO deliveryAdviceVO = new DeliveryAdviceVO();
        	// 设置page属性（分页）
        	page.setPageSize(Integer.parseInt(length));// 每页条数
        	page.setPageNo(Integer.parseInt(pageNum));// 当前页
        	// 设置bean属性（查询条件）
        	deliveryAdviceVO.setState(state);
        	deliveryAdviceVO.setSearchValue(searchValue);
        	deliveryAdviceVO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        	deliveryAdviceVO.setCode(code);
        	deliveryAdviceVO.setName(name);
        	deliveryAdviceVO.setEnterprise_name(enterprise_name);
        	if(starttime != null & !"".equals(starttime)){
        		deliveryAdviceVO.setStarttime(DateUnit.string2date(starttime, DateUnit.YYYY_MM_DD));
        	}
        	if(endtime != null & !"".equals(endtime)){
        		//因传过来的日期默认是0点，结束日期应该加24小时
        		Calendar c = Calendar.getInstance();
        		Date date = DateUnit.string2date(endtime, DateUnit.YYYY_MM_DD);
        		c.setTime(date);
        		c.add(Calendar.DAY_OF_MONTH, 1);
        		Date endDate = c.getTime();
        		deliveryAdviceVO.setEndtime(endDate);
        	}
        	list.add(deliveryAdviceConsumer.queryByPage(page, deliveryAdviceVO));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
	}
    /**
     * 新增保存
     */
    @POST
    @Path("/insert")
    @Produces("application/json")
    @Override
    public ResultMsg insert(DeliveryAdviceList deliveryAdviceList,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        try {
	        	DeliveryAdviceVO deliveryAdviceVO = deliveryAdviceList.getDeliveryAdviceList().get(0);
                deliveryAdviceVO.setState(Constant.DELIVERYADVICE_STATE_NEW);
	        	msg = deliveryAdviceConsumer.add(deliveryAdviceVO,userInfo);
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
     * 新增保存并提交
     */
    @POST
    @Path("/insertAndSubmit")
    @Produces("application/json")
    @Override
    public ResultMsg insertAndSubmit(DeliveryAdviceList deliveryAdviceList,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        try {
	        	DeliveryAdviceVO deliveryAdviceVO = deliveryAdviceList.getDeliveryAdviceList().get(0);
                deliveryAdviceVO.setState(Constant.DELIVERYADVICE_STATE_APPROVING);
	        	msg = deliveryAdviceConsumer.add(deliveryAdviceVO,userInfo);
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
    * 根据主键id查询发货通知数据
    */
	@GET
    @Path("/queryDeliveryAdviceByID")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
    public ResultMsg selectByPrimaryKey(@QueryParam("id") Long id,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        List<Object> resultData = new ArrayList<Object>();//返回集合
	        if (id != null && !"".equals(id)) {
	        	DeliveryAdviceDO deliveryAdviceDO = new DeliveryAdviceDO();
	        	deliveryAdviceDO.setPk_delivery_advice(id);
	        	deliveryAdviceDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
	        	resultData.add(deliveryAdviceConsumer.getRestByPrimaryKey(deliveryAdviceDO));
	            msg.setData(resultData);
	        } else {
	            msg.setData(resultData);
	            msg.setMsg("查询失败");
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
    }
	/**
	 * 更新数据保存
	 */
	@POST
    @Path("/save")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
    public ResultMsg save(DeliveryAdviceList deliveryAdviceList,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
            DeliveryAdviceVO deliveryAdviceVO = deliveryAdviceList.getDeliveryAdviceList().get(0);
            String delIds = deliveryAdviceList.getDelIds();
            deliveryAdviceVO.setState(Constant.DELIVERYADVICE_STATE_NEW);
        	msg = deliveryAdviceConsumer.modify(deliveryAdviceVO,userInfo,delIds);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }
	/**
	 * 更新数据保存并提交
	 */
	@POST
    @Path("/saveAndSubmit")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
    public ResultMsg saveAndSubmit(DeliveryAdviceList deliveryAdviceList,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	try {
	    		DeliveryAdviceVO deliveryAdviceVO = deliveryAdviceList.getDeliveryAdviceList().get(0);
	            String delIds = deliveryAdviceList.getDelIds();
	            deliveryAdviceVO.setState(Constant.DELIVERYADVICE_STATE_APPROVING);
	         	msg = deliveryAdviceConsumer.modify(deliveryAdviceVO,userInfo,delIds);
			} catch (Exception e) {
				msg.setCode("0");
				msg.setMsg("修改失败");
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
    @Path("/deleteSelected")
    @Produces("application/json")
	@Override
    public ResultMsg deleteSelect(DeliveryAdviceList deliveryAdviceList,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	if (deliveryAdviceList == null || deliveryAdviceList.getDeliveryAdviceList() == null) {
        		msg.setMsg("删除失败！！");
        	} else {
        		msg = deliveryAdviceConsumer.delete(deliveryAdviceList, userInfo);
        	}
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }
	/**
	 * 审核
	 */
	@POST
    @Path("/approveSelected")
    @Produces("application/json")
	@Override
    public ResultMsg approveSelect(DeliveryAdviceList deliveryAdviceList,@HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	if (deliveryAdviceList == null || deliveryAdviceList.getDeliveryAdviceList() == null) {
        		msg.setMsg("审核失败！！");
        	} else {
                msg = deliveryAdviceConsumer.approve(deliveryAdviceList, userInfo, sessionId);
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
    public ResultMsg checkApproveById(DeliveryAdviceVO deliveryAdviceVO, @HeaderParam("sessionId") String sessionId) {
        ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
            msg = deliveryAdviceConsumer.checkApproveById(deliveryAdviceVO,userInfo);
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
   public ResultMsg submitModel(DeliveryAdviceVO deliveryAdviceVO,@HeaderParam("sessionId") String sessionId) {
       ResultMsg msg = new ResultMsg();
       SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
       if (userInfo != null && userInfo.getEnterpriseid() != null) {
       	msg = deliveryAdviceConsumer.submitModel(deliveryAdviceVO,userInfo);
       } else {
       	msg.setMsg("用户登陆过期，请重新登陆！");
       }
       return msg;
   }
   	/**
	 * 查看审核流程
	 */
	@POST
    @Path("/viewProcess")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approveLog(DeliveryAdviceVO deliveryAdviceVO,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class,sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			msg = deliveryAdviceConsumer.approveLog(deliveryAdviceVO, userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
}
