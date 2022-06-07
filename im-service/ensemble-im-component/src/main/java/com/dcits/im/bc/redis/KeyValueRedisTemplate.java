package com.dcits.im.bc.redis;

import com.dcits.im.constants.MessageConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;


@Component
public class KeyValueRedisTemplate {
	private final Duration duration = Duration.ofSeconds(30);

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	public void set(String key ,String value) {
		redisTemplate.boundValueOps(key).set(value);
	}

	public String get(String key) {
		return redisTemplate.boundValueOps(key).get();
	}

	public boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	public void setTemp(String key, String value) {
		redisTemplate.boundValueOps(key).set(value, duration);
	}

	public String getDeviceToken(String uid){
		return redisTemplate.boundValueOps(String.format(MessageConstants.APNS_DEVICE_TOKEN,uid)).get();
	}

	public void openApns(String uid,String deviceToken){
		redisTemplate.boundValueOps(String.format(MessageConstants.APNS_DEVICE_TOKEN,uid)).set(deviceToken);
	}

	public void closeApns(String uid){
		redisTemplate.delete(String.format(MessageConstants.APNS_DEVICE_TOKEN,uid));
	}


}
