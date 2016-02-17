package com.wb.ibatis.common.logging;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author www
 * @date 2016年2月17日
 */

public class LogTest {

	private static Log log;
	
	@BeforeClass
	public static void initClass() {
		//LogFactory.selectLog4JLogging();
		//LogFactory.selectJavaLogging();
		log = LogFactory.getLog(LogTest.class);
	}
	
	@Test
	public void testLog() {
		System.out.println(log.getClass());
		log.error("error");
		log.debug("debug");
		log.warn("warn");
	}
}
