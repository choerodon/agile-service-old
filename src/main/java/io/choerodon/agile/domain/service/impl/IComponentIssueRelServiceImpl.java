package io.choerodon.agile.domain.service.impl;

import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.agile.domain.service.*;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.agile.infra.dataobject.ComponentIssueRelDO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:47:27
 */
@Service
@Transactional(rollbackFor = CommonException.class)
public class IComponentIssueRelServiceImpl extends BaseServiceImpl<ComponentIssueRelDO> implements IComponentIssueRelService {

}