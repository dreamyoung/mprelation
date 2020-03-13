package com.github.dreamyoung.mprelation;

@SuppressWarnings("serial")
public class ManyToOneException extends RuntimeException {

	private String msg;

	public ManyToOneException(String msg) {
		this.msg = msg;
	}

	public ManyToOneException(String msg, Object object) {
		super(object.toString());
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
