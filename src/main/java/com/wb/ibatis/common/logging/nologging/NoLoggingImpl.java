package com.wb.ibatis.common.logging.nologging;

import com.wb.ibatis.common.logging.Log;

/**
 * @author www
 * @date 2016年2月17日
 * 
 * 无日志模式
 * 
 */

public class NoLoggingImpl implements Log {
	
	public NoLoggingImpl(Class<?> clazz) {
		
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void error(String s, Throwable t) {

	}

	@Override
	public void error(String s) {

	}

	@Override
	public void debug(String s) {

	}

	@Override
	public void warn(String s) {

	}

}
