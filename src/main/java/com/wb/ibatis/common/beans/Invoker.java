package com.wb.ibatis.common.beans;

import java.lang.reflect.InvocationTargetException;

/**
 * @author www
 * @date 2016年1月31日
 * 
 */

public interface Invoker {

	public String getName();
	
	public Object invode(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;
}
