package com.wb.ibatis.common.beans;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.wb.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年2月4日
 */

public class DomProbeTest {
	
	private static Document document;
	
	private DomProbe probe;
	
	@BeforeClass
	public static void initClass() throws IOException {
		File file = Resources.getResourceAsFile("test.xml");
		document = getDocument(file);
	}
	
	@Before
	public void instantiate() {
		probe = new DomProbe();
	}

	@Test
	public void testGetReadablePropertyNames() {
		String[] readable = probe.getReadablePropertyNames(document);
		printArray(readable);
	}
	
	@Test
	public void testGetPropertyTypeForGetter() {
		Class<?> clazz = probe.getPropertyTypeForGetter(document, "school[1].class[0].student[2]");
		Assert.assertEquals("The return class should be type of DomProbeTest", DomProbeTest.class, clazz);
	}
	
	@Test
	public void testHasWritableProperty() {
		boolean has = probe.hasWritableProperty(document, "school[1].class[0].student[2]");
		Assert.assertEquals("The return should be true", true, has);
	}
	
	@Test
	public void testGetObject() {
		Object object = probe.getObject(document, "school[1].class[0].student[2]");
		Assert.assertEquals("The return should be \"王小锤是六石人\"", "王小锤是六石人", object);
	}
	
	@Test
	public void testSetObject() {
		probe.setObject(document, "school[1].class[0].student[2]", "王小锤是中国人");
		probe.setObject(document, "school[0]", "六石中心小学是六石街道最大的小学");
		probe.setObject(document, "factory[0]", "六石丝绸厂的技工组已经解散了");
		probe.setObject(document, "factory[1]", document);
		
		System.out.println(DomProbe.nodeToString(document, ""));
	}
	
	@Test 
	public void testNodeToString() {
		String xmlString = DomProbe.nodeToString(document, "");
		System.out.println(xmlString);
	}
	
	/**
	 * 解析XML文档
	 * @param reader
	 * @return
	 */
	private static Document getDocument(File file) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// 指定此工厂生成的解析器将提供对XML名称工具的支持
			factory.setNamespaceAware(false);
			// 开启验证属性，开启此属性会验证XML文档是否符合DTD
			factory.setValidating(false);
			// 指定此工厂生成的解析器将忽略注释
			factory.setIgnoringComments(true);
			// 指定此工厂生成的解析器将在解析XML文档时，必须删除元素内容中的空格
			factory.setIgnoringElementContentWhitespace(true);
			// 指定此工厂生成的解析器将把CDATA节点转换为Text节点，并将其附加到相邻的Text节点
			factory.setCoalescing(false);
			// 指定此工厂生成的解析器将扩展实体引用节点
			factory.setExpandEntityReferences(false);
			
//			OutputStreamWriter errorWriter = new OutputStreamWriter(System.err);
			
			// 使用工厂生成XML文档解析器
			DocumentBuilder builder = factory.newDocumentBuilder();
//			// 给解析器注册错误事件处理程序
//			builder.setErrorHandler(new SimpleErrorHandler(new PrintWriter(errorWriter, true)));
//			// 给解析器注册自定义的实体解析器
//			builder.setEntityResolver(new DaoClasspathEntityResolver());
			
			// 解析器解析XML文档
			Document document = builder.parse(file);
			return document;
		} catch (Exception e) {
			throw new RuntimeException("XML Parser Error. Cause: " + e);
		}
	}
	
	private void printArray(Object[] objArray) {
		for (Object object: objArray) {
			System.out.print(object + " | ");
		}
		System.out.println();
	}
}
