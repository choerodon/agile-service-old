package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.IssueLinkTypeE;
import io.choerodon.agile.domain.agile.repository.IssueLinkTypeRepository;
import io.choerodon.agile.infra.dataobject.IssueLinkTypeDO;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Component
@Transactional(rollbackFor = CommonException.class)
public class IssueLinkTypeRepositoryImpl implements IssueLinkTypeRepository {

    private static final String UPDATE_ERROR = "error.IssueLinkType.update";
    private static final String INSERT_ERROR = "error.IssueLinkType.create";

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;

    @Override
    public IssueLinkTypeE update(IssueLinkTypeE issueLinkTypeE) {
        IssueLinkTypeDO issueLinkTypeDO = ConvertHelper.convert(issueLinkTypeE, IssueLinkTypeDO.class);
        if (issueLinkTypeMapper.updateByPrimaryKeySelective(issueLinkTypeDO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(issueLinkTypeMapper.selectByPrimaryKey(issueLinkTypeDO.getLinkTypeId()), IssueLinkTypeE.class);
    }

    @Override
    public IssueLinkTypeE create(IssueLinkTypeE issueLinkTypeE) {
        IssueLinkTypeDO issueLinkTypeDO = ConvertHelper.convert(issueLinkTypeE, IssueLinkTypeDO.class);
        if (issueLinkTypeMapper.insert(issueLinkTypeDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        IssueLinkTypeDO query = new IssueLinkTypeDO();
        query.setLinkTypeId(issueLinkTypeDO.getLinkTypeId());
        return ConvertHelper.convert(issueLinkTypeMapper.selectByPrimaryKey(issueLinkTypeDO.getLinkTypeId()), IssueLinkTypeE.class);
    }

    @Override
    public int delete(Long linkTypeId) {
        IssueLinkTypeDO issueLinkTypeDO = new IssueLinkTypeDO();
        issueLinkTypeDO.setLinkTypeId(linkTypeId);
        return issueLinkTypeMapper.delete(issueLinkTypeDO);
    }
}
