package com.wb.ibatis.common.beans.testbean;

/**
 * @author www
 * @date 2016年2月11日
 */

public class ClassRoom {

	private String name;
	
	private Team team;
	
	public ClassRoom() {
		
	}

	public ClassRoom(String name, Team team) {
		super();
		this.name = name;
		this.team = team;
	}

	@Override
	public String toString() {
		return "Class [name=" + name + ", team=" + team + "]";
	}
	
	
}
