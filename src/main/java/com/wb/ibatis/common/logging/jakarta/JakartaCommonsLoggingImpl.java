package com.wb.ibatis.common.logging.jakarta;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author www
 * @date 2016年2月17日
 * 
 * Jakarta Commons Logging 日志模式(iBATIS默认的日志模式)
 * 
 * JCL的Logging组件实现方式是将日志的功能封装为一组标准的API，其底层实现可以任意修改和变换。
 * 当调用LogFactory.getLog()方法时，会找到必需的底层日志记录功能的实现，过程如下：
 * 	1：首选在CLASSPATH中查找commons-logging.properties文件，如果这个文件中
 * 	        定义了org.apache.commons.logging.Log属性，则使用该属性对应的日志组件，结束发现过程。
 * 	2：接着检查系统属性 org.apache.commons.logging.Log，如果找到则使用该属性对应的日志组件，结束发现过程。
 * 	3：接着在CLASSPATH中寻找log4j的类，如果找到，就假定应用要使用的是log4j，log4j本身的属性仍要通过log4j.properties文件配置，结束发现过程。
 * 	4：上述3步都找不到使得的日志组件，如果应用程序运行在JRE1.4或更高版本，则默认使用JRE1.4的日志记录功能，结束发现过程。
 * 	5：上述4步都找不到使得的日志组件，则使用内建的SimpleLog，把所有日志信息直接输出到System.err，结束发现过程。
 */

public class JakartaCommonsLoggingImpl implements com.wb.ibatis.common.logging.Log {

	private Log log;
	
	public JakartaCommonsLoggingImpl(Class<?> clazz) {
		log = LogFactory.getLog(clazz);
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
