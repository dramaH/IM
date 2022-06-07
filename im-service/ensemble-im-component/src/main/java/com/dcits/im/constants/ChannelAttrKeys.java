package com.dcits.im.constants;

import io.netty.util.AttributeKey;

public interface ChannelAttrKeys {
    AttributeKey<String> USER_ID = AttributeKey.valueOf("user_id");
    AttributeKey<String> CHANNEL = AttributeKey.valueOf("channel");
    AttributeKey<String> DEVICE_ID = AttributeKey.valueOf("device_id");
    AttributeKey<String> TAG = AttributeKey.valueOf("tag");
    AttributeKey<String> LANGUAGE = AttributeKey.valueOf("language");
}
