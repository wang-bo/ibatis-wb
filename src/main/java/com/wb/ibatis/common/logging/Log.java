package com.wb.ibatis.common.logging;

/**
 * @author www
 * @date 2016年2月17日
 * 
 * Log接口
 * 
 */

public interface Log {

	public boolean isDebugEnabled();
	
	public void error(String s, Throwable t);
	
	public void error(String s);
	
	public void debug(String s);
	
	public void warn(String s);
}
