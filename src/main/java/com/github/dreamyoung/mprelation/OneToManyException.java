package com.github.dreamyoung.mprelation;

@SuppressWarnings("serial")
public class OneToManyException extends RuntimeException {

	private String msg;

	public OneToManyException(String msg) {
		this.msg = msg;
	}

	public OneToManyException(String msg, Object object) {
		super(object.toString());
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
