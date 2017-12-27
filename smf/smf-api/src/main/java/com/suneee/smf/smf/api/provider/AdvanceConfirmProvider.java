package com.suneee.smf.smf.api.provider;


import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AdvanceConfirmDO;

public interface AdvanceConfirmProvider {



	 public ResultMsg add(AdvanceConfirmDO insertDO,String pushType);


	 
}
