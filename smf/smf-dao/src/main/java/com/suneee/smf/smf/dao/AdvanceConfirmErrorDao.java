package com.suneee.smf.smf.dao;

import com.suneee.smf.smf.model.AdvanceConfirmDO;
import com.suneee.smf.smf.model.AdvanceConfirmErrorDO;
import com.suneee.smf.smf.model.AdvanceConfirmErrorVO;
import com.suneee.smf.smf.model.AdvanceConfirmVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * @Description: TODO dao
 * @author: changzhaoyu
 * @date: 2017年12月19日 12:55:10
 */
public interface AdvanceConfirmErrorDao {

	/**
	 * 
	 * @Title: selectByPage 
	 * @Description: TODO 分页查询方法
	 * @param map
	 * @return
	 * @return: CollConfirmErrDao
	 */
	
   List<AdvanceConfirmErrorVO> selectByPage(Map<String, Object> map);
   /**
    * 
    * @Title: countByPage 
    * @Description: TODO 统计符合条件的总数
    * @param map
    * @return
    * @return: Long
    */
   Long countByPage(Map<String, Object> map);
   
   AdvanceConfirmErrorVO getByPrimaryKey(AdvanceConfirmErrorDO bean);
   
   //查出所有的放款确认异常数据
   List<AdvanceConfirmErrorVO> getAllPriamryKey();
   
   //修改相关的放款异常记录为已处理
   void updateErrorDOState(AdvanceConfirmVO advanceConfirmVO);
   
   //更新放款异常记录
   void update(AdvanceConfirmErrorVO errorVO);

	int insert(AdvanceConfirmErrorDO errorDO);

    void deleteErrorDO(AdvanceConfirmVO advanceConfirmVO);

    AdvanceConfirmDO getAllAdvanceByPk(@Param("pk_advance_confirm")Long  pk_advance_confirm);
}
