package com.dcits.im.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "im.apns")
public class APNsProperties {

    private boolean debug;

    private String appId;

    private final P12 p12 = new P12();

    public static class P12 {
        private String file;
        private String password;

        public void setFile(String file) {
            this.file = file;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFile() {
            return file;
        }

        public String getPassword() {
            return password;
        }
    }

    public P12 getP12() {
        return p12;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getP12File() {
        return p12.file;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getP12Password() {
        return p12.password;
    }
}
