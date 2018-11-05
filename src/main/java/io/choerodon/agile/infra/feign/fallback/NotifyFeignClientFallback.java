package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.api.dto.NoticeSendDTO;
import io.choerodon.agile.api.dto.WsSendDTO;
import io.choerodon.agile.infra.feign.NotifyFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;


@Component
public class NotifyFeignClientFallback implements NotifyFeignClient {

    private static final String FEIGN_ERROR = "notify.error";

    @Override
    public void postNotice(NoticeSendDTO dto) {
        throw new CommonException(FEIGN_ERROR);
    }
}
