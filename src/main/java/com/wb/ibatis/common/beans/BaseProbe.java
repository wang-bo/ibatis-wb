package com.wb.ibatis.common.beans;

import java.util.List;

/**
 * @author www
 * @date 2016年2月3日
 * 
 * 抽象类，用来简化编写Probe接口的实现类
 * 
 */

public abstract class BaseProbe implements Probe {

	/**
	 * 获取object对象中可读的属性名称数组
	 */
	public abstract String[] getReadablePropertyNames(Object object);
	
	/**
	 * 获取object对象中可写的属性名称数组
	 */
	public abstract String[] getWriteablePropertyNames(Object object);
	
	/**
	 * 获取object对象中指定属性的值
	 */
	protected abstract Object getProperty(Object object, String property);

	/**
	 * 设置object对象中指定属性的值
	 */
	protected abstract void setProperty(Object object, String property, Object value);

	/**
	 * 获取object对象中指定属性对应的值列表(或数组)中指定序号的值类型
	 * @param object 被操作的对象
	 * @param indexedName 格式：name[i]，表示获取object中name属性对应的值列表(或数组)中序号为i的值类型,
	 * 						name部分不存在时，object对象就是列表或数组，获取object中序号为i的值类型。
	 * @return
	 */
	protected Class<?> getIndexedType(Object object, String indexedName) {
		Class<?> value = null;
		try {
			String name = indexedName.substring(0, indexedName.indexOf('['));
			int i = Integer.parseInt(indexedName.substring(indexedName.indexOf('[') + 1, indexedName.indexOf(']')));
			Object list = null;
			if ("".equals(name)) {
				list = object;
			} else {
				list = getProperty(object, name);
			}
			if (list instanceof List) {
				value = ((List<?>) list).get(i).getClass();
			} else if (list instanceof Object[]) {
				value = ((Object[]) list)[i].getClass();
			} else if (list instanceof char[]) {
				value = Character.class;
			} else if (list instanceof boolean[]) {
				value = Boolean.class;
			} else if (list instanceof byte[]) {
				value = Byte.class;
			} else if (list instanceof double[]) {
				value = Double.class;
			} else if (list instanceof float[]) {
				value = Float.class;
			} else if (list instanceof int[]) {
				value = Integer.class;
			} else if (list instanceof long[]) {
				value = Long.class;
			} else if (list instanceof short[]) {
				value = Short.class;
			} else {
				throw new ProbeException("The '" + name + "' property of the " + object.getClass().getName() + " class is not a List or Array.");
			}
		} catch (ProbeException e) {
			throw e;
		} catch (Exception e) {
			throw new ProbeException("Error getting ordinal list from JavaBean. Cause " + e, e);
		}
		return value;
	}
	
	/**
	 * 获取object对象中指定属性对应的值列表(或数组)中指定序号的值
	 * @param object 被操作的对象
	 * @param indexedName 格式：name[i]，表示获取object中name属性对应的值列表(或数组)中序号为i的值,
	 * 						name部分不存在时，object对象就是列表或数组，获取object中序号为i的值。
	 * @return
	 */
	protected Object getIndexedProperty(Object object, String indexedName) {
		Object value = null;
		try {
			String name = indexedName.substring(0, indexedName.indexOf('['));
			int i = Integer.parseInt(indexedName.substring(indexedName.indexOf('[') + 1, indexedName.indexOf(']')));
			Object list = null;
			if ("".equals(name)) {
				list = object;
			} else {
				list = getProperty(object, name);
			}
			if (list instanceof List) {
				value = ((List<?>) list).get(i);
			} else if (list instanceof Object[]) {
				value = ((Object[]) list)[i];
			} else if (list instanceof char[]) {
				value = new Character(((char[]) list)[i]);
			} else if (list instanceof boolean[]) {
				value = new Boolean(((boolean[]) list)[i]);
			} else if (list instanceof byte[]) {
				value = new Byte(((byte[]) list)[i]);
			} else if (list instanceof double[]) {
				value = new Double(((double[]) list)[i]);
			} else if (list instanceof float[]) {
				value = new Float(((float[]) list)[i]);
			} else if (list instanceof int[]) {
				value = new Integer(((int[]) list)[i]);
			} else if (list instanceof long[]) {
				value = new Long(((long[]) list)[i]);
			} else if (list instanceof short[]) {
				value = new Short(((short[]) list)[i]);
			} else {
				throw new ProbeException("The '" + name + "' property of the " + object.getClass().getName() + " class is not a List or Array.");
			}
		} catch (ProbeException e) {
			throw e;
		} catch (Exception e) {
			throw new ProbeException("Error getting ordinal list from JavaBean. Cause " + e, e);
		}
		return value;
	}
	
	/**
	 * 设置object对象中指定属性对应的值列表(或数组)中指定序号的值
	 * @param object
	 * @param indexedName 格式：name[i]，表示object中name属性对应的值列表(或数组)中序号为i的对象,
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	protected void setIndexedProperty(Object object, String indexedName, Object value) {
		try {
			String name = indexedName.substring(0, indexedName.indexOf('['));
			int i = Integer.parseInt(indexedName.substring(indexedName.indexOf('[') + 1, indexedName.indexOf(']')));
			Object list = getProperty(object, name);
			if (list instanceof List) {
				((List<Object>) list).set(i, value);
			} else if (list instanceof Object[]) {
				((Object[]) list)[i] = value;
			} else if (list instanceof char[]) {
				((char[]) list)[i] = ((Character) value).charValue();
			} else if (list instanceof boolean[]) {
				((boolean[]) list)[i] = ((Boolean) value).booleanValue();
			} else if (list instanceof byte[]) {
				((byte[]) list)[i] = ((Byte) value).byteValue();
			} else if (list instanceof double[]) {
				((double[]) list)[i] = ((Double) value).doubleValue();
			} else if (list instanceof float[]) {
				((float[]) list)[i] = ((Float) value).floatValue();
			} else if (list instanceof int[]) {
				((int[]) list)[i] = ((Integer) value).intValue();
			} else if (list instanceof long[]) {
				((long[]) list)[i] = ((Long) value).longValue();
			} else if (list instanceof short[]) {
				((short[]) list)[i] = ((Short) value).shortValue();
			} else {
				throw new ProbeException("The '" + name + "' property of the " + object.getClass().getName() + " class is not a List or Array.");
			}
		} catch (ProbeException e) {
			throw e;
		} catch (Exception e) {
			throw new ProbeException("Error getting ordinal list from JavaBean. Cause " + e, e);
		}
	}
}
