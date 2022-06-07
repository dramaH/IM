package com.dcits.im.constants;

public interface SessionConst {
    /**
     * 用户登录状态
     */
    public enum OnlineStateEnum {
        STATE_ACTIVE(0, "登录"),
        STATE_APNS(1, "APNS登录"),
        STATE_INACTIVE(2, "未登录");

        private Integer code;
        private String name;

        OnlineStateEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Integer getCode() {
            return code;
        }
    }

    /**
     * 渠道类型
     */
    public enum ChannelEnum{
        CHANNEL_IOS("ios", "ios"),
        CHANNEL_ANDROID("android", "android"),
        CHANNEL_WINDOWS("windows", "windows"),
        CHANNEL_MAC("mac", "mac"),
        CHANNEL_WEB("web", "web");

        private String code;
        private String name;

        ChannelEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }
    }
}
