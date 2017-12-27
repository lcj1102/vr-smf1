package com.suneee.smf.smf.rest.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.component.cache.CacheUtils;
import com.suneee.smf.smf.api.rest.InterestCalculationRestService;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.InterestCalculationConsumer;
import com.suneee.smf.smf.model.InterestCalculationDO;
import com.suneee.smf.smf.model.InterestCalculationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Description: 企业计息rest
 * @author: 张礼佳
 * @date: 2017年12月8日 上午9:42:25
 */
@Service
@Path("/interestCalculation")
public class InterestCalculationRestServiceImpl implements
		InterestCalculationRestService {

	private final Logger logger = LoggerFactory
			.getLogger(InterestCalculationRestService.class);

	@Autowired
	private InterestCalculationConsumer interestCalculationConsumer;

	@GET
	@Path("/list")
	@Produces("application/json")
	@Override
	public ResultMsg interestCalculationList(
			@QueryParam("searchValue") String searchValue,
			@QueryParam("enterprise_application_name") String enterprise_application_name,
			@QueryParam("state") String state,
			@QueryParam("beginDate") String beginDate,
			@QueryParam("endDate") String endDate,
			@QueryParam("length") String length,
			@QueryParam("pageNum") String pageNum, 
			@QueryParam("draw") String draw,
			@HeaderParam("sessionId") String sessionId) {

		logger.debug(
				"查询用户计息列表... 入参: searchValue[{}],enterprise_application_name[{}],state[{}],beginDate[{}],endDate[{}],length[{}],pageNum[{}],sessionId[{}]",
				searchValue, enterprise_application_name, state, beginDate,
				endDate, length, pageNum, sessionId);

		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class,
				sessionId);

		ResultMsg result = new ResultMsg();

		if (userInfo == null || userInfo.getEnterpriseid() == null) {
			logger.debug("用户登录失效.... userInfo[{}]", userInfo);
			result.setMsg("用户登陆过期，请重新登陆！");
			return result;
		}
		
		// 设置分页参数
		Page<InterestCalculationVO> page = new Page<InterestCalculationVO>();
		page.setPageNo(Integer.parseInt(pageNum));// 设置分页查询当前页
		page.setPageSize(Integer.parseInt(length));// 设置分页查询每页条数
		
		if(1 == Integer.parseInt(draw)){
			logger.info("界面打开..不加载数据");
			page.setTotalCount(0);
			page.setPageCount(0);
			page.setResults(null);
			List<Object> data = new ArrayList<Object>();
			data.add(page);
			result.setData(data);
			logger.debug("返回数据. [{}]", JSON.toJSONString(result));
			return result;
		}

		// 设置查询参数
		InterestCalculationDO bean = new InterestCalculationDO();
		bean.setEnterprise_application_name(enterprise_application_name);
		if (beginDate != null && !"".equals(beginDate)) {
			bean.setStartDate(DateUnit.string2date(beginDate, "yyyy-MM-dd"));
		}
		if (endDate != null && !"".equals(endDate)) {
			bean.setEndDate(DateUnit.string2date(endDate, "yyyy-MM-dd"));
		}
		bean.setSearchValue(searchValue);
		bean.setState(state);
		bean.setEnterpriseid(Long.valueOf(userInfo.getEnterpriseid()));

		List<Object> data = new ArrayList<Object>();
		data.add(interestCalculationConsumer.listByPage(page, bean));
		result.setData(data);
		logger.debug("返回数据. [{}]", JSON.toJSONString(result));
		return result;
	}

	/**
	 * @Description: ${todo} 企业计息定时
	 * @author: 超杰
	 * @date: ${date} ${time}
	 * ${tags}
	 */

	@GET
	@Path("/autoCalculateInterest")
	@Produces("application/json")
	@Override
	public void autoCalculateInterest()  {

		interestCalculationConsumer.add();

	}

}
