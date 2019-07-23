package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.api.vo.ExecuteResult;
import io.choerodon.agile.api.vo.InputVO;
import io.choerodon.agile.api.vo.event.TransformInfo;
import io.choerodon.agile.infra.feign.CustomFeignClientAdaptor;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/20
 */
public class CustomFeignClientAdaptorFallBack implements CustomFeignClientAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(CustomFeignClientAdaptorFallBack.class);

    @Override
    public void action(URI baseUri) {
        logger.info("action");
    }

    @Override
    public ResponseEntity<List<TransformInfo>> filterTransformsByConfig(URI baseUri, List<TransformInfo> transforms) {
        throw new CommonException("error.customFeignClientAdaptor.filterTransformsByConfig");
    }

    @Override
    public ResponseEntity<ExecuteResult> executeConfig(URI baseUri, InputVO inputVO) {
        throw new CommonException("error.customFeignClientAdaptor.filterTransformsByConfig");
    }
}
