package com.github.dreamyoung.mprelation;

@SuppressWarnings("serial")
public class AutoMapperException extends RuntimeException {
	private String msg;

	public AutoMapperException(String msg) {
		this.msg = msg;
	}

	public AutoMapperException(String msg, Object object) {
		super(object.toString());
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
