package io.choerodon.agile;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/3
 */
@Configuration
public class RedisTestConfig {
    @Bean
    @SuppressWarnings("unchecked")
    public RedisSerializer<Object> defaultRedisSerializer() {
        return Mockito.mock(RedisSerializer.class);
    }


    @Bean
    public RedisConnectionFactory connectionFactory() {
        RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);
        RedisConnection connection = Mockito.mock(RedisConnection.class);
        Mockito.when(factory.getConnection()).thenReturn(connection);

        return factory;
    }
}
