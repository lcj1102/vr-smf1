 /**********************************************************************
 * Copyright © 2015 SunEee.                                         *
 * Limited. All rights reserved.               						*
 * The software is developed by suneee fei lun wei. Using,       	*
 * reproducing,modification and distribution are prohibited         *
 * without SunEee's permission.                                     *
 ********************************************************************
 * @Title: SpringJdbcInit.java 
 * @Prject: flcn
 * @Package: com.suneee.db 
 * @Description: TODO
 * @author: rongxin   
 * @date: 2015年10月9日 下午5:36:21 
 * @version: V1.0   
 */
package com.suneee.smf.smf.config.db;

 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.jdbc.core.JdbcTemplate;

 import javax.sql.DataSource;

 /**
  * @ClassName: SpringJdbcInit
  * @Description: TODO
  * @author: rongxin
  * @date: 2015年10月9日 下午5:36:21
  */
 @Configuration
 public class SpringJdbcInit {
     @Autowired
     private DataSource dataSource;
     
     /**
      * 尽量不要使用,尽量用mybatis解决，此处仅供调试
      * @return JdbcTemplate
      */
     @Bean
     @Deprecated
     public JdbcTemplate jdbcTemplate() {
         JdbcTemplate jdbc = new JdbcTemplate();
         jdbc.setDataSource(dataSource);
         return jdbc;
     }

 }
