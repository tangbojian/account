package com.tbj.test;

import java.math.BigDecimal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tbj.bean.Account1;
import com.tbj.service.impl.Account1ServiceImpl;


public class TestInit {

	public static void main(String[] args) {
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
		System.out.println(applicationContext);
//		Account1ServiceImpl bean = applicationContext.getBean(Account1ServiceImpl.class);
//		Account1 model = new Account1();
//		model.setId(1);
//		model.setBalance(new BigDecimal(100));
//		bean.modify(model);
		
	}
	
}
