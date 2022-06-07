package com.dcits.im.test;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class AbstractWebsocketClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(AbstractWebsocketClient.class);

    /**
     * 接收响应的超时时间(秒)
     */
    private final int connectionTimeout;

    /**
     * 任务上下文
     */
    protected WebsocketContext websocketContext;

    public AbstractWebsocketClient(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.websocketContext = new WebsocketContext(new CountDownLatch(1));
    }

    /**
     * 发送消息.<br>
     *
     * @param message 发送文本
     * @return:
     */
    public void write(String message) throws MyException {
        Channel channel = getChannel();
        if (channel != null) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return;
        }
        throw new MyException ("连接已经关闭");
    }

    /**
     *  连接并发送消息.<br>
     *
     * @return:
     */
    public void connect() throws MyException {
        try {
            doOpen();
            doConnect();
        } catch (Exception e) {
            throw new MyException ("连接没有成功打开,原因是:{}" + e.getMessage(), e);
        }
    }

    /**
     * 接收消息.<br>
     *
     * @return: {@link java.lang.String}
     */
    public String receiveResult() throws MyException {
        this.receive(this.websocketContext.getCountDownLatch());
        if (StringUtils.isEmpty(this.websocketContext.getResult())) {
            throw new MyException("未获取到任务结果信息");
        }
        return this.websocketContext.getResult();
    }

    /**
     * 接收消息封装.<br>
     *
     * @param countDownLatch  计数器
     * @return:
     */
    private void receive(CountDownLatch countDownLatch) throws MyException {
        boolean waitFlag = false;
        try {
            waitFlag = countDownLatch.await(connectionTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.info("此连接未接收到响应信息");
            Thread.currentThread().interrupt();
        }
        if (!waitFlag) {
            log.error("Timeout({}}s) when receiving response message", connectionTimeout);
            throw new MyException ("此连接未接收到响应信息");
        }
    }

    /**
     * 初始化连接.<br>
     *
     * @return:
     */
    protected abstract void doOpen();

    /**
     * 建立连接.<br>
     *
     * @return:
     */
    protected abstract void doConnect() throws MyException;

    /**
     * 获取本次连接channel.<br>
     *
     * @return: {@link Channel}
     */
    protected abstract Channel getChannel();

    /**
     * 关闭连接.<br>
     *
     * @return:
     * @exception:
     */
    @Override
    public abstract void close();
}