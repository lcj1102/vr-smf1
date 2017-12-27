/**
 * Created By: XI
 * Created On: 2016-11-6 1:09:39
 *
 * Amendment History:
 * 
 * Amended By       Amended On      Amendment Description
 * ------------     -----------     ---------------------------------------------
 *
 **/
package com.suneee.smf.smf.provider.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.smf.smf.api.provider.TestProvider;
import com.suneee.smf.smf.model.TestBean;
import com.suneee.smf.smf.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TestProviderImpl implements TestProvider {


	@Autowired
	private TestService testService;

	@Override
	public TestBean getByPK(TestBean bean) {
		System.out.println("提供者被调用");
		return testService.getByPK(bean);
	}
}
