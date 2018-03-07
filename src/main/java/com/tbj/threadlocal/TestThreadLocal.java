package com.tbj.threadlocal;

public class TestThreadLocal extends ThreadLocal<String>{

	@Override
	protected String initialValue() {
		System.out.println("ThreadName: " + Thread.currentThread().getName());
		return "1";
	}
	
	@Override
	public void set(String value) {
		super.set(value);
	}
	
	@Override
	public String get() {
		return super.get();
	}
}
