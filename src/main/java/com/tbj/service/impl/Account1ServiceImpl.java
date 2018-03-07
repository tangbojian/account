package com.tbj.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tbj.bean.Account1;
import com.tbj.mapper.Account1Mapper;
import com.tbj.service.Account1Service;

@Service("account1Service")
public class Account1ServiceImpl implements Account1Service {
	
	@Autowired
	private Account1Mapper account1Mapper;

	@Override
	@Transactional
	public int modify(Account1 model) {
		//在这个方法之前，我们需要将某些信息存放到预发送表。
		int i = 0;
		int num = 0;
		//num = 10 / 0;
		try {
			Thread.sleep(10);
			i = account1Mapper.update(model);
			//num = 10 / 0;
		} catch (Exception e) {
			//num = 10 / 0;
		}
		//num = 10 / 0;
		return i;
	}

	@Override
	public void update(Account1 model) {
		account1Mapper.update(model);
	}

}
