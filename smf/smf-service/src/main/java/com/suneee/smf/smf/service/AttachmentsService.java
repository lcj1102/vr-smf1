package com.suneee.smf.smf.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.dao.AttachmentsDao;
import com.suneee.smf.smf.model.AttachmentsDO;
import com.suneee.smf.smf.model.AttachmentsInsertDO;
import com.suneee.smf.smf.model.AttachmentsVO;

/**
 * @Description: 附件
 * @author: 致远
 * @date: 2017年12月1日 下午4:05:10
 */
@Service("attachmentsService")
public class AttachmentsService {

	@Autowired
	private AttachmentsDao attachmentsDao;

	/**
	 * @Title: getFileList 
	 * @author:致远
	 * @Description: 查询附件列表
	 * @param sourcebillid  关联类型主键
	 * @param sourcebilltype   关联类型
	 * @param userInfo  当前登录人
	 * @return
	 * @return: ResultMsg
	 */
	public List<AttachmentsVO> getFileList(Long sourcebillid, String sourcebilltype,SystemUserInfoT userInfo) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("sourcebillid", sourcebillid);
		map.put("sourcebilltype", sourcebilltype);
		map.put("enterpriseid", userInfo.getEnterpriseid());
		map.put("state", Constant.ATTACHMENTS_STATE_NEW);
		return attachmentsDao.getFileList(map);
	}
	/**
	 * @Title: addFile 
	 * @author:致远
	 * @Description: 新增附件
	 * @param fileDO   
	 * @param userInfo
	 * @return
	 * @return: int 1 成功  0 失败
	 * @throws MsgException 
	 */
	public int addFile(AttachmentsDO fileDO, SystemUserInfoT userInfo) throws MsgException {
		fileDO.setInputmanid(userInfo.getUserId().longValue());
		fileDO.setInputmanname(userInfo.getUserName());
		fileDO.setBookindate(new Date());
		fileDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		fileDO.setState(Constant.ATTACHMENTS_STATE_NEW);
		
		if (fileDO.getSize() != null && false == "".equals(fileDO.getSize())) {
			if (Double.valueOf(fileDO.getSize())/(1024*1024) > 1) {
				double size = Double.valueOf(fileDO.getSize())/(1024*1024.0);
				BigDecimal bg = new BigDecimal(size);
				fileDO.setSize(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"M");
			} else {
				Long size = (long) (Double.valueOf(fileDO.getSize())/1024);
				fileDO.setSize(size+"K");
			}
		}
		
		int m = attachmentsDao.addFile(fileDO);
		if (m != 1) {
			throw new MsgException("0", "保存失败");
		}
		return 1;
	}
	/**
	 * @Title: deleteFile 
	 * @author:致远
	 * @Description: 删除附件
	 * @param pk_attachments
	 * @param userInfo
	 * @return
	 * @return: int 1 成功  0 失败
	 * @throws MsgException 
	 */
	public int deleteFile(AttachmentsDO fileDO, SystemUserInfoT userInfo) throws MsgException {
		fileDO.setCanceldate(new Date());
		fileDO.setCancelid(userInfo.getUserId().longValue());
		fileDO.setCancelname(userInfo.getUserName());
		fileDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		fileDO.setState(Constant.ATTACHMENTS_STATE_DEL);
		int m = attachmentsDao.deleteFile(fileDO);
		if (m != 1) {
			throw new MsgException("0", "删除失败");
		}
		return 1;
	}
	public int addFile(AttachmentsInsertDO file, SystemUserInfoT userInfo) throws MsgException {
		for (AttachmentsDO do1 : file.getFile()) {
			if (do1.getPk_attachments() != null) {
				do1.setModefydate(new Date());
				do1.setModiferid(userInfo.getUserId().longValue());
				do1.setModifername(userInfo.getName());
				do1.setEnterpriseid(userInfo.getEnterpriseid().longValue());
				if (do1.getSize() != null && false == "".equals(do1.getSize()) && do1.getSize().indexOf("K") == -1 && do1.getSize().indexOf("M") == -1) {
					if (Double.valueOf(do1.getSize())/(1024*1024) > 1) {
						double size = Double.valueOf(do1.getSize())/(1024*1024.0);
						BigDecimal bg = new BigDecimal(size);
						do1.setSize(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"M");
					} else {
						Long size = (long) (Double.valueOf(do1.getSize())/1024);
						do1.setSize(size+"K");
					}
				}
				int m = attachmentsDao.updateFile(do1);
				if (m != 1) {
					throw new MsgException("0", "保存失败");
				}
			} else {
				do1.setSourcebilltype(Constant.CAPITALAPPLICATION_FILE_CODE);
				do1.setInputmanid(userInfo.getUserId().longValue());
				do1.setInputmanname(userInfo.getUserName());
				do1.setBookindate(new Date());
				do1.setEnterpriseid(userInfo.getEnterpriseid().longValue());
				do1.setState(Constant.ATTACHMENTS_STATE_NEW);
				
				if (do1.getSize() != null && false == "".equals(do1.getSize())) {
					if (Double.valueOf(do1.getSize())/(1024*1024) > 1) {
						double size = Double.valueOf(do1.getSize())/(1024*1024.0);
						BigDecimal bg = new BigDecimal(size);
						do1.setSize(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"M");
					} else {
						Long size = (long) (Double.valueOf(do1.getSize())/1024);
						do1.setSize(size+"K");
					}
				}
				int m = attachmentsDao.addFile(do1);
				if (m != 1) {
					throw new MsgException("0", "保存失败");
				}
			}
		}
		//删除 
		for (AttachmentsDO do1 : file.getDelFile()) {
			do1.setCanceldate(new Date());
			do1.setCancelid(userInfo.getUserId().longValue());
			do1.setCancelname(userInfo.getUserName());
			do1.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			do1.setState(Constant.ATTACHMENTS_STATE_DEL);
			int m = attachmentsDao.deleteFile(do1);
			if (m != 1) {
				throw new MsgException("0", "删除失败");
			}
		}
		return 1;
	}
}
