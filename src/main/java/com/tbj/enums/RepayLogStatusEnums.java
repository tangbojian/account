package com.tbj.enums;

/**
 * @author bjtang
 * @date   2017年10月18日  
 * @desc   还款记录状态 	
 */
public enum RepayLogStatusEnums {

	PRE_SEND("预发送", "preSend"),
	SUC_SEND("成功发送", "sucSend"),
	SUC_DEAL("成功处理", "sucDeal"),
	DEATH("死亡数据", "death");
	
	private final String name;
	private final String val;
	
	RepayLogStatusEnums(String name, String val){
		this.name = name;
		this.val = val;
	}
	
	public String getName() {
		return name;
	}
	
	public String getVal() {
		return val;
	}
	
}
