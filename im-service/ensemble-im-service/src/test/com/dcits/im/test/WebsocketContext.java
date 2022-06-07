package com.dcits.im.test;

import java.util.concurrent.CountDownLatch;

public class WebsocketContext {

    /**
     * 计数器(用于监听是否返回结果)
     */
    private CountDownLatch countDownLatch;

    /**
     * 最终结果
     */
    private String result;

    public WebsocketContext(CountDownLatch countDownLatch) {
        this.countDownLatch= countDownLatch;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch= countDownLatch;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}