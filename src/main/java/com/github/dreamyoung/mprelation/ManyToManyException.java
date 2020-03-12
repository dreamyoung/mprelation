package com.github.dreamyoung.mprelation;

@SuppressWarnings("serial")
public class ManyToManyException extends RuntimeException {

	private String msg;

	public ManyToManyException(String msg) {
		this.msg = msg;
	}

	public ManyToManyException(String msg, Object object) {
		super(object.toString());
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
