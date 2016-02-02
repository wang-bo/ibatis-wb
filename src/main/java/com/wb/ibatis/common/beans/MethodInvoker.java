package com.wb.ibatis.common.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author www
 * @date 2016年1月31日
 * 
 * 方法调用器
 * 
 */

public class MethodInvoker implements Invoker {
	
	private Method method;
	private String name;
	
	public MethodInvoker(Method method) {
		this.method = method;
		this.name = method.getName();
	}
	
	public Method getMethod() {
		return method;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object invode(Object target, Object[] args)
			throws IllegalAccessException, InvocationTargetException {
		return method.invoke(target, args);
	}

}
