package io.choerodon.agile.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.agile.api.vo.ObjectSchemeSearchVO;
import io.choerodon.agile.api.vo.ObjectSchemeVO;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public interface ObjectSchemeService {

    PageInfo<ObjectSchemeVO> pageQuery(Long organizationId, PageRequest pageRequest, ObjectSchemeSearchVO searchDTO);
}
