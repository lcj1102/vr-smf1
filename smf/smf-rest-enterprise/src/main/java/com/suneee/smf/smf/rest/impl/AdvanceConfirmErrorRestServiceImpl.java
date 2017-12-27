package com.suneee.smf.smf.rest.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.component.cache.CacheUtils;
import com.suneee.smf.smf.api.rest.AdvanceConfirmErrorRestService;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.AdvanceConfirmErrorConsumer;
import com.suneee.smf.smf.model.AdvanceConfirmErrorDO;
import com.suneee.smf.smf.model.AdvanceConfirmErrorVO;
/**
 * @Description: 放款确认异常处理
 * @author: changzhaoyu
 * @date: 2017年12月21日 下午4:08:11
 */
@Service//指明dubbo服务
@Path("advanceConfirmError")
public class AdvanceConfirmErrorRestServiceImpl implements AdvanceConfirmErrorRestService{

	 @Autowired
	 private AdvanceConfirmErrorConsumer advanceConfirmErrorConsumer;
	 
	 private static final Logger log = LoggerFactory.getLogger(AdvanceConfirmErrorRestServiceImpl.class);
	
	/**
     * 分页查询
     */
    @SuppressWarnings("deprecation")
	@GET
    @Path("/errlist")//为空则其完整路径就是类路径
    @Produces("application/json")
	@Override
	public ResultMsg selectByPage(@QueryParam("code") String code,
    		@QueryParam("enterprise_application_name") String enterprise_application_name,
			@QueryParam("time_stamp") String time_stamp,
			@QueryParam("beginDateTS") String beginDateTS,
			@QueryParam("endDateTS") String endDateTS,
			@QueryParam("busi_date") String busi_date,
			@QueryParam("beginDateBD") String beginDateBD,
			@QueryParam("endDateBD") String endDateBD,
			@QueryParam("searchValue") String searchValue,
			@QueryParam("state_new") String state_new,
			@QueryParam("state_end") String state_end,
            @QueryParam("length") String length,
            @QueryParam("pageNum") String pageNum,
            @QueryParam("draw") String draw,
            @HeaderParam("sessionId") String sessionId) {
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	if(draw !=null && false == "".equals(draw)){
        		Integer drawInteger = Integer.parseInt(draw);
        		if (drawInteger == 1 && state_new == null ) {
        			state_new = "未处理";
				}
        	}
        	// 初始化
        	List<Object> list = new ArrayList<Object>();
        	Page<AdvanceConfirmErrorVO> page = new Page<AdvanceConfirmErrorVO>();
        	AdvanceConfirmErrorDO bean = new AdvanceConfirmErrorDO();
        	// 设置page属性
        	page.setPageSize(Integer.parseInt(length));// 每页条数
        	page.setPageNo(Integer.parseInt(pageNum));// 当前页
        	// 设置bean属性（查询条件）
        	bean.setCode(code);
        	bean.setEnterprise_application_name(enterprise_application_name);
        	bean.setSearchValue(searchValue);
        	bean.setState_new(state_new);
        	bean.setState_end(state_end);
			bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        	if (time_stamp != null && false == "".equals(time_stamp)) {
        		bean.setTime_stamp(DateUnit.string2date(time_stamp, "yyyy-MM-dd"));
        	}
        	if (beginDateTS != null && false == "".equals(beginDateTS)) {
        		bean.setBeginDateTS(DateUnit.string2date(beginDateTS, "yyyy-MM-dd"));
        	}
        	if (endDateTS != null && false == "".equals(endDateTS)) {
    			Date date =DateUnit.string2date(endDateTS, "yyyy-MM-dd");
       		 	bean.setEndDateTS(DateUnit.icDateByDay(date,1));
        	}
        	if (busi_date != null && false == "".equals(busi_date)) {
        		bean.setBusi_date(DateUnit.string2date(busi_date, "yyyy-MM-dd"));
        	}
        	if (beginDateBD != null && false == "".equals(beginDateBD)) {
        		bean.setBeginDateBD(DateUnit.string2date(beginDateBD, "yyyy-MM-dd"));
        	}
        	if (endDateBD != null && false == "".equals(endDateBD)) {
        		Date datebd =DateUnit.string2date(endDateBD, "yyyy-MM-dd");
       		 	bean.setEndDateBD(DateUnit.icDateByDay(datebd, 1));
        	}
        	list.add(advanceConfirmErrorConsumer.selectByPage(bean, page));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
	}
    /**
     * 根据放款确认单号获取数据
     */
    @GET
    @Path("/getByPrimaryKey")
    @Produces("application/json")
	@Override
	public ResultMsg getByPrimaryKey(@QueryParam("code")String code,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
	        List<Object> list = new ArrayList<Object>();//返回集合
	        if (code != null && !"".equals(code)) {
	        	AdvanceConfirmErrorDO AdvanceConfirmErrorDO = new AdvanceConfirmErrorDO();
	        	AdvanceConfirmErrorDO.setCode(code);
	        	AdvanceConfirmErrorDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
	        	list.add(advanceConfirmErrorConsumer.getByPrimaryKey(AdvanceConfirmErrorDO));
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
     * 放款确认异常处理任务调度
     */
    @GET
    @Path("/dealAdvanceConfirmError")
    @Produces("application/json")
	@Override
	public ResultMsg dealAdvanceConfirmError()
	{
    	log.info("----------------------放款确认异常处理任务调度执行开始！----------------------------");
    	ResultMsg resultMsg = new ResultMsg();

		try 
		{
			resultMsg = advanceConfirmErrorConsumer.getSmfAdvanceConfirmBypk();
		} catch (MsgException e) 
		{
			return new ResultMsg("0",e.getMessage());
		}catch (Exception e) 
		{
			return new ResultMsg("0","放款确认异常处理失败！");
		}
		log.info("----------------------放款确认异常处理任务调度执行结束！----------------------------");
		return resultMsg;
	}
    
    

}
