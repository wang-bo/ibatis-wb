package com.wb.ibatis.common.beans;

import java.util.Map;

import com.wb.ibatis.sqlmap.engine.mapping.result.ResultObjectFactoryUtil;

/**
 * @author www
 * @date 2016年2月5日
 * 
 * 处理Bean对象或其他标准对象（如Map、List）的属性和方法
 * 
 */

public class ComplexBeanProbe extends BaseProbe {
	
	private static final Object[] NO_ARGUMENTS = new Object[0];
	
	protected ComplexBeanProbe() {
		
	}
	
	/**
	 * 获取object对象中可读的属性名称数组
	 */
	@Override
	public String[] getReadablePropertyNames(Object object) {
		return ClassInfo.getInstance(object.getClass()).getReadablePropertyNames();
	}

	/**
	 * 获取object对象中可写的属性名称数组
	 */
	@Override
	public String[] getWriteablePropertyNames(Object object) {
		return ClassInfo.getInstance(object.getClass()).getWriteablePropertyNames();
	}
	
	/**
	 * 获得object对象中指定属性的getter方法的返回值类型
	 * name可能是嵌套的结构，格式：name1.name2(可以多层，即可以有多个'.')，表示object中name1属性对应的getter方法的返回值类型中name2属性对应的getter方法的返回值类型
	 */
	@Override
	public Class<?> getPropertyTypeForGetter(Object object, String name) {
		Class<?> type = object.getClass();
		if (object instanceof Class) {
			type = getClassPropertyTypeForGetter((Class<?>) object, name);
		} else if (object instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) object;
			Object value = map.get(name);
			if (value == null) {
				type = Object.class;
			} else {
				type = value.getClass();
			}
		} else {
			if (name.indexOf('.') > -1) {
				String[] nameArray = name.split("\\.");
				for (String str: nameArray) {
					type = ClassInfo.getInstance(type).getGetterType(str);
				}
			} else {
				type = ClassInfo.getInstance(type).getGetterType(name);
			}
		}
		return type;
	}

	/**
	 * 获得object对象中指定属性的setter方法的参数类型，
	 * name可能是嵌套的结构，格式：name1.name2(可以多层，即可以有多个'.')，表示object中name1属性对应的setter方法的参数类型中name2属性对应的setter方法的参数类型
	 */
	@Override
	public Class<?> getPropertyTypeForSetter(Object object, String name) {
		Class<?> type = object.getClass();
		if (object instanceof Class) {
			type = getClassPropertyTypeForSetter((Class<?>) object, name);
		} else if (object instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) object;
			Object value = map.get(name);
			if (value == null) {
				type = Object.class;
			} else {
				type = value.getClass();
			}
		} else {
			if (name.indexOf('.') > -1) {
				String[] nameArray = name.split("\\.");
				for (String str: nameArray) {
					type = ClassInfo.getInstance(type).getSetterType(str);
				}
			} else {
				type = ClassInfo.getInstance(type).getSetterType(name);
			}
		}
		return type;
	}
	
	/**
	 * 从Class对象中获取指定属性的getter方法的返回值类型，
	 * name可能是嵌套的结构，格式：name1.name2(可以多层，即可以有多个'.')，表示type类型中name1属性对应的getter方法的返回值类型中name2属性对应的getter方法的返回值类型
	 */
	private Class<?> getClassPropertyTypeForGetter(Class<?> type, String name) {
		if (name.indexOf('.') > -1) {
			String[] nameArray = name.split("\\.");
			for (String str: nameArray) {
				type = ClassInfo.getInstance(type).getGetterType(str);
			}
		} else {
			type = ClassInfo.getInstance(type).getGetterType(name);
		}
		return type;
	}
	
	/**
	 * 从Class对象中获取指定属性的setter方法的参数类型，
	 * name可能是嵌套的结构，格式：name1.name2(可以多层，即可以有多个'.')，表示type类型中name1属性对应的setter方法的参数类型中name2属性对应的setter方法的参数类型
	 */
	private Class<?> getClassPropertyTypeForSetter(Class<?> type, String name) {
		if (name.indexOf('.') > -1) {
			String[] nameArray = name.split("\\.");
			for (String str: nameArray) {
				type = ClassInfo.getInstance(type).getSetterType(str);
			}
		} else {
			type = ClassInfo.getInstance(type).getSetterType(name);
		}
		return type;
	}
	
	/**
	 * 判断object对象中指定属性是否可写
	 * name可能是嵌套的结构，格式：name1.name2(可以多层，即可以有多个'.')
	 */
	@Override
	public boolean hasWritableProperty(Object object, String name) {
		boolean hasProperty = false;
		if (object instanceof Map) {
			hasProperty = true; // ((Map<?, ?>) object).containsKey(name);
		} else {
			if (name.indexOf('.') > -1) {
				Class<?> type = object.getClass();
				String[] nameArray = name.split("\\.");
				for (String str: nameArray) {
					// TODO 这段代码感觉逻辑有问题，待定
					type = ClassInfo.getInstance(type).getGetterType(str);
					hasProperty = ClassInfo.getInstance(type).hasWritableProperty(str);
				}
			} else {
				hasProperty = ClassInfo.getInstance(object.getClass()).hasWritableProperty(name);
			}
		}
		return hasProperty;
	}

	/**
	 * 判断object对象中指定属性是否可读
	 * name可能是嵌套的结构，格式：name1.name2(可以多层，即可以有多个'.')
	 */
	@Override
	public boolean hasReadableProperty(Object object, String name) {
		boolean hasProperty = false;
		if (object instanceof Map) {
			hasProperty = true; // ((Map<?, ?> object).containsKey(name);
		} else {
			if (name.indexOf('.') > -1) {
				Class<?> type = object.getClass();
				String[] nameArray = name.split("\\.");
				for (String str: nameArray) {
					// TODO 这段代码感觉逻辑有问题，待定
					type = ClassInfo.getInstance(type).getGetterType(str);
					hasProperty = ClassInfo.getInstance(type).hasReadableProperty(str);
				}
			} else {
				hasProperty = ClassInfo.getInstance(object.getClass()).hasReadableProperty(name);
			}
		}
		return hasProperty;
	}

	/**
	 * 获取object对象中指定属性的值
	 * name可能是嵌套的结构，格式：name1.name2(可以多层，即可以有多个'.')，表示object中name1属性对应的值中name2属性对应的值
	 */
	@Override
	public Object getObject(Object object, String name) {
		if (name.indexOf('.') > -1) {
			Object value = object;
			String[] nameArray = name.split("\\.");
			for (String str: nameArray) {
				value = getProperty(value, str);
				if (value == null) {
					break;
				}
			}
			return value;
		} else {
			return getProperty(object, name);
		}
	}

	/**
	 * 设置object对象中指定属性的值
	 * name可能是嵌套的结构，格式：name1.name2(可以多层，即可以有多个'.')，表示设置object中name1属性对应的值中name2属性对应的值
	 */
	@Override
	public void setObject(Object object, String name, Object value) {
		if (name.indexOf('.') > -1) {
			Object child = object;
			String[] nameArray = name.split("\\.");
			// nameArray的最后一个元素是待操作的对象，所以不遍历
			for (int i = 0; i < nameArray.length - 1; i++) {
				String str = nameArray[i];
				Class<?> type = getPropertyTypeForSetter(child, str);
				Object parent = child;
				child = getProperty(parent, str);
				// 取不到属性，就设置这个属性值
				if (child == null) {
					if (value == null) {
						return; // value为null时不生成child
					} else {
						try {
							child = ResultObjectFactoryUtil.createObjectThroughFactory(type);
							setObject(parent, str, child);
						} catch (Exception e) {
							throw new ProbeException("Cannot set value of property '" + name + "' because '" + name + 
									"' is null and cannot be instantiated on instance of " + type.getName() + ". Cause: " + e, e);
						}
					}
				}
			}
			setProperty(child, nameArray[nameArray.length - 1], value);
		} else {
			setProperty(object, name, value);
		}
	}
	
	/**
	 * 获取object对象中指定属性的值
	 * property格式为name[i]，表示获取object中name属性对应的值列表(或数组)中序号为i的值,
	 * property格式为name1.name2(可以多层，即可以有多个'.')，表示object中name1属性对应的值中name2属性对应的值
	 */
	@Override
	protected Object getProperty(Object object, String property) {
		try {
			Object value = null;
			if (property.indexOf('[') > -1) {
				value = getIndexedProperty(object, property);
			} else {
				if (object instanceof Map) {
					int index = property.indexOf('.');
					if (index > -1) {
						String mapKey = property.substring(0, index);
						value = getProperty(((Map<?, ?>) object).get(mapKey), property.substring(index + 1));
					} else {
						value = ((Map<?, ?>) object).get(property);
					}
				} else {
					int index = property.indexOf('.');
					if (index > -1) {
						String newProperty = property.substring(0, index);
						value = getProperty(getProperty(object, newProperty), property.substring(index + 1));
					} else {
						ClassInfo classInfo = ClassInfo.getInstance(object.getClass());
						Invoker method = classInfo.getGetInvoker(property);
						if (method == null) {
							throw new NoSuchMethodException("No GET method for property " + property + " on instance of " + object.getClass().getName());
						}
						try {
							value = method.invode(object, NO_ARGUMENTS);
						} catch (Throwable	t) {
							throw ClassInfo.unwrapThrowable(t);
						}
					}
				}
			}
			return value;
		} catch (ProbeException e) {
			throw e;
		} catch (Throwable t) {
			if (object == null) {
				throw new ProbeException("Could not get property '" + property + "' from null reference. Cause: " + t, t);
			} else {
				throw new ProbeException("Could not get property '" + property + "' from " + object.getClass().getName() + ". Cause: " + t, t);
			}
		}
	}

	/**
	 * 设置object对象中指定属性的值
	 * property格式为name[i]，表示object中name属性对应的值列表(或数组)中序号为i的对象,
	 * property格式为name1.name2(可以多层，即可以有多个'.')，表示object中name1属性对应的值中name2属性对应的值
	 */
	@Override
	protected void setProperty(Object object, String property, Object value) {
		try {
			if (property.indexOf('[') > -1) {
				setIndexedProperty(object, property, value);
			} else {
				if (object instanceof Map) {
					((Map) object).put(property, value);
				} else {
					ClassInfo classInfo = ClassInfo.getInstance(object.getClass());
					Invoker method = classInfo.getSetInvoker(property);
					if (method == null) {
						throw new NoSuchMethodException("No SET method for property " + property + " on instance of " + object.getClass().getName());
					}
					try {
						method.invode(object, new Object[] {value});
					} catch (Throwable t) {
						throw ClassInfo.unwrapThrowable(t);
					}
				}
			}
		} catch (ProbeException e) {
			throw e;
		} catch (Throwable t) {
			if (object == null) {
				throw new ProbeException("Could not set property '" + property + "' to value '" + value + "' for null reference. Cause: " + t, t);
			} else {
				throw new ProbeException("Could not set property '" + property + "' to value '" + value + "' for " + object.getClass().getName() + ". Cause: " + t, t);
			}
		}
	}

}
