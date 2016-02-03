package com.wb.ibatis.common.beans;

/**
 * @author www
 * @date 2016年2月3日
 * 
 * 处理Bean、DOM对象和其他对象的转化。
 * 对ClassInfo类处理的方法都是通过Probe接口操作的，
 * 可以说是ClassInfo类的解释器。
 * 
 */

public interface Probe {

	/**
	 * 获取object对象中指定属性的值
	 */
	public Object getObject(Object object, String name);
	
	/**
	 * 设置object对象中指定属性的值
	 */
	public void setObject(Object object, String name, Object value);
	
	/**
	 * 获得object对象中指定属性的getter方法的返回值类型
	 */
	public Class<?> getPropertyTypeForGetter(Object object, String name);
	
	/**
	 * 获得object对象中指定属性的setter方法的参数类型
	 */
	public Class<?> getPropertyTypeForSetter(Object object, String name);
	
	/**
	 * 判断object对象中指定属性是否可写
	 */
	public boolean hasWritableProperty(Object object, String name);
	
	/**
	 * 判断object对象中指定属性是否可读
	 */
	public boolean hasReadableProperty(Object object, String name);
}
