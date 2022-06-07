package com.dcits.im.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "im")
public class IMProperties {

    private final App app = new App();

    private final Websocket websocket = new Websocket();

    public App getApp() {
        return app;
    }

    public Websocket getWebsocket() {
        return websocket;
    }

    public static class App {

        private Integer port;

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getPort() {
            return port;
        }
    }

    public static class Websocket {
        private Integer port;

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    public Integer getAppPort() {
        return  app.port;
    }

    public Integer getWebsocketPort() {
        return  websocket.port;
    }
}
