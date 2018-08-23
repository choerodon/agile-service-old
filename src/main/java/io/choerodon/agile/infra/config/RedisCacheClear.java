package io.choerodon.agile.infra.config;

import io.choerodon.agile.infra.common.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/23
 */
@Component
public class RedisCacheClear implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheClear.class);

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("项目初始化清除缓存");
        String pattern = "*";
        Set<String> caches = redisUtil.keys(pattern);
        if (!caches.isEmpty()) {
            redisUtil.deleteByKey(caches);
        }
    }
}
