package com.wb.ibatis.common.logging.log4j;

import org.apache.log4j.Logger;

import com.wb.ibatis.common.logging.Log;

/**
 * @author www
 * @date 2016年2月17日
 * 
 * Log4J日志模式
 * 
 */

public class Log4jImpl implements Log {
	
	private Logger log;
	
	public Log4jImpl(Class<?> clazz) {
		log = Logger.getLogger(clazz);
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public void error(String s, Throwable t) {
		log.error(s, t);
	}

	@Override
	public void error(String s) {
		log.error(s);
	}

	@Override
	public void debug(String s) {
		log.debug(s);
	}

	@Override
	public void warn(String s) {
		log.warn(s);
	}

}
