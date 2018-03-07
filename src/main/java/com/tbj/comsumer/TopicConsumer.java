package com.tbj.comsumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class TopicConsumer implements MessageListener{

	@Override
	public void onMessage(Message message) {
		System.out.println("topic消息 :" + message);
	}
	
}
