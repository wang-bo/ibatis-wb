package com.wb.ibatis.common.beans;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

/**
 * @author www
 * @date 2016年2月13日
 * 
 * 
 * 
 */

public class GenericProbe extends BaseProbe {
	
	private static final BaseProbe BEAN_PROBE = new ComplexBeanProbe();
	private static final BaseProbe DOM_PROBE = new DomProbe();
	
	protected GenericProbe() {
		
	}

	/**
	 * 获取object对象中指定属性的值
	 */
	@Override
	public Object getObject(Object object, String name) {
		if (object instanceof Document) {
			return DOM_PROBE.getObject(object, name);
		} else if (object instanceof List) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else if (object instanceof Object[]) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else if (object instanceof char[]) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else if (object instanceof boolean[]) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else if (object instanceof byte[]) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else if (object instanceof double[]) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else if (object instanceof float[]) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else if (object instanceof int[]) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else if (object instanceof long[]) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else if (object instanceof short[]) {
			return BEAN_PROBE.getIndexedProperty(object, name);
		} else {
			return BEAN_PROBE.getObject(object, name);
		}
	}

	/**
	 * 设置object对象中指定属性的值
	 */
	@Override
	public void setObject(Object object, String name, Object value) {
		if (object instanceof Document) {
			DOM_PROBE.setObject(object, name, value);
		} else {
			BEAN_PROBE.setObject(object, name, value);
		}
	}

	/**
	 * 获得object对象中指定属性的getter方法的返回值类型
	 */
	@Override
	public Class<?> getPropertyTypeForGetter(Object object, String name) {
		if (object instanceof Class) {
			return getClassPropertyTypeForGetter((Class<?>) object, name);
		} else if (object instanceof Document) {
			return DOM_PROBE.getPropertyTypeForGetter(object, name);
		} else if (name.indexOf('[') > -1) {
			return BEAN_PROBE.getIndexedType(object, name);
		} else {
			return BEAN_PROBE.getPropertyTypeForGetter(object, name);
		}
	}

	/**
	 * 获得object对象中指定属性的setter方法的参数类型
	 */
	@Override
	public Class<?> getPropertyTypeForSetter(Object object, String name) {
		if (object instanceof Class) {
			return getClassPropertyTypeForSetter((Class<?>) object, name);
		} else if (object instanceof Document) {
			return DOM_PROBE.getPropertyTypeForSetter(object, name);
		} else {
			return BEAN_PROBE.getPropertyTypeForSetter(object, name);
		}
	}

	/**
	 * 判断object对象中指定属性是否可写
	 */
	@Override
	public boolean hasWritableProperty(Object object, String name) {
		if (object instanceof Document) {
			return DOM_PROBE.hasWritableProperty(object, name);
		} else {
			return BEAN_PROBE.hasWritableProperty(object, name);
		}
	}

	/**
	 * 判断object对象中指定属性是否可读
	 */
	@Override
	public boolean hasReadableProperty(Object object, String name) {
		if (object instanceof Document) {
			return DOM_PROBE.hasReadableProperty(object, name);
		} else {
			return BEAN_PROBE.hasReadableProperty(object, name);
		}
	}

	/**
	 * 获取object对象中可读的属性名称数组
	 */
	@Override
	public String[] getReadablePropertyNames(Object object) {
		if (object instanceof Document) {
			return DOM_PROBE.getReadablePropertyNames(object);
		} else {
			return BEAN_PROBE.getReadablePropertyNames(object);
		}
	}

	/**
	 * 获取object对象中可写的属性名称数组
	 */
	@Override
	public String[] getWriteablePropertyNames(Object object) {
		if (object instanceof Document) {
			return DOM_PROBE.getWriteablePropertyNames(object);
		} else {
			return BEAN_PROBE.getWriteablePropertyNames(object);
		}
	}

	/**
	 * 获取object对象中指定属性的值
	 */
	@Override
	protected Object getProperty(Object object, String property) {
		if (object instanceof Document) {
			return DOM_PROBE.getProperty(object, property);
		} else {
			return BEAN_PROBE.getProperty(object, property);
		}
	}

	/**
	 * 设置object对象中指定属性的值
	 */
	@Override
	protected void setProperty(Object object, String property, Object value) {
		if (object instanceof Document) {
			DOM_PROBE.setProperty(object, property, value);
		} else {
			BEAN_PROBE.setProperty(object, property, value);
		}
	}
	
	/**
	 * 从Class对象中获取指定属性的getter方法的返回值类型
	 */
	private Class<?> getClassPropertyTypeForGetter(Class<?> type, String name) {
		if (name.indexOf('.') > -1) {
			String[] nameArray = name.split("\\.");
			for (String str: nameArray) {
				if (Map.class.isAssignableFrom(type)) {
					type = Object.class;
					break;
				}
				type = ClassInfo.getInstance(type).getGetterType(str);
			}
		} else {
			type = ClassInfo.getInstance(type).getGetterType(name);
		}
		return type;
	}
	
	/**
	 * 从Class对象中获取指定属性的setter方法的参数类型，
	 */
	private Class<?> getClassPropertyTypeForSetter(Class<?> type, String name) {
		if (name.indexOf('.') > -1) {
			String[] nameArray = name.split("\\.");
			for (String str: nameArray) {
				if (Map.class.isAssignableFrom(type)) {
					type = Object.class;
					break;
				}
				type = ClassInfo.getInstance(type).getSetterType(str);
			}
		} else {
			type = ClassInfo.getInstance(type).getSetterType(name);
		}
		return type;
	}

}
