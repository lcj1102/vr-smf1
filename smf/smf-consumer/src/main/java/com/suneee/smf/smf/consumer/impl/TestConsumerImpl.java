package com.suneee.smf.smf.consumer.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.smf.smf.api.provider.TestProvider;
import com.suneee.smf.smf.consumer.service.TestConsumer;
import com.suneee.smf.smf.model.TestBean;
import com.suneee.smf.smf.service.TestService;

/**
 * Created by Administrator on 2017/6/6.
 */
@Service("testComsumer")
public class TestConsumerImpl implements TestConsumer, BeanFactoryAware {

	@Reference
	TestProvider testProvider;

	@Autowired
	TestService testService;

	private BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public String getByPKByProvider(Long id) {
		System.out.println("消费者调用provider接口");

		String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				(ListableBeanFactory) beanFactory, Object.class);
		
		for (String name : names) {
			System.err.println(name);
		}

		TestBean testBean = testProvider.getByPK(new TestBean(id));
		return testBean.getMenuId().toString();
	}

	@Override
	public String getByPKByService(Long id) {
		System.out.println("消费者调用本地service服务接口");
		TestBean testBean = testService.getByPK(new TestBean(id));
		return testBean.getMenuId().toString();
	}
}
