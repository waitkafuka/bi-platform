package com.baidu.rigel.biplatform.cache.listener;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

import redis.clients.jedis.JedisPubSub;

public class PrintListener extends JedisPubSub{

	@Override
	public void onMessage(String channel, String message) {
		String time = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		System.out.println("message receive:" + message + ",channel:" + channel + "..." + time);
		//此处我们可以取消订阅
		if(message.equalsIgnoreCase("quit")){
			this.unsubscribe(channel);
		}
	}

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        // TODO Auto-generated method stub
        System.out.println("message receive:" + message + ",channel:" + channel + "..." + "pattern");
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        // TODO Auto-generated method stub
        
    }
}