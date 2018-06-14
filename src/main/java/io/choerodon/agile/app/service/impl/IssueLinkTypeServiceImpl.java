package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.IssueLinkTypeDTO;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Service
@Transactional(rollbackFor = CommonException.class)
public class IssueLinkTypeServiceImpl implements IssueLinkTypeService {

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;

    @Override
    public List<IssueLinkTypeDTO> listIssueLinkType() {
        return ConvertHelper.convertList(issueLinkTypeMapper.selectAll(), IssueLinkTypeDTO.class);
    }
}
