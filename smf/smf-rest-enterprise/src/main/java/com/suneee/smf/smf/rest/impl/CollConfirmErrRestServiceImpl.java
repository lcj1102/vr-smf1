package com.suneee.smf.smf.rest.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.component.cache.CacheUtils;
import com.suneee.smf.smf.api.rest.CollConfirmErrRestService;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.CollConfirmErrConsumer;
import com.suneee.smf.smf.model.CollectionconfirmErrorDO;
import com.suneee.smf.smf.model.CollectionconfirmErrorVO;

/**
 * 
 * @Description: TODOrest
 * @author: yunhe
 * @date: 2017年12月8日 14:53:27
 */
@Service//指明dubbo服务
@Path("collConfirmErr")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class CollConfirmErrRestServiceImpl implements CollConfirmErrRestService {

    @Autowired
    private CollConfirmErrConsumer collConfirmErrConsumer;
    

    /**
     * 分页查询
     */
    @GET
    @Path("/errlist")//为空则其完整路径就是类路径
    @Produces("application/json")
    @Override
    public ResultMsg selectByPage(@QueryParam("code") String code,
    		@QueryParam("enterprise_payment_name") String enterprise_payment_name,
			@QueryParam("time_stamp") String time_stamp,
			@QueryParam("beginDateTS") String beginDateTS,
			@QueryParam("endDateTS") String endDateTS,
			@QueryParam("busi_date") String busi_date,
			@QueryParam("beginDateBD") String beginDateBD,
			@QueryParam("endDateBD") String endDateBD,
			@QueryParam("notFirstLoadingFlag") String notFirstLoadingFlag,
			@QueryParam("searchValue") String searchValue,
			@QueryParam("state_new") String state_new,
			@QueryParam("state_end") String state_end,
            @QueryParam("length") String length,
            @QueryParam("pageNum") String pageNum,
            @HeaderParam("sessionId") String sessionId){
    	SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	// 初始化
        	List<Object> list = new ArrayList<Object>();
        	Page<CollectionconfirmErrorVO> page = new Page<CollectionconfirmErrorVO>();
        	CollectionconfirmErrorDO bean = new CollectionconfirmErrorDO();
        	// 设置page属性
        	page.setPageSize(Integer.parseInt(length));// 每页条数
        	page.setPageNo(Integer.parseInt(pageNum));// 当前页
        	// 设置bean属性（查询条件）
        	bean.setCode(code);
        	bean.setEnterprise_payment_name(enterprise_payment_name);
        	bean.setSearchValue(searchValue);
        	if ("notFirstLoading".equals(notFirstLoadingFlag)) {
        		bean.setState_new(state_new);
        		bean.setState_end(state_end);
			}else{
				bean.setState_new(Constant.COLLECTIONCONFIRMERROR_STATE_DEAL);
			}
			bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        	if (time_stamp != null && false == "".equals(time_stamp)) {
        		bean.setTime_stamp(DateUnit.string2date(time_stamp, "yyyy-MM-dd"));
        	}
        	if (beginDateTS != null && false == "".equals(beginDateTS)) {
        		bean.setBeginDateTS(DateUnit.string2date(beginDateTS, "yyyy-MM-dd"));
        	}
        	if (endDateTS != null && false == "".equals(endDateTS)) {
        		//bean.setEndDateTS(DateUnit.string2date(endDateTS, "yyyy-MM-dd"));
            	Calendar dayEnd = Calendar.getInstance();
    			dayEnd.setTime(DateUnit.string2date(endDateTS.replace("/", "-"), DateUnit.YYYY_MM_DD));
    			dayEnd.set(Calendar.HOUR, 23);
    			dayEnd.set(Calendar.MINUTE, 59);
    			dayEnd.set(Calendar.SECOND, 59);
    			bean.setEndDateTS(dayEnd.getTime());
        	}
        	if (busi_date != null && false == "".equals(busi_date)) {
        		bean.setBusi_date(DateUnit.string2date(busi_date, "yyyy-MM-dd"));
        	}
        	if (beginDateBD != null && false == "".equals(beginDateBD)) {
        		bean.setBeginDateBD(DateUnit.string2date(beginDateBD, "yyyy-MM-dd"));
        	}
        	if (endDateBD != null && false == "".equals(endDateBD)) {
        		//bean.setEndDateBD(DateUnit.string2date(endDateBD, "yyyy-MM-dd"));
            	Calendar dayEnd = Calendar.getInstance();
    			dayEnd.setTime(DateUnit.string2date(endDateBD.replace("/", "-"), DateUnit.YYYY_MM_DD));
    			dayEnd.set(Calendar.HOUR, 23);
    			dayEnd.set(Calendar.MINUTE, 59);
    			dayEnd.set(Calendar.SECOND, 59);
    			bean.setEndDateBD(dayEnd.getTime());
        	}
        	list.add(collConfirmErrConsumer.selectByPage(bean, page));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
    }
    /**
     * 根据收款确认单号获取数据
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
	        	CollectionconfirmErrorDO collectionconfirmErrorDO = new CollectionconfirmErrorDO();
	        	collectionconfirmErrorDO.setCode(code);
	        	collectionconfirmErrorDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
	        	list.add(collConfirmErrConsumer.getByPrimaryKey(collectionconfirmErrorDO));
	            msg.setData(list);
	        } else {
	            msg.setMsg("查询失败");
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
	}
   /***
    * 收款确认异常处理定时任务
    */
    @GET
    @Path("/getSmfCollConfirmBypk")
    @Produces("application/json")
	@Override
	public ResultMsg getSmfCollConfirmBypk() {
    	
		try {
			return collConfirmErrConsumer.getSmfCollConfirmBypk();
		} catch (MsgException e) {
			return new ResultMsg("0",e.getMessage());
		}catch (Exception e) {
			return new ResultMsg("0","失败");
		}
	}

}
