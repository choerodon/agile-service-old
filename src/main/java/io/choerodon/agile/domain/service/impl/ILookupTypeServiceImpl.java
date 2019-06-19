package io.choerodon.agile.domain.service.impl;

import io.choerodon.agile.domain.service.*;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
@Service
@Transactional(rollbackFor = CommonException.class)
public class ILookupTypeServiceImpl implements ILookupTypeService {

}