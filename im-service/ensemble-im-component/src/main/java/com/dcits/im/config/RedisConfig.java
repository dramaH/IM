package com.dcits.im.config;

import com.dcits.im.constants.MessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.util.Objects;


@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisConfig {

    @Autowired
    public RedisConfig(LettuceConnectionFactory connectionFactory, @Value("${spring.profiles.active}") String profile){
        if (Objects.equals("dev",profile)){
            connectionFactory.setValidateConnection(true);
        }
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       MessageListener bindNoticeListener,
                                                                       MessageListener messagePushListListener){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(bindNoticeListener,new ChannelTopic(MessageConstants.BIND_NOTICE_INNER_QUEUE));
        container.addMessageListener(messagePushListListener,new ChannelTopic(MessageConstants.MESSAGE_PUSH_LIST_INNER_QUEUE));
        return container;
    }

    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        /*通过在这里设置redis存储数据采取的序列化格式*/
        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}