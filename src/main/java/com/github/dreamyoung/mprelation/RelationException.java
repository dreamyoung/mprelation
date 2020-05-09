package com.github.dreamyoung.mprelation;

@SuppressWarnings("serial")
public class RelationException extends RuntimeException {

	private String msg;

	public RelationException(String msg) {
		this.msg = msg;
	}

	public RelationException(String msg, Object object) {
		super(object.toString());
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
