package com.wb.ibatis.common.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import com.wb.ibatis.common.beans.ProbeException;

/**
 * @author www
 * @date 2016年1月31日
 * 
 * JavaBean管理类
 * 一是充当JavaBean的容器池
 * 二是担任某个独立的JavaBean的对象实体容器(即一个ClassInfo实例代表一个JavaBean类的定义)
 * 
 */

public class ClassInfo {

	// 缓存开关，是否缓存ClassInfo实例，默认开启
	private static boolean cacheEnabled = true;
	// 空数组
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	// 存储一些Java的常用类
	private static final Set<Class<?>> SIMPLE_TYPE_SET = new HashSet<>();
	// 存储一些已经针对JavaBean类实例化的ClassInfo对象
	private static final Map<Class<?>, ClassInfo> CLASS_INFO_MAP = Collections.synchronizedMap(new HashMap<Class<?>, ClassInfo>());
	
	// JavaBean的类名
	private String className;
	// JaveBean中可读属性名称数组
	private String[] readablePropertyNames = EMPTY_STRING_ARRAY;
	// JavaBean中可写属性名称数组
	private String[] writeablePropertyNames = EMPTY_STRING_ARRAY;
	// JavaBean中的setter方法的Map
	private Map<String, Invoker> setMethods = new HashMap<>();
	// JavaBean中的getter方法的Map
	private Map<String, Invoker> getMethods = new HashMap<>();
	// JavaBean中setter方法的第一个参数类型的Map
	private Map<String, Class<?>> setTypes = new HashMap<>();
	// JavaBean中getter方法的返回值类型的Map
	private Map<String, Class<?>> getTypes = new HashMap<>();
	// JavaBean中的默认构造方法
	private Constructor<?> defaultConstructor;
	
	static {
		// 初始化Java常用数据类型Set
		SIMPLE_TYPE_SET.add(String.class);
		SIMPLE_TYPE_SET.add(Byte.class);
		SIMPLE_TYPE_SET.add(Short.class);
		SIMPLE_TYPE_SET.add(Integer.class);
		SIMPLE_TYPE_SET.add(Long.class);
		SIMPLE_TYPE_SET.add(Float.class);
		SIMPLE_TYPE_SET.add(Double.class);
		SIMPLE_TYPE_SET.add(Character.class);
		SIMPLE_TYPE_SET.add(Boolean.class);
		SIMPLE_TYPE_SET.add(Date.class);
		SIMPLE_TYPE_SET.add(Class.class);
		SIMPLE_TYPE_SET.add(BigInteger.class);
		SIMPLE_TYPE_SET.add(BigDecimal.class);
		
		SIMPLE_TYPE_SET.add(Collection.class);
		SIMPLE_TYPE_SET.add(List.class);
		SIMPLE_TYPE_SET.add(ArrayList.class);
		SIMPLE_TYPE_SET.add(LinkedList.class);
		SIMPLE_TYPE_SET.add(Set.class);
		SIMPLE_TYPE_SET.add(HashSet.class);
		SIMPLE_TYPE_SET.add(TreeSet.class);
		SIMPLE_TYPE_SET.add(Map.class);
		SIMPLE_TYPE_SET.add(HashMap.class);
		SIMPLE_TYPE_SET.add(TreeMap.class);
		SIMPLE_TYPE_SET.add(Vector.class);
		SIMPLE_TYPE_SET.add(Hashtable.class);
		SIMPLE_TYPE_SET.add(Enumeration.class);
	}
	
	/**
	 * 私有构造方法，只能在静态方法getInstance中实例化ClassInfo对象。
	 * @param clazz
	 */
	private ClassInfo(Class<?> clazz) {
		className = clazz.getName();
		addDefaultConstructor(clazz);
		addGetMethods(clazz);
		addSetMethods(clazz);
		addFields(clazz);
		readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
		writeablePropertyNames = setMethods.keySet().toArray(new String[setMethods.keySet().size()]);
	}
	
