package com.tbj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tbj.service.Account1Service;

@RestController
@RequestMapping("/test")
public class TestController {
	
	@Autowired
	private Account1Service account1Service;

	@RequestMapping("/index")
	public void index(){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
//		TestService bean = applicationContext.getBean("testService", TestService.class);
//		bean.execute();
	}
	
}
