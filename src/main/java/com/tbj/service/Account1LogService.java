package com.tbj.service;

import com.tbj.bean.Account1Log;

public interface Account1LogService {

	public int insert(Account1Log model);
	
	public int countByKey(String key);
	
}
