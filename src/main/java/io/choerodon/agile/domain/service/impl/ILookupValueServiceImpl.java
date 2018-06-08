package io.choerodon.agile.domain.service.impl;

import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.agile.domain.service.*;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.agile.infra.dataobject.LookupValueDO;

/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
@Service
@Transactional(rollbackFor = CommonException.class)
public class ILookupValueServiceImpl extends BaseServiceImpl<LookupValueDO> implements ILookupValueService {

}