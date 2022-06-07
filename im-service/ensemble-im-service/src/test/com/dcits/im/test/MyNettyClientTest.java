package com.dcits.im.test;

import java.net.URISyntaxException;

public class MyNettyClientTest {
    public static void main(String[] args) throws URISyntaxException, MyException {
        WebsocketClient websocketClient = new WebsocketClient("ws://10.5.2.122:8089", 3);
        websocketClient.connect();
        websocketClient.write("1{'aaa':'aaa','bbb':'bbb'}");
    }
}
