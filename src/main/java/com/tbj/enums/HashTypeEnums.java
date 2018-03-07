package com.tbj.enums;

/**
 * @author bjtang
 * @date   2017年10月18日  
 * @desc   数据存在哪些hash中
 */
public enum HashTypeEnums {

	PRE_DATA("预发送数据", "pre_data"),
	SUC_SEND_DATA("成功发送的数据", "suc_send_data"),
	DEATH_DATA("死亡数据", "death_data");
	
	private final String name;
	private final String val;
	
	HashTypeEnums(String name, String val){
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
