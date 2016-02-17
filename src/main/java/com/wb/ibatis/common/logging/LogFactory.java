package com.wb.ibatis.common.logging;

import java.lang.reflect.Constructor;

import com.wb.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年2月17日
 * 
 * Log工厂类，生产Log接口的实例
 * 
 */

public class LogFactory {

	private static Constructor<?> logConstructor;
	
	static {
		// 找到日志的实现类，LogFactory启动时会加载Jakarta Commons Logging，后面3个其实都不会被执行
		tryImplementation("org.apache.commons.logging.LogFactory", "com.wb.ibatis.common.logging.jakarta.JakartaCommonsLoggingImpl");
	    tryImplementation("org.apache.log4j.Logger", "com.wb.ibatis.common.logging.log4j.Log4jImpl");
	    tryImplementation("java.util.logging.Logger", "com.wb.ibatis.common.logging.jdk14.Jdk14LoggingImpl");
	    tryImplementation("java.lang.Object", "com.wb.ibatis.common.logging.nologging.NoLoggingImpl");
	}
	
	/**
	 * 初始化logConstructor
	 */
	private static void tryImplementation(String testClassName, String implClassName) {
		if (logConstructor == null) {
			try {
				Resources.classForName(testClassName);
				Class<?> implClass = Resources.classForName(implClassName);
				logConstructor = implClass.getConstructor(new Class[] {Class.class});
			} catch (Throwable t) {
				// do nothing
			}
		}
	}
	
	/**
	 * 返回一个Log接口的实例
	 */
	public static Log getLog(Class<?> clazz) {
		try {
			return (Log) logConstructor.newInstance(new Object[] {clazz});
		} catch (Throwable t) {
			throw new RuntimeException("Error creating logger for class " + clazz + ". Cause: " + t, t);
		}
	}
	
	/**
	 * 切换日志实现为Log4J，Log4J的包需存在classpath中，
	 * 注意：必须在获得Log实例前调用这个方法才有效。
	 */
	public static synchronized void selectLog4JLogging() {
		try {
			Resources.classForName("org.apache.log4j.Logger");
			Class<?> implClass = Resources.classForName("com.wb.ibatis.common.logging.log4j.Log4jImpl");
			logConstructor = implClass.getConstructor(new Class[] {Class.class});
		} catch (Throwable t) {
			// do nothing
		}
	}
	
	/**
	 * 切换日志实现为JDK1.4 Logging API，必须在JRE 1.4及以上版本中运行，
	 * 注意：必须在获得Log实例前调用这个方法才有效。
	 */
	public static synchronized void selectJavaLogging() {
		try {
			Resources.classForName("java.util.logging.Logger");
			Class<?> implClass = Resources.classForName("com.wb.ibatis.common.logging.jdk14.Jdk14LoggingImpl");
			logConstructor = implClass.getConstructor(new Class[] {Class.class});
		} catch (Throwable t) {
			// do nothing
		}
	}
}
