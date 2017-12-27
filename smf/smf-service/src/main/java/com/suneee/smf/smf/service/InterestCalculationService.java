package com.suneee.smf.smf.service;

import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.dao.InterestCalculationDao;
import com.suneee.smf.smf.model.InterestCalculationDO;
import com.suneee.smf.smf.model.InterestCalculationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 
 * @Description: 企业计息Service
 * @author: 张礼佳
 * @date: 2017年12月8日 上午11:17:48
 */
@Service("interestCalculationService")
public class InterestCalculationService {

	@Autowired
	private InterestCalculationDao interestCalculationDao;

	public Page<InterestCalculationVO> listByPage(
			Page<InterestCalculationVO> page, InterestCalculationDO bean) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enterprise_application_name",
				bean.getEnterprise_application_name());
		map.put("searchValue", bean.getSearchValue());
		map.put("beginDate", bean.getStartDate());
		map.put("endDate", bean.getEndDate());
		map.put("state", bean.getState());
		map.put("enterpriseid", bean.getEnterpriseid());

		// if (bean.getEndDate() != null && !"".equals(bean.getEndDate())) {
		// Calendar c = Calendar.getInstance();
		// c.setTime(bean.getEndDate());
		// c.add(Calendar.DAY_OF_MONTH, 1);
		// Date endTime = c.getTime();
		// map.put("endDate", endTime);
		// }

		int startNum = (page.getPageNo() - 1) * (page.getPageSize());
		map.put("startNum", startNum);
		map.put("pageSize", page.getPageSize());

		List<InterestCalculationVO> calculationVOs = interestCalculationDao
				.selectByPage(map);
		page.setResults(calculationVOs);

		int count = (int) interestCalculationDao.queryCount(map);
		page.setTotalCount(count);
		page.setPageCount(count / page.getPageSize()
				+ (count % page.getPageSize() == 0 ? 0 : 1));

		return page;

	}

	public void add() {
		Date date = DateUnit.date2date(new Date(), "yyyy-MM-dd ");
		interestCalculationDao.add(date);
	}
}
