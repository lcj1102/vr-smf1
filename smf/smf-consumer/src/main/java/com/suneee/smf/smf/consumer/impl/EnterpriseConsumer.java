package com.suneee.smf.smf.consumer.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.scn.scf.api.provider.EnterpriseRestProvider;
import com.suneee.scn.scf.model.EnterpriseVO;
import com.suneee.smf.smf.common.Constant;


@Service("enterpriseConsumer")
public class EnterpriseConsumer {

	//测试环境url
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.EnterpriseRestProvider")   
	private EnterpriseRestProvider enterpriseRestProvider;

    @Transactional(readOnly = true)
    public List<EnterpriseVO> getEnterpriseByName(String keyword,Long enterpriseid) {
		return enterpriseRestProvider.selectByName(keyword, enterpriseid,Constant.ENTERPRISE_OPERATION);
	}
    
    @Transactional(readOnly = true)
    public EnterpriseVO getEnterpriseByCode(String code,Long enterpriseid) {
		return enterpriseRestProvider.selectByCode(code, enterpriseid);
	}

}
