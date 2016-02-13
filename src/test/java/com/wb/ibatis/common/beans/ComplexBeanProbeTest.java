package com.wb.ibatis.common.beans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wb.ibatis.common.beans.testbean.ClassRoom;
import com.wb.ibatis.common.beans.testbean.Grade;
import com.wb.ibatis.common.beans.testbean.School;
import com.wb.ibatis.common.beans.testbean.Student;
import com.wb.ibatis.common.beans.testbean.Team;

/**
 * @author www
 * @date 2016年2月11日
 */

public class ComplexBeanProbeTest {
	
	private static ComplexBeanProbe probe;
	
	private School school;
	private Grade grade;
	private ClassRoom classRoom;
	private Team team;
	private Student student;
	private Map<Object, Object> map =  new HashMap<>();
	
	@BeforeClass
	public static void initClass() {
		probe = new ComplexBeanProbe();
	}
	
	@Before
	public void instantiate() {
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
		String[] readable = probe.getReadablePropertyNames(student);
		printArray(readable);
	}
	
	@Test
	public void testGetWriteablePropertyNames() {
		String[] writeable = probe.getWriteablePropertyNames(student);
		printArray(writeable);
	}
	
	@Test
	public void testGetPropertyTypeForGetter() {
		// case 1: Class
		Assert.assertEquals("The return class should be type of Grade", probe.getPropertyTypeForGetter(School.class, "grade"), Grade.class);
		Assert.assertEquals("The return class should be type of Team", probe.getPropertyTypeForGetter(School.class, "grade.classRoom.team"), Team.class);
		
		// case 2: Map
		Assert.assertEquals("The return class should be type of Object", probe.getPropertyTypeForGetter(map, "object"), Object.class);
		Assert.assertEquals("The return class should be type of School", probe.getPropertyTypeForGetter(map, "school"), School.class);
		
		// case 3: Object
		Assert.assertEquals("The return class should be type of Grade", probe.getPropertyTypeForGetter(school, "grade"), Grade.class);
		Assert.assertEquals("The return class should be type of Team", probe.getPropertyTypeForGetter(school, "grade.classRoom.team"), Team.class);
	}
	
	@Test
	public void testGetPropertyTypeForSetter() {
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
		System.out.println(probe.getObject(school, "grade.classRoom.team.students[1].name"));
	}
	
	@Test
	public void testSetObject() {
		Team team2 = new Team("第14小组", Arrays.asList(new Student[] {new Student("张三", 1, "鹤峰"), new Student("李四", 2, "六石")}));
	//	ClassRoom classRoom2 = new ClassRoom("13班", team2);
	//	Grade grade2 = new Grade("12年纪", classRoom2);
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
	
	private void printArray(Object[] objArray) {
		for (Object object: objArray) {
			System.out.print(object + " | ");
		}
		System.out.println();
	}
}
