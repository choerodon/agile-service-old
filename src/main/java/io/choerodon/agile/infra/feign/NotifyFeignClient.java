package io.choerodon.agile.infra.feign;



import io.choerodon.agile.api.dto.NoticeSendDTO;
import io.choerodon.agile.api.dto.WsSendDTO;
import io.choerodon.agile.infra.feign.fallback.NotifyFeignClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "notify-service", path = "/v1/notices", fallback = NotifyFeignClientFallback.class)
public interface NotifyFeignClient {

    @PostMapping("")
    void postNotice(@RequestBody NoticeSendDTO dto);

}