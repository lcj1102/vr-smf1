package com.suneee.smf.smf.rest.impl;

import java.util.ArrayList;
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
import com.alibaba.fastjson.JSON;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.component.cache.CacheUtils;
import com.suneee.smf.smf.api.rest.EnterpriseSettlementRestService;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.EnterpriseSettlementComsumer;
import com.suneee.smf.smf.model.EnterpriseSettlementVO;

/**
 * 
 * @Description: 分账结算
 * @author: 张礼佳
 * @date: 2017年12月15日 下午4:25:01
 */
@Service
@Path("/enterpriseSettlement")
public class EnterpriseSettlementRestServiceImpl implements
		EnterpriseSettlementRestService {

	private Logger logger = LoggerFactory
			.getLogger(EnterpriseSettlementRestService.class);

	@Autowired
	private EnterpriseSettlementComsumer enterpriseSettlementComsumer;

	@GET
	@Path("/list")
	@Produces("application/json")
	@Override
	public ResultMsg enterpriseSettlementList(
			@QueryParam("searchValue") String searchValue,
			@QueryParam("name") String name,
			@QueryParam("enterprise_name") String enterprise_name,
			@QueryParam("beginDate") String beginDate,
			@QueryParam("endDate") String endDate,
			@QueryParam("minAmount") String minAmount,
			@QueryParam("maxAmount") String maxAmount,
			@QueryParam("length") String length,
			@QueryParam("pageNum") String pageNum,
			@QueryParam("draw") String draw,
			@HeaderParam("sessionId") String sessionId) {

		logger.debug(
				"查询分账结算列表... 入参: searchValue[{}],name[{}],enterprise_name[{}],beginDate[{}],endDate[{}],length[{}],pageNum[{}],draw[{}],sessionId[{}]",
				searchValue, name, enterprise_name, beginDate, endDate, length,
				pageNum, draw, sessionId);

		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class,
				sessionId);

		ResultMsg result = new ResultMsg();

		if (userInfo == null || userInfo.getEnterpriseid() == null) {
			logger.debug("用户登录失效.... userInfo[{}]", userInfo);
			result.setMsg("用户登陆过期，请重新登陆！");
			return result;
		}
		
		// 设置分页参数
		Page<EnterpriseSettlementVO> page = new Page<EnterpriseSettlementVO>();
		page.setPageNo(Integer.parseInt(pageNum));// 设置分页查询当前页
		page.setPageSize(Integer.parseInt(length));// 设置分页查询每页条数
		
//		if(1 == Integer.parseInt(draw)){
//			logger.info("界面打开..不加载数据");
//			page.setTotalCount(0);
//			page.setPageCount(0);
//			page.setResults(null);
//			List<Object> data = new ArrayList<Object>();
//			data.add(page);
//			result.setData(data);
//			logger.debug("返回数据. [{}]", JSON.toJSONString(result));
//			return result;
//		}

		// 设置查询参数
		EnterpriseSettlementVO bean = new EnterpriseSettlementVO();
		bean.setName(name);
		bean.setEnterpriseid(Long.valueOf(userInfo.getEnterpriseid()));
		bean.setEnterprise_name(enterprise_name);
		if(minAmount != null && !"".equals(minAmount)){
			bean.setMinAmount(Double.valueOf(minAmount));
		}
		if(maxAmount !=null && !"".equals(maxAmount)){
			bean.setMaxAmount(Double.valueOf(maxAmount));
		}
		if (beginDate != null && !"".equals(beginDate)) {
			bean.setStartDate(DateUnit.string2date(beginDate, "yyyy-MM-dd"));
		}
		if (endDate != null && !"".equals(endDate)) {
			bean.setEndDate(DateUnit.string2date(endDate, "yyyy-MM-dd"));
		}
		bean.setSearchValue(searchValue);

		List<Object> data = new ArrayList<Object>();
		data.add(enterpriseSettlementComsumer.listByPage(page, bean));
		result.setData(data);
		logger.debug("返回数据. [{}]", JSON.toJSONString(result));
		return result;
	}

	@GET
	@Path("/get")
	@Produces("application/json")
	@Override
	public ResultMsg getEnterpriseSettlement(@QueryParam("id") String id,
			@HeaderParam("sessionId") String sessionId) {
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class,
				sessionId);

		ResultMsg result = new ResultMsg();

		if (userInfo == null || userInfo.getEnterpriseid() == null) {
			logger.debug("用户登录失效.... userInfo[{}]", userInfo);
			result.setMsg("用户登陆过期，请重新登陆！");
			return result;
		}
		EnterpriseSettlementVO settlementVO = enterpriseSettlementComsumer
				.queryById(id, Long.valueOf(userInfo.getEnterpriseid()));

		if (settlementVO == null) {
			result.setMsg("数据不存在，请刷新界面！");
			return result;
		}

		List<Object> datas = new ArrayList<Object>(1);
		datas.add(settlementVO);
		result.setData(datas);

		return result;
	}

}
