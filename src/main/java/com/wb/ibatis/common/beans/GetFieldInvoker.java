package com.wb.ibatis.common.beans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author www
 * @date 2016年2月2日
 * 
 * 获取字段值用的调用器
 * 
 */

public class GetFieldInvoker implements Invoker {

	private Field field;
	private String name;
	
	public GetFieldInvoker(Field field) {
		this.field = field;
		this.name = "(" + field.getName() + ")";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object invode(Object target, Object[] args)
			throws IllegalAccessException, InvocationTargetException {
		return field.get(target);
	}

}
