package com.tbj.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tbj.bean.Account1Log;
import com.tbj.mapper.Account1LogMapper;
import com.tbj.service.Account1LogService;

@Service("account1LogService")
public class Account1LogServiceImpl implements Account1LogService {
	
	@Autowired
	private Account1LogMapper account1LogMapper;

	@Override
	public int insert(Account1Log model) {
		return account1LogMapper.insert(model);
	}

	@Override
	public int countByKey(String key) {
		return account1LogMapper.countByKey(key);
	}

}
