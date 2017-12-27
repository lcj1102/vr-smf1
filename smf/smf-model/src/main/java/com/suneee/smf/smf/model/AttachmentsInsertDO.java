package com.suneee.smf.smf.model;

import java.util.List;

public class AttachmentsInsertDO {
	List<AttachmentsDO> file;
	
	List<AttachmentsDO> delFile;

	public List<AttachmentsDO> getFile() {
		return file;
	}

	public void setFile(List<AttachmentsDO> file) {
		this.file = file;
	}

	public List<AttachmentsDO> getDelFile() {
		return delFile;
	}

	public void setDelFile(List<AttachmentsDO> delFile) {
		this.delFile = delFile;
	}

}
