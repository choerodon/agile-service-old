package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.dataobject.IssueLinkDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueLinkE;
import io.choerodon.agile.infra.repository.IssueLinkRepository;
import io.choerodon.agile.infra.mapper.IssueLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
@Component
public class IssueLinkRepositoryImpl implements IssueLinkRepository {

    private static final String INSERT_ERROR = "error.IssueLink.create";

    @Autowired
    private IssueLinkMapper issueLinkMapper;

    @Override
    public List<IssueLinkE> create(IssueLinkE issueLinkE) {
        IssueLinkDTO issueLinkDTO = ConvertHelper.convert(issueLinkE, IssueLinkDTO.class);
        if (issueLinkMapper.insert(issueLinkDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        IssueLinkDTO query = new IssueLinkDTO();
        query.setIssueId(issueLinkDTO.getIssueId());
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