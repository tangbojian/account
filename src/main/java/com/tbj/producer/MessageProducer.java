package com.tbj.producer;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service("messageProducer")
public class MessageProducer {

	@Resource(name="amqpTemplate")
	private AmqpTemplate amqpTemplate;
	
	public void send(Object message) {
		amqpTemplate.convertAndSend("repay", message);
	}
	
}
