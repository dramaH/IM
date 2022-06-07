package com.dcits.im.bc.listener;

import com.alibaba.fastjson.JSON;
import com.dcits.im.constants.ChannelAttrKeys;
import com.dcits.im.constants.MessageConstants;
import com.dcits.im.constants.SessionConst;
import com.dcits.im.jpa.entity.ImRecCurrentUser;
import com.farsunset.cim.sdk.server.constant.ChannelSysAttrKeys;
import com.farsunset.cim.sdk.server.group.BindChannelGroup;
import com.farsunset.cim.sdk.server.model.MessagePushListBody;
import io.netty.channel.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 集群环境下，监控多设备登录情况，控制是否其余终端下线的逻辑
 */
@Slf4j
@Component
public class BindNoticeListener implements MessageListener {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 一个账号只能在同一个类型的终端登录
     * 如: 多个android或ios不能同时在线，一个android或ios可以和web，桌面同时在线
     */
    private final Map<String, String[]> conflictMap = new HashMap<>();

    @Resource
    private BindChannelGroup bindChannelGroup;

    public BindNoticeListener(){
        conflictMap.put(SessionConst.ChannelEnum.CHANNEL_ANDROID.getCode(),new String[]{SessionConst.ChannelEnum.CHANNEL_ANDROID.getCode(), SessionConst.ChannelEnum.CHANNEL_IOS.getCode()});
        conflictMap.put(SessionConst.ChannelEnum.CHANNEL_IOS.getCode(),new String[]{SessionConst.ChannelEnum.CHANNEL_ANDROID.getCode(), SessionConst.ChannelEnum.CHANNEL_IOS.getCode()});
        conflictMap.put(SessionConst.ChannelEnum.CHANNEL_WINDOWS.getCode(),new String[]{SessionConst.ChannelEnum.CHANNEL_WINDOWS.getCode(), SessionConst.ChannelEnum.CHANNEL_WEB.getCode(), SessionConst.ChannelEnum.CHANNEL_MAC.getCode()});
        conflictMap.put(SessionConst.ChannelEnum.CHANNEL_WEB.getCode(),new String[]{SessionConst.ChannelEnum.CHANNEL_WINDOWS.getCode(), SessionConst.ChannelEnum.CHANNEL_WEB.getCode(), SessionConst.ChannelEnum.CHANNEL_MAC.getCode()});
        conflictMap.put(SessionConst.ChannelEnum.CHANNEL_MAC.getCode(),new String[]{SessionConst.ChannelEnum.CHANNEL_WINDOWS.getCode(), SessionConst.ChannelEnum.CHANNEL_WEB.getCode(), SessionConst.ChannelEnum.CHANNEL_MAC.getCode()});
    }

    @SneakyThrows
    @Override
    public void onMessage(Message redisMessage, byte[] bytes) {
        /*byte[] bodyBytes = redisMessage.getBody();
        ImRecCurrentUser currentUser = JSON.parseObject(
                (String)redisTemplate.getValueSerializer().deserialize(bodyBytes), ImRecCurrentUser.class);
        String userId = currentUser.getUserId();
        String[] conflictChannels = conflictMap.get(currentUser.getChannel());
        Collection<Channel> channelList = bindChannelGroup.find(userId, ChannelAttrKeys.CHANNEL, conflictChannels);
        channelList.removeIf(channel -> currentUser.getNodeId().equals(channel.attr(ChannelSysAttrKeys.ID).get()));
        *//*
         * 获取到其他在线的终端连接，提示用户在其他终端登录
         *//*
        channelList.forEach(channel -> {
            if (Objects.equals(currentUser.getDeviceId(),channel.attr(ChannelAttrKeys.DEVICE_ID).get())){
                channel.close();
                return;
            }
            MessagePushListBody listBody = new MessagePushListBody();
            MessagePushListBody.MessagePushBody body = new MessagePushListBody.MessagePushBody();
            body.setAction(MessageConstants.FORCE_OFFLINE_ACTION);
            channel.writeAndFlush(listBody);
            channel.close();
        });*/
    }
}