	/**
	 * 设置默认构造方法(这里选择无参构造方法)
	 * @param clazz
	 */
	private void addDefaultConstructor(Class<?> clazz) {
		// 返回定义在本类中的所有构造方法，包括public和private
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor: constructors) {
			// jdk1.8 增加了getParameterCount()方法
			if (constructor.getParameterTypes().length == 0) {
				if (canAccessPrivateMethods()) {
					try {
						constructor.setAccessible(true);
					} catch (SecurityException e) {
						// 不处理
					}
				}
				if (constructor.isAccessible()) {
					this.defaultConstructor = constructor;
				}
			}
		}
	}
	
	/**
	 * 设置所有getter方法，包括所有父类及接口中的所有public、protected、private方法
	 * @param clazz
	 */
	private void addGetMethods(Class<?> clazz) {
		Method[] methods = getClassMethods(clazz);
		for (Method method: methods) {
			String name = method.getName();
			if ((name.startsWith("get") && name.length() > 3)
					|| (name.startsWith("is") && name.length() > 2)) {
				if (method.getParameterTypes().length == 0) {
					name = dropCase(name); // 去掉前缀，并将剩余部分的首字母小写
					// getter不会有重载方法，不像setter，可能会有重载方法
					addGetMethod(name, method);
				}
			}
		}
	}
	
	/**
	 * 设置所有setter方法，包括所有父类及接口中的所有public、protected、private方法
	 * @param clazz
	 */
	private void addSetMethods(Class<?> clazz) {
		// 存放setter重载方法的Map
		Map<String, List<Method>> conflictingSetterMap =  new HashMap<>();
		Method[] methods = getClassMethods(clazz);
		for (Method method: methods) {
			String name = method.getName();
			if (name.startsWith("set") && name.length() > 3) {
				if (method.getParameterTypes().length == 1) {
					name = dropCase(name); // 去掉前缀，并将剩余部分的首字母小写
					// addSetMethod(name, method); 
					// setter可能会有重载方法，存入Map中待处理
					addSetterConflict(conflictingSetterMap, name, method);
				}
			}
		}
		// 处理setter的重载方法，将正确的setter方法找出，并放入setMethods和setTypes中
		resolveSetterConflicts(conflictingSetterMap);
	}
	

	
	/**
	 * 设置所有字段，包括所有父类中的所有public、protected、private字段
	 * @param clazz
	 */
	private void addFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field: fields) {
			if (canAccessPrivateMethods()) {
				try {
					field.setAccessible(true);
				} catch (SecurityException e) {
					// 不处理
				}
			}
			if (field.isAccessible()) {
				if (!getMethods.containsKey(field.getName())) {
					addGetField(field);
				}
				if (!setMethods.containsKey(field.getName())) {
					addSetField(field);
				}
			}
		}
		// 如果有父类，则递归调用，处理定义在父类中的字段
		if (clazz.getSuperclass() != null) {
			addFields(clazz.getSuperclass());
		}
	}
	
	/**
	 * 将getter方法放入getMethods和getTypes中
	 * @param name
	 * @param method
	 */
	private void addGetMethod(String name, Method method) {
		getMethods.put(name, new MethodInvoker(method));
		getTypes.put(name, method.getReturnType());
	}
	
	/**
	 * 将setter方法放入setMethods和setTypes中
	 * @param name
	 * @param method
	 */
	private void addSetMethod(String name, Method method) {
		setMethods.put(name, new MethodInvoker(method));
		setTypes.put(name, method.getParameterTypes()[0]);
	}

	/**
	 * 将field的相应getter操作放入getMethods和getTypes中
	 * @param field
	 */
	private void addGetField(Field field) {
		getMethods.put(field.getName(), new GetFieldInvoker(field));
		getTypes.put(field.getName(), field.getType());
	}
	
	private void addSetField(Field field) {
		setMethods.put(field.getName(), new SetFieldInvoker(field));
		setTypes.put(field.getName(), field.getType());
	}
	
	/**
	 * 将setter的重载方法存入Map中，待后续处理
	 * @param conflictingSetterMap
	 * @param name
	 * @param method
	 */
	private void addSetterConflict(Map<String, List<Method>> conflictingSetterMap, 
			String name, Method method) {
		List<Method> methodList = conflictingSetterMap.get(name);
		if (methodList == null) {
			methodList = new ArrayList<>();
			conflictingSetterMap.put(name, methodList);
		}
		methodList.add(method);
	}
	
	/**
	 * 处理setter的重载方法，根据相应的getter方法返回值类型来找到正确的setter方法，并调用addSetMethod()来放入setMethods和setTypes中
	 * @param conflictingSetterMap
	 */
	private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetterMap) {
		for (String name: conflictingSetterMap.keySet()) {
			List<Method> setterList = conflictingSetterMap.get(name);
			Method firstMethod = setterList.get(0);
			if (setterList.size() == 1) {
				addSetMethod(name, firstMethod);
			} else {
				Class<?> expectedType = getTypes.get(name);
				if (expectedType == null) {
					throw new RuntimeException("Illegal overloaded setter method with ambiguous type for property " +
							name + " in class " + firstMethod.getDeclaringClass() + ". This breaks the JavaBeans " +
							"specification and can cause unpredicatble results.");
				} else {
					Method setterMethod = null;
					for (Method method: setterList) {
						if (method.getParameterTypes().length == 1 &&
							expectedType.equals(method.getParameterTypes()[0])) {
							setterMethod = method;
							break;
						}
					}
					if (setterMethod == null) {
						throw new RuntimeException("Illegal overloaded setter method with ambiguous type for property " +
								name + " in class " + firstMethod.getDeclaringClass() + ". This breaks the JavaBeans " +
								"specification and can cause unpredicatble results.");
					}
					addSetMethod(name, setterMethod);
				}
			}
		}
	}
	
	/**
	 * 返回所有方法(包括父类的方法，private、protected、public)
	 * 因为Class.getMethods()方法只返回public的Method，所以这里做了扩展
	 * @param clazz
	 * @return
	 */
	private Method[] getClassMethods(Class<?> clazz) {
		Map<String, Method> uniqueMethods = new HashMap<>();
		Class<?> currentClass = clazz;
		while (currentClass != null) {
			// 定义在本类中的所有方法
			addUniqueMethod(uniqueMethods, currentClass.getDeclaredMethods());
			// 继续处理定义在接口中的方法，因为这个class有可能是抽象类
			Class<?>[] interfaces = currentClass.getInterfaces();
			for (Class<?> iface: interfaces) {
				addUniqueMethod(uniqueMethods, iface.getMethods());
			}
			// 继续遍历父类
			currentClass = currentClass.getSuperclass();
		}
		Collection<Method> methods = uniqueMethods.values();
		return methods.toArray(new Method[methods.size()]);
 	}
	
	/**
	 * 将数组中的所有method添加到Map中，需去重
	 * @param uniqueMethods
	 * @param methods
	 */
	private void addUniqueMethod(Map<String, Method> uniqueMethods, Method[] methods) {
		for (Method method: methods) {
			// 判断是否桥接方法，桥接方法是 JDK 1.5 引入泛型后，为了使Java的泛型方法生成的字节码和 1.5 版本前的字节码相兼容，由编译器自动生成的方法
			if (!method.isBridge()) {
				String signature = getSignature(method);
				// 判断这个方法是否已存在Map中，如果已存在，说明这个方法在子类中被覆盖过了，就无需再处理
				if (!uniqueMethods.containsKey(signature)) {
					if (canAccessPrivateMethods()) {
						try {
							method.setAccessible(true);
						} catch (SecurityException e) {
							// 不处理
						}
					}
					uniqueMethods.put(signature, method);
				}
			}
		}
	}
	
	/**
	 * 返回方法签名的字符串(方法名+方法参数名)
	 * @param method
	 * @return
	 */
	private String getSignature(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getName());
		Class<?>[] parameters = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			if (i == 0) {
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(parameters[i].getName());
		}
		return sb.toString();
	}
	
	/**
	 * 返回方法名所对应的属性名(反射时依据的属性名，和真实的属性名不一定对应)，如：
	 * getName --> name
	 * setAge --> age
	 * isVisitAble --> visitAble 
	 * 
	 * @param name
	 * @return
	 */
	private static String dropCase(String name) {
		if (name.startsWith("is")) {
			name = name.substring(2);
		} else if (name.startsWith("get") || name.startsWith("set")) {
			name = name.substring(3);
		} else {
			throw new ProbeException("Error parsing property name '" + name + "'. Didn't start with 'is', 'get' or 'set'.");
		}
		if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
			name = name.substring(0, 1).toLowerCase(Locale.US) + name.substring(1);
		}
		return name;
	}
	
	/**
	 * 检测安全管理器是否允许反射时访问类中的字段和调用方法(包括public、protected、private)
	 * @return
	 */
	private static boolean canAccessPrivateMethods() {
		try {
			SecurityManager securityManager = System.getSecurityManager();
			if (securityManager != null) {
				securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
			}
		} catch (SecurityException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 返回本实例所代表的JavaBean的类名
	 * @return
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * 实例化本实例所代表的JavaBean
	 * @return
	 */
	public Object instantiateClass() {
		if (defaultConstructor != null) {
			try {
				return defaultConstructor.newInstance(new Object[] {});
			} catch (Exception e) {
				throw new RuntimeException("Error instantiating class. Cause: " + e, e);
			}
		} else {
			throw new RuntimeException("Error instantiating class. There is no default constructor for class " + className);
		}
	}
	
	/**
	 * 返回属性对应的getter方法
	 * @param propertyName
	 * @return
	 */
	public Method getGetter(String propertyName) {
		Invoker invoker = getMethods.get(propertyName);
		if (invoker == null) {
			throw new ProbeException("There is no READABLE property named '" + propertyName + "' in class '" + className + "'");
		}
		if (!(invoker instanceof MethodInvoker)) {
			throw new ProbeException("Can't get getter method because '" + propertyName + "' is a field in class '" + className + "'");
		}
		return ((MethodInvoker) invoker).getMethod();
	}
	
	/**
	 * 返回属性对应的setter方法
	 * @param propertyName
	 * @return
	 */
	public Method getSetter(String propertyName) {
		Invoker invoker = setMethods.get(propertyName);
		if (invoker == null) {
			throw new ProbeException("There is no WRITEABLE property named '" + propertyName + "' in class '" + className + "'");
		}
		if (!(invoker instanceof MethodInvoker)) {
			throw new ProbeException("Can't get setter method because '" + propertyName + "' is a field in class '" + className + "'");
		}
		return ((MethodInvoker) invoker).getMethod();
	}
	
	/**
	 * 返回属性对应的getter方法调用器
	 * @param propertyName
	 * @return
	 */
	public Invoker getGetInvoker(String propertyName) {
		Invoker invoker = getMethods.get(propertyName);
		if (invoker == null) {
			throw new ProbeException("There is no READABLE property named '" + propertyName + "' in class '" + className + "'");
		}
		return invoker;
	}
	
	/**
	 * 返回属性对应的setter方法调用器
	 * @param propertyName
	 * @return
	 */
	public Invoker getSetInvoker(String propertyName) {
		Invoker invoker = setMethods.get(propertyName);
		if (invoker == null) {
			throw new ProbeException("There is no WRITEABLE property named '" + propertyName + "' in class '" + className + "'");
		}
		return invoker;
	}
	
	/**
	 * 返回属性对应的getter方法返回值类型
	 * @param propertyName
	 * @return
	 */
	public Class<?> getGetterType(String propertyName) {
		Class<?> clazz = getTypes.get(propertyName);
		if (clazz == null) {
			throw new ProbeException("There is no READABLE property named '" + propertyName + "' in class '" + className + "'");
		}
		return clazz;
	}
	
	/**
	 * 返回属性对应的setter方法参数类型
	 * @param propertyName
	 * @return
	 */
	public Class<?> getSetterType(String propertyName) {
		Class<?> clazz = setTypes.get(propertyName);
		if (clazz == null) {
			throw new ProbeException("There is no WRITEABLE property named '" + propertyName + "' in class '" + className + "'");
		}
		return clazz;
	}

	/**
	 * 返回JavaBean中可读的属性名称数组
	 * @return
	 */
	public String[] getReadablePropertyNames() {
		return readablePropertyNames;
	}

	/**
	 * 返回JavaBean中可写的属性名称数组
	 * @return
	 */
	public String[] getWriteablePropertyNames() {
		return writeablePropertyNames;
	}
	
	/**
	 * 检测指定属性名在JavaBean中是否可写属性
	 * @param propertyName
	 * @return
	 */
	public boolean hasWritableProperty(String propertyName) {
		return setMethods.containsKey(propertyName);
	}
	
	/**
	 * 检测指定属性名在JavaBean中是否可读属性
	 * @param propertyName
	 * @return
	 */
	public boolean hasReadableProperty(String propertyName) {
		return getMethods.containsKey(propertyName);
	}
	
	/**
	 * 检测一个类是否是普通的常用类
	 * @param clazz
	 * @return
	 */
	public static boolean isKnowType(Class<?> clazz) {
		if (SIMPLE_TYPE_SET.contains(clazz)) {
			return true;
		} else if (Collection.class.isAssignableFrom(clazz)) { // clazz是否实现了Collection接口
			return true;
		} else if (Map.class.isAssignableFrom(clazz)) {
			return true;
		} else if (List.class.isAssignableFrom(clazz)) {
			return true;
		} else if (Set.class.isAssignableFrom(clazz)) {
			return true;
		} else if (Iterator.class.isAssignableFrom(clazz)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取指定类的ClassInfo对象，缓存开关开启时使用缓存
	 * @param clazz
	 * @return
	 */
	public static ClassInfo getInstance(Class<?> clazz) {
		if (cacheEnabled) {
			synchronized (clazz) {
				ClassInfo cached = CLASS_INFO_MAP.get(clazz);
				if (cached == null) {
					cached = new ClassInfo(clazz);
					CLASS_INFO_MAP.put(clazz, cached);
				}
				return cached;
			}
		} else {
			return new ClassInfo(clazz);
		}
	}

	/**
	 * 设置缓存开关
	 * @param cacheEnabled
	 */
	public static void setCacheEnabled(boolean cacheEnabled) {
		ClassInfo.cacheEnabled = cacheEnabled;
	}
	
	/**
	 * 获取导致这个Throwable的根异常
	 * @param t
	 * @return
	 */
	public static Throwable unwrapThrowable(Throwable t) {
		Throwable t2 = t;
		while (true) {
			if (t2 instanceof InvocationTargetException) {
				t2 = ((InvocationTargetException) t).getTargetException();
			} else if (t instanceof UndeclaredThrowableException) {
				t2 = ((UndeclaredThrowableException) t).getUndeclaredThrowable();
			} else {
				return t2;
			}
		}
	}
}
