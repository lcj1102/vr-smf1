package com.suneee.smf.smf.rest.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.smf.smf.api.rest.TestRestService;
import com.suneee.smf.smf.consumer.service.TestConsumer;
import com.suneee.smf.smf.model.TestBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;


@Service//指明dubbo服务
@Path("test")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class TestRestServiceImpl implements TestRestService {

    @Autowired
    private TestConsumer testComsumer;

   /* @Reference
    TestProvider testProvider;*/

    @POST
    @Path("")//为空则其完整路径就是类路径
    @Consumes({"application/x-www-form-urlencoded"})//getHelloWorld()接收x-www-form-urlencoded格式的数据
    /**
     * 返回基础数据类型无需使用@Produces注解
     * 接受基础数据类型无需使用@Consumes注解
     * */
    public String getHelloWorld(@FormParam("id") Long beanId){
        return testComsumer.getByPKByService(beanId);
    }

    @GET
    @Path("")
    public String getLocalName(){
        return "local本地";
    }


    @DELETE
    @Path("")
    public int deleteName(@FormParam("age") int i) throws NotFoundException {
        System.out.println(i);
        if (i>2){
            throw new NotFoundException();
        }
        return i;
    }

    @PUT
    @Path("")
    public long getLong(@FormParam("age") long l) {
        System.out.println(l);
        return l;
    }

    /**
     * 对于bean的特别说明，由于jackson会根据方法名来进行序列化及反序列化，所以对于bean中的非属性get/set，其余方法避免使用
     * get/set开头，对于其他模块引用的对象，可以使用装饰模式进行包装
     * */
    @POST
    @Path("/bean")
    @Produces("application/json")
    @Consumes("application/json")//指定getUser()接收JSON格式的数据。REST框架会自动将JSON数据反序列化为User对象
    public TestBean getUser(TestBean bean) {
        System.out.println(bean);
        return bean;
    }

    @GET
    @Path("/hello/{id}")//相对路径为/hello/abc，则abc会作为name传入下方的参数中。
    public String getHelloWorldGet(@PathParam("id") Long beanId){
//        testProvider.getByPK(new TestBean(beanId));
        return testComsumer.getByPKByProvider(beanId);
    }

}
