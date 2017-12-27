package com.suneee.smf.smf.rest.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
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
import com.suneee.scn.scf.model.EnterpriseVO;
import com.suneee.smf.smf.api.rest.EnterpriseRestService;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.EnterpriseConsumer;


@Service//指明dubbo服务
@Path("enterprise")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class EnterpriseRestServiceImpl implements EnterpriseRestService {

    @Autowired
    private EnterpriseConsumer enterpriseConsumer;


    @GET
    @Path("/getEnterpriseByName")
    @Produces("application/json")
    @Override
    public ResultMsg getEnterpriseByName(@QueryParam("keyword") String keyword,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	try {
				keyword = URLDecoder.decode(keyword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	List<Object> list = new ArrayList<Object>();
            List<EnterpriseVO> relist = enterpriseConsumer.getEnterpriseByName(keyword, userInfo.getEnterpriseid().longValue());
            list.addAll(relist);
            msg.setData(list);
            msg.setCode("1");
            msg.setMsg("查询成功");
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }


    @GET
    @Path("/getEnterpriseByCode")
    @Produces("application/json")
    @Override
	public ResultMsg getEnterpriseByCode(@QueryParam("code")String code,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	List<Object> list = new ArrayList<Object>();
            EnterpriseVO enterpriseVO = enterpriseConsumer.getEnterpriseByCode(code, userInfo.getEnterpriseid().longValue());
            list.add(enterpriseVO);
            msg.setData(list);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
	}


}
