package com.dcits.im.test;

import com.farsunset.cim.sdk.server.coder.WebMessageDecoder;
import com.farsunset.cim.sdk.server.coder.WebMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;

public class WebsocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final WebsocketClientHandler handler;

    public WebsocketChannelInitializer(WebsocketClientHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new WebMessageDecoder());
        p.addLast(new WebMessageEncoder());
        p.addLast(new HttpObjectAggregator(8192));
        p.addLast(WebSocketClientCompressionHandler.INSTANCE);
        p.addLast(handler);
    }
}