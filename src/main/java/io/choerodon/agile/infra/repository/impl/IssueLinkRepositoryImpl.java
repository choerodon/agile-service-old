package io.choerodon.agile.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueLinkE;
import io.choerodon.agile.domain.agile.repository.IssueLinkRepository;
import io.choerodon.agile.infra.dataobject.IssueLinkDO;
import io.choerodon.agile.infra.mapper.IssueLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
@Component
@Transactional(rollbackFor = CommonException.class)
public class IssueLinkRepositoryImpl implements IssueLinkRepository {

    private static final String UPDATE_ERROR = "error.IssueLink.update";
    private static final String INSERT_ERROR = "error.IssueLink.create";

    @Autowired
    private IssueLinkMapper issueLinkMapper;

    @Override
    public IssueLinkE update(IssueLinkE issueLinkE) {
        IssueLinkDO issueLinkDO = ConvertHelper.convert(issueLinkE, IssueLinkDO.class);
        if (issueLinkMapper.updateByPrimaryKeySelective(issueLinkDO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(issueLinkMapper.selectByPrimaryKey(issueLinkDO.getIssueId()), IssueLinkE.class);
    }

    @Override
    public List<IssueLinkE> create(IssueLinkE issueLinkE) {
        IssueLinkDO issueLinkDO = ConvertHelper.convert(issueLinkE, IssueLinkDO.class);
        if (issueLinkMapper.insert(issueLinkDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        IssueLinkDO query = new IssueLinkDO();
        query.setIssueId(issueLinkDO.getIssueId());
        return ConvertHelper.convertList(issueLinkMapper.select(query), IssueLinkE.class);
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        return issueLinkMapper.deleteByIssueId(issueId);
    }

    @Override
    public int delete(Long issueLinkId) {
        return issueLinkMapper.deleteByPrimaryKey(issueLinkId);
    }

}