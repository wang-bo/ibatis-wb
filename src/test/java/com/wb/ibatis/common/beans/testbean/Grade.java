package com.wb.ibatis.common.beans.testbean;

/**
 * @author www
 * @date 2016年2月11日
 */

public class Grade {
	
	private String name;
	
	private ClassRoom classRoom;
	
	public Grade() {
		
	}

	public Grade(String name, ClassRoom classRoom) {
		super();
		this.name = name;
		this.classRoom = classRoom;
	}

	@Override
	public String toString() {
		return "Grade [name=" + name + ", classRoom=" + classRoom + "]";
	}
	
	

}
