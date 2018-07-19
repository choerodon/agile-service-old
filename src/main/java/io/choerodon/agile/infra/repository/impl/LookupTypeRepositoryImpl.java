package io.choerodon.agile.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.LookupTypeE;
import io.choerodon.agile.domain.agile.repository.LookupTypeRepository;
import io.choerodon.agile.infra.dataobject.LookupTypeDO;
import io.choerodon.agile.infra.mapper.LookupTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
@Component
public class LookupTypeRepositoryImpl implements LookupTypeRepository {

    private static final String UPDATE_ERROR = "error.LookupType.update";
    private static final String INSERT_ERROR = "error.LookupType.insert";
    private static final String DELETE_ERROR = "error.LookupType.delete";

    @Autowired
    private LookupTypeMapper lookupTypeMapper;

    @Override
    public LookupTypeE update(LookupTypeE lookupTypeE) {
        LookupTypeDO lookupTypeDO = ConvertHelper.convert(lookupTypeE, LookupTypeDO.class);
        if (lookupTypeMapper.updateByPrimaryKeySelective(lookupTypeDO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(lookupTypeMapper.selectByPrimaryKey(lookupTypeDO.getTypeCode()), LookupTypeE.class);
    }

    @Override
    public LookupTypeE create(LookupTypeE lookupTypeE) {
        LookupTypeDO lookupTypeDO = ConvertHelper.convert(lookupTypeE, LookupTypeDO.class);
        if (lookupTypeMapper.insert(lookupTypeDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(lookupTypeMapper.selectByPrimaryKey(lookupTypeDO.getTypeCode()), LookupTypeE.class);
    }

    @Override
    public int delete(String typeCode) {
        LookupTypeDO lookupTypeDO = lookupTypeMapper.selectByPrimaryKey(typeCode);
        int isDelete = lookupTypeMapper.delete(lookupTypeDO);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return isDelete;
    }
}