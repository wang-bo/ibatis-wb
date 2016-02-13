package com.wb.ibatis.common.beans.testbean;

/**
 * @author www
 * @date 2016年2月11日
 */

public class Student {

	private String name;
	
	private int age;
	
	public String address;
	
	public Student() {
		
	}

	public Student(String name, int age, String address) {
		super();
		this.name = name;
		this.age = age;
		this.address = address;
	}

//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public int getAge() {
//		return age;
//	}
//
//	public void setAge(int age) {
//		this.age = age;
//	}

	@Override
	public String toString() {
		return "Student [name=" + name + ", age=" + age + ", address="
				+ address + "]";
	}
}
