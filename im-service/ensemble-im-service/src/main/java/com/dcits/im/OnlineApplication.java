package com.dcits.im;

import com.dcits.im.config.properties.APNsProperties;
import com.dcits.im.config.properties.IMProperties;
import com.dcits.rpc.consumer.core.annotation.EnableCometClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Auther hanqian@dcits.com
 * @Create 2021/11/27 15:29:22
 */
@SpringBootApplication
@EnableCometClients(basePackages = {"com.dcits.im"})
//@EnableEurekaClient
@EnableConfigurationProperties({
        APNsProperties.class,
        IMProperties.class})
public class OnlineApplication {

  public static void main(String[] args) {
    try {
      SpringApplication.run(OnlineApplication.class, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
