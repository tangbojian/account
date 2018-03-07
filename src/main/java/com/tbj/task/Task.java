package com.tbj.task;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tbj.bean.Account1;
import com.tbj.service.Account1Service;

@Component
public class Task implements Callable<String>{
	
	@Autowired
	private Account1Service account1Service;

	@Override
	public String call() throws Exception {
		System.out.println("start...");
		Account1 model = new Account1();
		model.setCompanyId("1001");
		model.setBalance(new BigDecimal(100));
		model.setKey(UUID.randomUUID().toString().replaceAll("-", ""));
		account1Service.modify(model);
		System.out.println("end...");
		return "";
	}

}
