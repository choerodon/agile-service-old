package io.choerodon.agile.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.agile.api.vo.PageSearchVO;
import io.choerodon.agile.api.vo.PageVO;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface PageService {
    PageInfo<PageVO> pageQuery(Long organizationId, PageRequest pageRequest, PageSearchVO searchDTO);
}
