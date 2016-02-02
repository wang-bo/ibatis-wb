package com.wb.ibatis.common.beans;

/**
 * @author www
 * @date 2016年1月31日
 * 
 * 使用BeanProbe和StaticBeanProbe出错时抛出的异常
 * 
 */

public class ProbeException extends RuntimeException {

	private static final long serialVersionUID = 6608322546484835303L;

	public ProbeException() {
		
	}
	
	public ProbeException(String message) {
		super(message);
	}
	
	public ProbeException(Throwable cause) {
		super(cause);
	}
	
	public ProbeException(String message, Throwable cause) {
		super(message, cause);
	}
}
