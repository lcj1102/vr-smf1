package com.suneee.smf.smf.api.rest;


import com.suneee.smf.smf.model.TestBean;

public interface TestRestService {

    public String getHelloWorld(Long beanId);

    public int deleteName(int i);

    public long getLong(long l);
	
	public String getLocalName();

    public TestBean getUser(TestBean bean);

    public String getHelloWorldGet(Long beanId);
}
