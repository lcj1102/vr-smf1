package com.suneee.smf.smf.provider.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.suneee.scn.config.PropertiesFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by Administrator on 2017/5/27.
 */

@Configuration//标明spring配置类
@ComponentScan({"com.suneee.smf.smf"})//配置扫描包，需要包含spring配置类所在包，可以使用@ComponentScan({"com.suneee.scn.sys.provider.impl","com.suneee.scn.sys.config"})
public class DubboProviderConfig {
    public static final String APPLICATION_NAME;//提供者名称
    public static final String APPLICATION_OWNER;//提供者所有者
    public static final String REGISTRY_ADDRESS;//注册dubbo服务的地址
    public static final String DUBBO_SERVER;//dubbo使用的协议的服务器端实现类型
    public static final String PROVIDER_FILTER;//拦截器
    public static final int   PROVIDER_TIMEOUT;//超时时间
    public static final String PROTOCOL_SERIALIZATION;//dubbo协议序列化方式
    public static final int   PROTOCOL_PORT;//提供者的接口
    public static final String PROTOCOL_NAME;//dubbo协议名称
    public static final String ANNOTATION_PACKAGE = "com.suneee.smf.smf.provider.impl";//提供者实现类的包路径，实现类必须添加dubbo的service注解

    static {
        Properties properties = PropertiesFactory.createProperties("smf/smf","providerDubbo");
        APPLICATION_NAME = properties.getProperty("dubbo.applicationName");
        APPLICATION_OWNER = properties.getProperty("dubbo.applicationOwner");
        REGISTRY_ADDRESS = properties.getProperty("dubbo.registryAddress");
        DUBBO_SERVER = properties.getProperty("dubbo.dubboServer");
        PROVIDER_FILTER = properties.getProperty("dubbo.providerFilter");
        PROVIDER_TIMEOUT = Integer.parseInt(properties.getProperty("dubbo.providerTimeout"));
        PROTOCOL_SERIALIZATION = properties.getProperty("dubbo.protocolSerialization");
        PROTOCOL_PORT = Integer.parseInt(properties.getProperty("dubbo.protocolPort"));
        PROTOCOL_NAME = properties.getProperty("dubbo.protocolName");
    }

    @Bean
    public ApplicationConfig providerApplicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(APPLICATION_NAME);
        applicationConfig.setOwner(APPLICATION_OWNER);
        return applicationConfig;
    }
    @Bean
    public RegistryConfig providerRegistryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(REGISTRY_ADDRESS);
        registryConfig.setServer(DUBBO_SERVER);
        return registryConfig;
    }
    @Bean
    public AnnotationBean providerAnnotationBean() {
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(ANNOTATION_PACKAGE);
        return annotationBean;
    }

    @Bean
    public ConsumerConfig providerConsumerConfig() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setFilter(PROVIDER_FILTER);
        consumerConfig.setTimeout(PROVIDER_TIMEOUT);
        consumerConfig.setCheck(false);
        return consumerConfig;
    }
    @Bean
    public ProtocolConfig providerProtocolConfig() {
        ProtocolConfig ProtocolConfig = new ProtocolConfig();
        ProtocolConfig.setSerialization(PROTOCOL_SERIALIZATION);
        ProtocolConfig.setPort(PROTOCOL_PORT);
        ProtocolConfig.setName(PROTOCOL_NAME);
        ProtocolConfig.setServer(DUBBO_SERVER);
        return ProtocolConfig;
    }

}
