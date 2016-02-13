package com.wb.ibatis.common.beans.testbean;

import java.util.List;

/**
 * @author www
 * @date 2016年2月11日
 */

public class Team {

	private String name;
	
	private List<Student> students;
	
	public Team() {
		
	}

	public Team(String name, List<Student> students) {
		super();
		this.name = name;
		this.students = students;
	}

	@Override
	public String toString() {
		return "Team [name=" + name + ", students=" + students + "]";
	}
	
}
