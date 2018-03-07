package com.tbj.comsumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import com.rabbitmq.client.Channel;

public class DirectConsumer implements ChannelAwareMessageListener{

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		System.out.println("Direct:"+message);
		int i = 0;
		if(i % 2 == 0){
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
		}else{
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		}
		i++;
	}

	
}
