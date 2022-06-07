/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.dcits.im.bc.apns;

import com.dcits.im.config.properties.APNsProperties;
import com.farsunset.cim.sdk.server.model.MessagePushListBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class ApnsComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApnsComponent.class);

	//private final ApnsClient apnsClient;

	private final APNsProperties properties;

	@Autowired
	public ApnsComponent(APNsProperties properties) throws IOException {
		this.properties = properties;

		/*InputStream stream = getClass().getResourceAsStream(properties.getP12File());

		apnsClient = new ApnsClientBuilder()
				.setApnsServer(properties.isDebug() ? ApnsClientBuilder.DEVELOPMENT_APNS_HOST : ApnsClientBuilder.PRODUCTION_APNS_HOST)
				.setClientCredentials(stream, properties.getP12Password())
				.build();*/
	}

	/**
	 * 推送系统消息
	 * @param message
	 * @param deviceToken
	 */
	public void push(MessagePushListBody messagePushListBody, String deviceToken) {
		if(StringUtils.isBlank(deviceToken)) {
			return ;
		}

		/*ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();

		payloadBuilder.setAlertTitle("您有一条新的消息");

		payloadBuilder.setSound("default");
		payloadBuilder.setBadgeNumber(1);
		payloadBuilder.addCustomProperty("id",message.getId());
		payloadBuilder.addCustomProperty("action",message.getAction());
		payloadBuilder.addCustomProperty("content",message.getContent());
		payloadBuilder.addCustomProperty("sender",message.getSender());
		payloadBuilder.addCustomProperty("receiver",message.getReceiver());
		payloadBuilder.addCustomProperty("format",message.getFormat());
		payloadBuilder.addCustomProperty("extra",message.getExtra());
		payloadBuilder.addCustomProperty("timestamp",message.getTimestamp());

		String token = TokenUtil.sanitizeTokenString(deviceToken);

		String payload = payloadBuilder.build();

		ApnsPushNotification notification = new SimpleApnsPushNotification(token, properties.getAppId(), payload);

		apnsClient.sendNotification(notification).whenComplete((response, cause) -> {
			if (response != null) {
				LOGGER.info("APNs push done.\ndeviceToken : {} \napnsPayload : {}",deviceToken,payload);
			} else {
				LOGGER.error("APNs push failed",cause);
			}
		});*/
	}
}
