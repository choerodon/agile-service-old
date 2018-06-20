package io.choerodon.agile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 敏捷开发服务入口类
 *
 * @author dinghuang123@gmail.com
 */
@EnableFeignClients("io.choerodon")
//@EnableChoerodonResourceServer
@EnableAsync
@EnableEurekaClient
@SpringBootApplication
public class AgileServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgileServiceApplication.class);
    }
}
