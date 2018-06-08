package io.choerodon.agile.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.LookupValueE;
import io.choerodon.agile.domain.agile.repository.LookupValueRepository;
import io.choerodon.agile.infra.dataobject.LookupValueDO;
import io.choerodon.agile.infra.mapper.LookupValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
@Component
@Transactional(rollbackFor = CommonException.class)
public class LookupValueRepositoryImpl implements LookupValueRepository {

    private static final String UPDATE_ERROR = "error.LookupValue.update";
    private static final String INSERT_ERROR = "error.LookupValue.insert";
    private static final String DELETE_ERROR = "error.LookupValue.delete";

    @Autowired
    private LookupValueMapper lookupValueMapper;

    @Override
    public LookupValueE update(LookupValueE lookupValueE) {
        LookupValueDO lookupValueDO = ConvertHelper.convert(lookupValueE, LookupValueDO.class);
        if (lookupValueMapper.updateByPrimaryKeySelective(lookupValueDO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(lookupValueMapper.selectByPrimaryKey(lookupValueDO.getValueCode()), LookupValueE.class);
    }

    @Override
    public LookupValueE create(LookupValueE lookupValueE) {
        LookupValueDO lookupValueDO = ConvertHelper.convert(lookupValueE, LookupValueDO.class);
        if (lookupValueMapper.insert(lookupValueDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(lookupValueMapper.selectByPrimaryKey(lookupValueDO.getValueCode()), LookupValueE.class);
    }

    @Override
    public int delete(String valueCode) {
        LookupValueDO lookupValueDO = lookupValueMapper.selectByPrimaryKey(valueCode);
        int isDelete = lookupValueMapper.delete(lookupValueDO);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return isDelete;
    }
}