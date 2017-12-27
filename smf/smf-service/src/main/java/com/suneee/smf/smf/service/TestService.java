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
package com.suneee.smf.smf.service;

import com.suneee.smf.smf.model.TestBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("testService")
public class TestService {


	public TestBean getByPK(TestBean bean){
		System.out.println("本地服务被调用:"+bean);
		return bean;
	}
}