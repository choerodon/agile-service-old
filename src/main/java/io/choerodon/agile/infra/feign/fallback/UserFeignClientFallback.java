package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.api.dto.UserDTO;
import io.choerodon.agile.infra.dataobject.UserDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
public class UserFeignClientFallback implements UserFeignClient {

    private static final String QUERY_ERROR = "error.UserFeign.query";
    private static final String BATCH_QUERY_ERROR = "error.UserFeign.queryList";

    @Override
    public ResponseEntity<UserDO> query(Long organizationId, Long id) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<UserDO>> listUsersByIds(Long[] ids) {
        throw new CommonException(BATCH_QUERY_ERROR);
    }

    @Override
    public ResponseEntity<ProjectDTO> queryProject(Long id) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<Page<UserDTO>> list(Long id, PageRequest pageRequest, String param) {
        throw new CommonException(QUERY_ERROR);
    }

}