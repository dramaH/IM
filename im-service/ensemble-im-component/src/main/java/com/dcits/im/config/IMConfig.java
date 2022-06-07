package com.dcits.im.config;

import com.dcits.comet.commons.ThreadLocalManager;
import com.dcits.im.config.properties.IMProperties;
import com.dcits.im.jpa.entity.ImRecCurrentUser;
import com.dcits.im.jpa.repository.ImRecCurrentUserRepository;
import com.farsunset.cim.sdk.server.coder.AppMessageDecoder;
import com.farsunset.cim.sdk.server.coder.AppMessageEncoder;
import com.farsunset.cim.sdk.server.coder.WebMessageDecoder;
import com.farsunset.cim.sdk.server.coder.WebMessageEncoder;
import com.farsunset.cim.sdk.server.group.BindChannelGroup;
import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.handler.IMRequestHandler;
import com.farsunset.cim.sdk.server.model.Transporter;
import io.netty.channel.Channel;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class IMConfig implements IMRequestHandler, ApplicationListener<ApplicationStartedEvent> {

	@Resource
	private ImRecCurrentUserRepository currentUserRepository;

	@Resource
	private ApplicationContext applicationContext;

	private final HashMap<String,IMRequestHandler> handlerMapping = new HashMap<>();

	/**
	 * 消接收消息事件主处理方法
	 * @param channel 渠道
	 * @param body 绑定申请消息
	 */
	@Override
	public void process(Channel channel, Transporter body) {
		IMRequestHandler handler = handlerMapping.get(body.getOperation());
		if(handler == null) {return ;}
		ThreadLocalManager.put("tranTimestamp",System.currentTimeMillis());
		handler.process(channel, body);
	}

	@Bean
	public BindChannelGroup bindChannelGroup() {
		return new BindChannelGroup();
	}


	@Bean(destroyMethod = "destroy")
	public CIMNioSocketAcceptor getNioSocketAcceptor(IMProperties properties) {
		return new CIMNioSocketAcceptor.Builder()
				.setAppPort(properties.getAppPort())
				.setWebsocketPort(properties.getWebsocketPort())
				.setOuterRequestHandler(this)
				.build();

	}

	/**
	 * springboot启动完成之后再启动im服务的，避免服务正在重启时，客户端会立即开始连接导致意外异常发生.
	 * @param applicationStartedEvent 启动事件
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {

		Map<String, IMRequestHandler> beans =  applicationContext.getBeansOfType(IMRequestHandler.class);

		for (Map.Entry<String, IMRequestHandler> requestHandlerEntry : beans.entrySet()) {
			String  beanName = requestHandlerEntry.getKey();
			IMRequestHandler handler = requestHandlerEntry.getValue();
            handlerMapping.put(beanName.toLowerCase() ,handler);
		}
		applicationContext.getBean(CIMNioSocketAcceptor.class).bind(
				AppMessageDecoder::new, AppMessageEncoder::new, WebMessageDecoder::new, WebMessageEncoder::new);

		ThreadLocalManager.put("tranTimestamp",System.currentTimeMillis());
		currentUserRepository.delete(new ImRecCurrentUser());
	}
}