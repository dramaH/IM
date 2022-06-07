package com.dcits.im.bc.handler.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

//由于当前SpringCloud架构中代理Bean对象获取不到该注解，改用使用Bean名称区别对应处理
@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface IMHandler {
    String key();
}
