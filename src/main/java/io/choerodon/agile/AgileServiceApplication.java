package io.choerodon.agile;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 敏捷开发服务入口类
 *
 * @author dinghuang123@gmail.com
 */
@EnableChoerodonResourceServer
@EnableAsync
@EnableEurekaClient
@SpringBootApplication
@EnableCaching
public class AgileServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgileServiceApplication.class);
    }
}
