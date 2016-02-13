package com.wb.ibatis.common.beans;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.wb.ibatis.common.beans.testbean.ClassRoom;
import com.wb.ibatis.common.beans.testbean.Grade;
import com.wb.ibatis.common.beans.testbean.School;
import com.wb.ibatis.common.beans.testbean.Student;
import com.wb.ibatis.common.beans.testbean.Team;
import com.wb.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年2月13日
 */

public class GenericProbeTest {
	
	private static GenericProbe probe;

	private Document document;
	
	private School school;
	private Grade grade;
	private ClassRoom classRoom;
	private Team team;
	private Student student;
	private Map<Object, Object> map =  new HashMap<>();
	
	@BeforeClass
	public static void initClass() {
		probe = new GenericProbe();
	}
	
	@Before
	public void instantiate() throws IOException {
		File file = Resources.getResourceAsFile("test.xml");
		document = getDocument(file);
		
		student = new Student("王大锤", 18, "六石街道樟村");
		team = new Team("第4小组", Arrays.asList(new Student[] {student, 
				new Student("王中锤", 17, "六石街道鹤峰"), new Student("王小锤", 16, "六石街道六石")}));
		classRoom = new ClassRoom("3班", team);
		grade = new Grade("2年纪", classRoom);
		school = new School("第1中学", grade);
		map.put("school", school);
	}
	
	@Test
	public void testGetReadablePropertyNames() {
		// case 0: Document
		printArray(probe.getReadablePropertyNames(document));
		
		// case 1: Object
		printArray(probe.getReadablePropertyNames(student));
	}
	
	@Test
	public void testGetWriteablePropertyNames() {
		// case 0: Document
		printArray(probe.getWriteablePropertyNames(document));
		
		// case 1: Object
		printArray(probe.getWriteablePropertyNames(student));
	}
	
	@Test
	public void testGetPropertyTypeForGetter() {
		// case 0: Document
		Assert.assertEquals("The return class should be type of DomProbeTest", DomProbeTest.class, probe.getPropertyTypeForGetter(document, "school[1].class[0].student[2]"));
		
		// case 1: Class
		Assert.assertEquals("The return class should be type of Grade", probe.getPropertyTypeForGetter(School.class, "grade"), Grade.class);
		Assert.assertEquals("The return class should be type of Team", probe.getPropertyTypeForGetter(School.class, "grade.classRoom.team"), Team.class);
		
		// case 2: Map
		Assert.assertEquals("The return class should be type of Object", probe.getPropertyTypeForGetter(map, "object"), Object.class);
		Assert.assertEquals("The return class should be type of School", probe.getPropertyTypeForGetter(map, "school"), School.class);
		
		// case 3: Object
		Assert.assertEquals("The return class should be type of Grade", probe.getPropertyTypeForGetter(school, "grade"), Grade.class);
		Assert.assertEquals("The return class should be type of Team", probe.getPropertyTypeForGetter(school, "grade.classRoom.team"), Team.class);
		
		// case 4: indexed
		Assert.assertEquals("The return class should be type of Student", probe.getPropertyTypeForGetter(school, "grade.classRoom.team.students[1]"), Student.class);
	}
	
	@Test
	public void testGetPropertyTypeForSetter() {
		// case 0: Document
		Assert.assertEquals("The return class should be type of DomProbeTest", DomProbeTest.class, probe.getPropertyTypeForSetter(document, "school[1].class[0].student[2]"));
		
		// case 1: Class
		Assert.assertEquals("The return class should be type of Grade", probe.getPropertyTypeForSetter(School.class, "grade"), Grade.class);
		Assert.assertEquals("The return class should be type of Team", probe.getPropertyTypeForSetter(School.class, "grade.classRoom.team"), Team.class);
		
		// case 2: Map
		Assert.assertEquals("The return class should be type of Object", probe.getPropertyTypeForSetter(map, "object"), Object.class);
		Assert.assertEquals("The return class should be type of School", probe.getPropertyTypeForSetter(map, "school"), School.class);
		
		// case 3: Object
		Assert.assertEquals("The return class should be type of Grade", probe.getPropertyTypeForSetter(school, "grade"), Grade.class);
		Assert.assertEquals("The return class should be type of Team", probe.getPropertyTypeForSetter(school, "grade.classRoom.team"), Team.class);
	}
	
