package com.github.dreamyoung.mprelation;

@SuppressWarnings("serial")
public class AutoMapperConfigurationException extends RuntimeException {

	private String msg;

	public AutoMapperConfigurationException(String msg) {
		this.msg = msg;
	}

	public AutoMapperConfigurationException(String msg, Object object) {
		super(object.toString());
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
