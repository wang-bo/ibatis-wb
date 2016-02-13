package com.wb.ibatis.common.beans;

import org.w3c.dom.Document;

/**
 * @author www
 * @date 2016年2月13日
 * 
 * 获得Probe接口实例的工厂类
 * 
 */

public class ProbeFactory {

	private static final Probe DOM = new DomProbe();
	private static final Probe BEAN = new ComplexBeanProbe();
	private static final Probe GENERIC = new GenericProbe();
	
	/**
	 * 获得Probe接口实例的工厂方法，默认返回GenericProbe。
	 * @return
	 */
	public static Probe getProbe() {
		return GENERIC;
	}
	
	/**
	 * 获得Probe接口实例的工厂方法，根据参数类型返回合适的实例。
	 */
	public static Probe getProbe(Object object) {
		if (object instanceof Document) {
			return DOM;
		} else {
			return BEAN;
		}
	}
}
