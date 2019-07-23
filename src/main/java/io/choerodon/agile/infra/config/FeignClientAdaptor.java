package io.choerodon.agile.infra.config;

import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import io.choerodon.agile.infra.feign.CustomFeignClientAdaptor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author shinan.chen
 * @date 2018/9/27
 */
@Configuration
@Import(FeignClientsConfiguration.class)
public class FeignClientAdaptor {
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;
    /**
     * 配置自定义feign
     *
     * @param client
     * @param decoder
     * @param encoder
     * @param interceptor
     * @return
     */
    @Bean
    public CustomFeignClientAdaptor instanceServiceImpl(Client client, Decoder decoder, Encoder encoder, RequestInterceptor interceptor) {
        return Feign.builder().encoder(new SpringFormEncoder(new SpringEncoder(messageConverters))).decoder(decoder)
                .client(client)
                .contract(new Contract.Default())
                .requestInterceptor(interceptor)
                .target(Target.EmptyTarget.create(CustomFeignClientAdaptor.class));
    }
}
