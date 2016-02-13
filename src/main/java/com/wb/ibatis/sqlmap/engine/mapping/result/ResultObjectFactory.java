package com.wb.ibatis.sqlmap.engine.mapping.result;

/**
 * @author www
 * @date 2016年2月11日
 * 
 * iBATIS执行完sql语句后，使用这个接口的实例来创建result objects。
 * 使用时在配置文件SqlMapConfig的"resultObjectFactory"元素中配置接口的实现类，实现类需要一个public的无参构造方法。
 * 
 * iBATIS通过ResultObjectFactoryUtil类来使用这个接口的实现类。
 * 
 */

public interface ResultObjectFactory {

	/**
	 * 返回指定类型的实例
	 * @param statementId
	 * @param clazz
	 * @return
	 */
	Object createInstance(String statementId, Class<?> clazz) throws InstantiationException, IllegalAccessException;
	
	/**
	 * 设置SqlMapConfig中配置的"resultObjectFactory"的属性
	 */
	void setProperty(String name, String value);
}
