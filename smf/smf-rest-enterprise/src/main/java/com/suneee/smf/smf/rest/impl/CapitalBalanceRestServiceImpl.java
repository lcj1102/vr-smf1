package com.suneee.smf.smf.rest.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.suneee.smf.smf.api.rest.CapitalBalanceRestService;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.CapitalBalanceConsumer;
import com.suneee.smf.smf.model.CapitalBalanceDO;
import com.suneee.smf.smf.model.CapitalBalanceVO;
/**
 * @Description: 资金结余历史表
 * @author: 致远
 * @date: 2017年12月11日 下午3:15:09
 */
@Service//指明dubbo服务
@Path("capitalBalance")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class CapitalBalanceRestServiceImpl implements CapitalBalanceRestService {

	@Autowired
	private CapitalBalanceConsumer capitalBalanceConsumer;
	
	 /**
     * 分页查询
     */
    @GET
    @Path("/capiBalanceList")//为空则其完整路径就是类路径
    @Produces("application/json")
    @Override
    public ResultMsg capiBalanceList(@QueryParam("beginDate") String beginDate,
			@QueryParam("endDate") String endDate,
			@QueryParam("type") String type,
            @QueryParam("length") String length,
            @QueryParam("pageNum") String pageNum,
            @HeaderParam("sessionId") String sessionId){
    	SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	// 初始化
        	Page<CapitalBalanceVO> page = new Page<CapitalBalanceVO>();
        	List<Object> list = new ArrayList<Object>();
        	CapitalBalanceDO bean = new CapitalBalanceDO();
        	if (type == null || "".equals(type)) {
        		type = "HOUR";
        	}
        	// 设置page属性
        	if (length == null || "".equals(length)) {
        		page.setPageSize(15);
        	} else {
        		page.setPageSize(Integer.parseInt(length));// 每页条数
        	}
        	if (pageNum == null || "".equals(pageNum)) {
        		page.setPageNo(0);// 当前页
        	} else {
        		page.setPageNo(Integer.parseInt(pageNum));// 当前页
        	}
        	// 设置bean属性（查询条件）
			bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			bean.setType(type);
        	if (beginDate != null && false == "".equals(beginDate)) {
        		bean.setBeginDate(DateUnit.string2date(beginDate.replace("/", "-"), "yyyy-MM-dd"));
        	}
        	if (endDate != null && false == "".equals(endDate)) {
        		//bean.setEndDate(DateUnit.string2date(endDate.replace("/", "-"), "yyyy-MM-dd"));
            /*	Calendar dayEnd = Calendar.getInstance();
    			dayEnd.setTime(DateUnit.string2date(endDate.replace("/", "-"), DateUnit.YYYY_MM_DD));
    			dayEnd.set(Calendar.HOUR, 23);
    			dayEnd.set(Calendar.MINUTE, 59);
    			dayEnd.set(Calendar.SECOND, 59);
    			bean.setEndDate(dayEnd.getTime());*/
        		Date datebd =DateUnit.string2date(endDate.replace("/", "-"), "yyyy-MM-dd");
       		 	bean.setEndDate(DateUnit.icDateByDay(datebd, 1));
        	}
        	list.add(capitalBalanceConsumer.capiBalanceList(bean, page));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
    }
	
	 /**
     * 查询所有
     */
    @GET
    @Path("/balanceList")//为空则其完整路径就是类路径
    @Produces("application/json")
    @Override
    public ResultMsg balanceList(@QueryParam("beginDateStr") String beginDateStr,
			@QueryParam("endDateStr") String endDateStr,
			@QueryParam("type") String type,
			@HeaderParam("sessionId") String sessionId){
    	SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	// 初始化
        	List<Object> list = new ArrayList<Object>();
        	Page<CapitalBalanceVO> page = new Page<CapitalBalanceVO>();
        	CapitalBalanceDO bean = new CapitalBalanceDO();
        	// 设置bean属性（查询条件）
        	if (beginDateStr != null && false == "".equals(beginDateStr)) {
        		bean.setBeginDate(DateUnit.string2date(beginDateStr, "yyyy/MM/dd"));
        	}
        	if (endDateStr != null && false == "".equals(endDateStr)) {
        		bean.setEndDate(DateUnit.icDateByDay(DateUnit.string2date(endDateStr, "yyyy/MM/dd"), 1));
        	}
        	if (type == null || "".equals(type)) {
        		type = "HOUR";
        	}
        	bean.setType(type);
			bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        	list.add(capitalBalanceConsumer.balanceList(bean, page));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
    }
	/**
	 * 资金状态查询（定时任务）
	 */
	@GET
	@Path("/refreshBalanceTask")
	@Produces("application/json")
	@Override
	public void refreshBalanceTask(){
		try {
			capitalBalanceConsumer.refreshBalanceTask();
		} catch (MsgException e) {
			e.printStackTrace();
		}
	}
}
