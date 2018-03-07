package com.tbj.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.tbj.service.Account1Service;

public class TestService {

	@Autowired
	private Account1Service account1Service;
	
	public void execute(){
		System.out.println(account1Service);
	}
	
}
