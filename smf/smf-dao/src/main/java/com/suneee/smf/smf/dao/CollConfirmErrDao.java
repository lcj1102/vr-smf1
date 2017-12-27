package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.suneee.smf.smf.model.CollectionConfirmDO;
import com.suneee.smf.smf.model.CollectionConfirmVO;
import com.suneee.smf.smf.model.CollectionconfirmErrorDO;
import com.suneee.smf.smf.model.CollectionconfirmErrorVO;

/**
 * 
 * @Description: TODO dao
 * @author: yunhe
 * @date: 2017年12月8日 14:55:10
 */
public interface CollConfirmErrDao {

	/**
	 * 
	 * @Title: selectByPage 
	 * @Description: TODO 分页查询方法
	 * @param map
	 * @return
	 * @return: CollConfirmErrDao
	 */
    List<CollectionconfirmErrorVO> selectByPage(Map<String, Object> map);

    /**
     * 
     * @Title: countByPage 
     * @Description: TODO 统计符合条件的总数
     * @param map
     * @return
     * @return: Long
     */
    Long countByPage(Map<String, Object> map);
    
    CollectionconfirmErrorVO getByPrimaryKey(CollectionconfirmErrorDO bean);
    
    int insert(CollectionconfirmErrorDO errorDO);
    //查出所有的收款确认异常数据
    List<CollectionconfirmErrorVO> getAllPriamryKey();
    
    //根据收款确认异常主键 查出收款确认
    
	void update(CollectionconfirmErrorVO errorVO);

	void deleteErrorDOList(CollectionConfirmVO collectionConfirmVO);
	
	void updateErrorState(CollectionconfirmErrorVO errorVO);
	
	CollectionconfirmErrorVO selCollErrorByEnter();
	
	List<String> selDisEnterAndApp();
	
	CollectionConfirmDO getAllCollByPk(@Param("pk_collection_confirm")Long  pk_collection_confirm);
	
}