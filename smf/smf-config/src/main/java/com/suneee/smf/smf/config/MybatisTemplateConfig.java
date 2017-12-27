package com.suneee.smf.smf.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@MapperScan("com.suneee.smf.smf.dao")
public class MybatisTemplateConfig {
	
	@Bean
	@Autowired
	public SqlSessionTemplate  sqlSessionTemplate(SqlSessionFactory sqlSessionFactory){
		SqlSessionTemplate sql=new org.mybatis.spring.SqlSessionTemplate(sqlSessionFactory);
		return sql;	
	}

}