	@Test
	public void testHasWritableProperty() {
		// case 0: Document
		Assert.assertEquals("The return should be true", true, probe.hasWritableProperty(document, "school[1].class[0].student[2]"));
		
		// case 1: Map
		Assert.assertEquals("The return should be true", probe.hasWritableProperty(map, "object"), true);
		
		// case2: Object
		Assert.assertEquals("The return should be false", probe.hasWritableProperty(school, "object"), false);
		Assert.assertEquals("The return should be true", probe.hasWritableProperty(school, "grade"), true);
		
		// case3: failure：感觉是ComplexBeanProbe.hasWritableProperty()方法的代码有问题
		Assert.assertEquals("The return should be true", probe.hasWritableProperty(school, "grade.classRoom.team"), true);		
	}
	
	@Test
	public void testHasReadableProperty() {
		// case 0: Document
		Assert.assertEquals("The return should be true", true, probe.hasReadableProperty(document, "school[1].class[0].student[2]"));
		
		// case 1: Map
		Assert.assertEquals("The return should be true", probe.hasReadableProperty(map, "object"), true);
		
		// case2: Object
		Assert.assertEquals("The return should be false", probe.hasReadableProperty(school, "object"), false);
		Assert.assertEquals("The return should be true", probe.hasReadableProperty(school, "grade"), true);
		
		// case3: failure：感觉是ComplexBeanProbe.hasReadableProperty()方法的代码有问题
		Assert.assertEquals("The return should be true", probe.hasReadableProperty(school, "grade.classRoom.team"), true);		
	}
	
	@Test
	public void testGetObject() {
		// case 0: Document
		Assert.assertEquals("The return should be \"王小锤是六石人\"", "王小锤是六石人", probe.getObject(document, "school[1].class[0].student[2]"));
		
		// case 1: Object
		Assert.assertEquals("The return should be \"王中锤\"", "王中锤", probe.getObject(school, "grade.classRoom.team.students[1].name"));
		
		// case 2: List
		System.out.println(probe.getObject(Arrays.asList(new Student[] {new Student("王大锤", 18, "六石街道樟村")}), "[0]"));
	}
	
	@Test
	public void testSetObject() {
		// case 0: Document
		probe.setObject(document, "school[1].class[0].student[2]", "王小锤是中国人");
		probe.setObject(document, "school[0]", "六石中心小学是六石街道最大的小学");
		probe.setObject(document, "factory[0]", "六石丝绸厂的技工组已经解散了");
		probe.setObject(document, "factory[1]", document);
		System.out.println(DomProbe.nodeToString(document, ""));
		
		// case 1: Object
		Team team2 = new Team("第14小组", Arrays.asList(new Student[] {new Student("张三", 1, "鹤峰"), new Student("李四", 2, "六石")}));
		School school2 = new School();
		probe.setObject(school2, "grade.classRoom.team", team2);
		System.out.println(probe.getObject(school2, "grade.classRoom.team.students[1].name"));
	}
	
	@Test
	public void testGetProperty() {
		// case 1: Map
		System.out.println(probe.getProperty(map, "school"));
		System.out.println(probe.getProperty(map, "school.grade.classRoom.team"));
		
		// case 2: Object
		System.out.println(probe.getProperty(school, "grade.classRoom.team.students"));
		
		// case 3: indexed
		System.out.println(probe.getProperty(school, "grade.classRoom.team.students[1]"));
	}
	
	@Test
	public void testSetProperty() {
		Team team2 = new Team("第14小组", Arrays.asList(new Student[] {new Student("张三", 1, "鹤峰"), new Student("李四", 2, "六石")}));
		ClassRoom classRoom2 = new ClassRoom("13班", team2);
		Grade grade2 = new Grade("12年纪", classRoom2);
		School school2 = new School("第11中学", grade2);
		
		// case 1: Map
		probe.setProperty(map, "school", school2);
		System.out.println(probe.getProperty(map, "school"));
		System.out.println(probe.getProperty(map, "school.grade.classRoom.team"));
		
		// case 2: Object
		probe.setProperty(school, "grade", grade2);
		System.out.println(probe.getProperty(school, "grade.classRoom.team.students"));
		
		// case 3: indexed
		probe.setProperty(school, "grade.classRoom.team.students[1]", new Student("王五", 3, "樟村"));
		System.out.println(probe.getProperty(school, "grade.classRoom.team.students"));
		
		// case 4: failure：ComplexBeanProbe.setProperty()方法不支持name1.name2这种嵌套格式
		probe.setProperty(school, "grade.classRoom.team", team2);
		System.out.println(probe.getProperty(school, "grade.classRoom.team.students"));
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
