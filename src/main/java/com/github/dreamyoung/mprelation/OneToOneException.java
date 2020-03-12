package com.github.dreamyoung.mprelation;

@SuppressWarnings("serial")
public class OneToOneException extends RuntimeException {

	private String msg;

	public OneToOneException(String msg) {
		this.msg = msg;
	}

	public OneToOneException(String msg, Object object) {
		super(object.toString());
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
