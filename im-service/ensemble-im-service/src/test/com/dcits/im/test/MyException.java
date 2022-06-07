package com.dcits.im.test;

public class MyException extends Exception {


    public MyException () {
        super();
    }

    public MyException (String message) {
        super(message);

    }

    /**
     * 用指定的详细信息和原因构造一个新的异常.<br>
     *
     * @param message
     * @param cause
     * @return:
     */
    public MyException (String message, Throwable cause) {
        super(message, cause);
    }

}