package com.tbj.mapper;

import com.tbj.bean.Account1Log;

public interface Account1LogMapper {

	public int insert(Account1Log model);
	
	public int countByKey(String key);
	
}
