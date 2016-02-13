package com.wb.ibatis.common.beans.testbean;

/**
 * @author www
 * @date 2016年2月11日
 */

public class School {
	
	private String name;

	private Grade grade;
	
	public School() {
		
	}

	public School(String name, Grade grade) {
		super();
		this.name = name;
		this.grade = grade;
	}

	@Override
	public String toString() {
		return "School [name=" + name + ", grade=" + grade + "]";
	}
	
	
	
}
