package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import com.suneee.smf.smf.model.CapitalInjectionDO;
import com.suneee.smf.smf.model.CapitalInjectionVO;


public interface CapitalInjectionDao {

    //根据数据条件查询列表
	List<CapitalInjectionVO> selectByExample(Map<String,Object> map);

	//根据条件查询记录条数
	long countByExample(Map<String, Object> map);
	
	//新增
	int insert(CapitalInjectionDO capitalInjectionDO);
	
	//删除
	int delete(CapitalInjectionDO bean);
	
	//根据id查询任务信息
	CapitalInjectionVO getById(CapitalInjectionDO capitalInjectionDO);

	//编辑
	int update(CapitalInjectionDO capitalInjectionDO);
	
	//修改状态
	int updateState(CapitalInjectionDO bean);
	
	//判断编号是否重复
	long countByCode(Map<String, Object> map);
	
	 //时间戳验证
    long checkTimeStamp(CapitalInjectionDO bean);
    
    //资金退出结算接口
     List<CapitalInjectionVO> selectByName(Map<String,Object> map);
     
     CapitalInjectionVO selectByCode (CapitalInjectionVO capitalInjectionVO);
}
