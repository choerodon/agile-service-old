package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.LookupTypeDTO;
import io.choerodon.agile.app.service.LookupTypeService;
import io.choerodon.agile.infra.mapper.LookupTypeMapper;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
@Service
public class LookupTypeServiceImpl implements LookupTypeService {

    @Autowired
    private LookupTypeMapper lookupTypeMapper;

    @Override
    public List<LookupTypeDTO> listLookupType(Long project) {
        return ConvertHelper.convertList(lookupTypeMapper.selectAll(), LookupTypeDTO.class);
    }

}