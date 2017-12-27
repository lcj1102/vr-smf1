package com.suneee.smf.smf.config;

import com.suneee.scn.config.PropertiesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;


/**
 * @ClassName: SpringDSConfig
 * @Description: 只加载spring加载数据源
 * @author: rongxin
 * @date: 2015年9月30日 下午3:52:05
 */
@Configuration
@ComponentScan({ "com.suneee.smf.smf","com.suneee.scn.basic.workflow"})
public class SpringRootConfig{

	Properties properties = PropertiesFactory.createProperties("smf/smf","dbConfig");

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(properties.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(properties.getProperty("jdbc.url"));
		dataSource.setUsername(properties.getProperty("jdbc.username"));
		dataSource.setPassword(properties.getProperty("jdbc.password"));
		return dataSource;
	}

	@Bean
	@Autowired
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		DataSourceTransactionManager txManager = new org.springframework.jdbc.datasource.DataSourceTransactionManager();
		txManager.setDataSource(dataSource);
		return txManager;
	}
}
