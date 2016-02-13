package com.wb.ibatis.sqlmap.engine.mapping.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wb.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年2月11日
 * 
 * 创建result objects的实例，
 * 如果配置了"resultObjectFactory"元素，则使用ResultObjectFactory来创建result objects，否则使用iBATIS内部机制定义的方法。
 * 
 * 注意：这个类与SqlExecuter有些紧密耦合关系。SqlExecute在执行sql前需调用setStatementId()、setResultObjectFactory()方法，
 * 这里使用ThreadLocal来保存当前的配置(使用ThreadLocal是根据iBATIS-366的规定)，如果不使用ThreadLocal，在很多方法的参数中需要加入当前factory和statementId。
 */

public class ResultObjectFactoryUtil {

	/**
	 * 使用ThreadLocal来保存当前factory和statementId。
	 * 这么实现比传给每个方法参数简单，而且对性能也没有显著影响(iBATIS作者(Jeff Butler)测试过10W行的结果)
	 */
	private static ThreadLocal<FactorySettings> factorySettings = new ThreadLocal<>();
	
	/**
	 * 工具类，无需实例化
	 */
	private ResultObjectFactoryUtil() {
		
	}
	
	/**
	 * 通过factory来创建对象，如果factory为null，则通过createObjectInternally()方法来创建。
	 */
	public static Object createObjectThroughFactory(Class<?> clazz) throws
		InstantiationException, IllegalAccessException {
		Object object = null;
		FactorySettings setting = getFactorySettings();
		if (setting.getResultObjectFactory() == null) {
			object = createObjectInternally(clazz);
		} else {
			object = setting.getResultObjectFactory().createInstance(setting.getStatementId(), clazz);
			if (object == null) {
				object = createObjectInternally(clazz);
			}
		}
		return object;
	}
	
	/**
	 * 使用iBATIS内部机制定义的方法来创建对象。
	 * 将List和Collection转换为ArrayList，Set转换为HashSet，因为可能会在嵌套的resultMap中使用。
	 */
	private static Object createObjectInternally(Class<?> clazz) throws
		InstantiationException, IllegalAccessException {
		Class<?> classToCreate;
		if (clazz == List.class || clazz == Collection.class) {
			classToCreate = ArrayList.class;
		} else if (clazz == Set.class) {
			classToCreate = HashSet.class;
		} else {
			classToCreate = clazz;
		}
		Object object = Resources.instantiate(classToCreate);
		return object;
	}
	
	/**
	 * 设置当前ResultObjectFactory
	 */
	public static void setResultObjectFactory(ResultObjectFactory factory) {
		getFactorySettings().setResultObjectFactory(factory);
	}
	
	/**
	 * 设置当前statementId
	 */
	public static void setStatementId(String statementId) {
		getFactorySettings().setStatementId(statementId);
	}
	
	private static FactorySettings getFactorySettings() {
		FactorySettings setting = factorySettings.get();
		if (setting == null) {
			setting = new FactorySettings();
			factorySettings.set(setting);
		}
		return setting;
	}
	
	/**
	 * 保存当前factory和statementId的内部类，放置在ThreadLocal中。
	 */
	private static class FactorySettings {
		
		private ResultObjectFactory resultObjectFactory;
		
		private String statementId;
		
		public ResultObjectFactory getResultObjectFactory() {
			return resultObjectFactory;
		}
		
		public void setResultObjectFactory(ResultObjectFactory resultObjectFactory) {
			this.resultObjectFactory = resultObjectFactory;
		}
		
		public String getStatementId() {
			return statementId;
		}
		
		public void setStatementId(String statementId) {
			this.statementId = statementId;
		}
	}
}
