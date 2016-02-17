package com.wb.ibatis.common.logging.jdk14;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.wb.ibatis.common.logging.Log;

/**
 * @author www
 * @date 2016年2月17日
 * 
 * JDK1.4 Logging API 日志模式
 * 
 */

public class Jdk14LoggingImpl implements Log {

	private Logger log;
	
	public Jdk14LoggingImpl(Class<?> clazz) {
		log = Logger.getLogger(clazz.getName());
	}
	
	@Override
	public boolean isDebugEnabled() {
		return log.isLoggable(Level.FINE);
	}

	@Override
	public void error(String s, Throwable t) {
		log.log(Level.SEVERE, s, t);
	}

	@Override
	public void error(String s) {
		log.log(Level.SEVERE, s);
	}

	@Override
	public void debug(String s) {
		log.log(Level.FINE, s);
	}

	@Override
	public void warn(String s) {
		log.log(Level.WARNING, s);
	}

}
