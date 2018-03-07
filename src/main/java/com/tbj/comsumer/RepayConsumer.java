package com.tbj.comsumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import com.rabbitmq.client.Channel;

public class RepayConsumer implements ChannelAwareMessageListener{

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		System.out.println("Repay:"+message);
		int i = 0;
		if(i % 2 == 0){
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		}else{
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
		}
		i++;
	}

}
