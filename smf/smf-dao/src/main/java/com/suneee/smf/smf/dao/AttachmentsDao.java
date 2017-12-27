package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import com.suneee.smf.smf.model.AttachmentsDO;
import com.suneee.smf.smf.model.AttachmentsVO;

/**
 * @Description: 附件
 * @author: 致远
 * @date: 2017年12月5日 上午9:51:42
 */
public interface AttachmentsDao {

	List<AttachmentsVO> getFileList(Map<String, Object> map);

	int addFile(AttachmentsDO fileDO);

	int deleteFile(AttachmentsDO fileDO);

	int updateFile(AttachmentsDO do1);

}
