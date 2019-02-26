package io.choerodon.agile.infra.feign;

import io.choerodon.agile.infra.feign.fallback.NotifyFeignClientFallback;
import io.choerodon.core.notify.NoticeSendDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "notify-service", path = "/v1/notices", fallback = NotifyFeignClientFallback.class)
public interface NotifyFeignClient {

    @PostMapping("")
    void postNotice(@RequestBody NoticeSendDTO dto);

    @PostMapping("/ws/{code}/{id}")
    void postWebSocket(@PathVariable("code") String code,
                       @PathVariable("id") String id,
                       @RequestBody String message);

}