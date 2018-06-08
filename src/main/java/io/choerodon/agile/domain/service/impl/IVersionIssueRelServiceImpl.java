package io.choerodon.agile.domain.service.impl;

import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.agile.domain.service.*;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.agile.infra.dataobject.VersionIssueRelDO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
@Service
@Transactional(rollbackFor = CommonException.class)
public class IVersionIssueRelServiceImpl extends BaseServiceImpl<VersionIssueRelDO> implements IVersionIssueRelService {

}