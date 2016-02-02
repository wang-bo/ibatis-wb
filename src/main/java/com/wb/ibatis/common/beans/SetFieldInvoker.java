package com.wb.ibatis.common.beans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author www
 * @date 2016年2月2日
 * 
 * 给字段设置用的调用器
 * 
 */

public class SetFieldInvoker implements Invoker {
	
	private Field field;
	private String name;
	
	public SetFieldInvoker(Field field) {
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
		field.set(target, args[0]);
		return null;
	}

}
