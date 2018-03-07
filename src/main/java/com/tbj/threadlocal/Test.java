package com.tbj.threadlocal;

import java.util.Map;

public class Test {

	
	public static void main(String[] args) {
		
		Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
		for(Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()){
			System.out.println(entry.getKey());
			System.out.println("------------------");
			for(StackTraceElement ele : entry.getValue()){
				System.out.println(ele);
				System.out.println("================");
			}
		}
		
	}
	
	
	
}

class throwTest{
	
	
	public static void main(String[] args) {
		
		try {
			int i = 10 / 0;
		} catch (Exception e){
			System.out.println("catche");
			throw new RuntimeException(e);
		} finally {
			System.out.println("finally");
		}
		
	}
}
