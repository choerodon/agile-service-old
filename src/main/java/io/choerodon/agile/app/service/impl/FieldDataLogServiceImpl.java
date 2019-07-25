package io.choerodon.agile.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.FieldDataLogCreateVO;
import io.choerodon.agile.api.vo.FieldDataLogVO;
import io.choerodon.agile.app.service.FieldDataLogService;
import io.choerodon.agile.infra.dataobject.FieldDataLogDTO;
import io.choerodon.agile.infra.enums.ObjectSchemeCode;
import io.choerodon.agile.infra.mapper.FieldDataLogMapper;
import io.choerodon.agile.infra.utils.EnumUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/6/19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FieldDataLogServiceImpl implements FieldDataLogService {

    private static final String ERROR_DATALOG_CREATE = "error.dataLog.create";
    private static final String ERROR_SCHEMECODE_ILLEGAL = "error.schemeCode.illegal";
    @Autowired
    private FieldDataLogMapper fieldDataLogMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public FieldDataLogDTO baseCreate(FieldDataLogDTO create) {
        if (fieldDataLogMapper.insert(create) != 1) {
            throw new CommonException(ERROR_DATALOG_CREATE);
        }
        return fieldDataLogMapper.selectByPrimaryKey(create.getId());
    }

    @Override
    public FieldDataLogVO createDataLog(Long projectId, String schemeCode, FieldDataLogCreateVO create) {
        FieldDataLogDTO dataLog = modelMapper.map(create, FieldDataLogDTO.class);
        dataLog.setProjectId(projectId);
        dataLog.setSchemeCode(schemeCode);
        return modelMapper.map(baseCreate(dataLog), FieldDataLogVO.class);
    }

    @Override
    public void deleteByFieldId(Long projectId, Long fieldId) {
        FieldDataLogDTO delete = new FieldDataLogDTO();
        delete.setFieldId(fieldId);
        delete.setProjectId(projectId);
        fieldDataLogMapper.delete(delete);
    }

    @Override
    public List<FieldDataLogVO> queryByInstanceId(Long projectId, Long instanceId, String schemeCode) {
        if (!EnumUtil.contain(ObjectSchemeCode.class, schemeCode)) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        return modelMapper.map(fieldDataLogMapper.queryByInstanceId(projectId, schemeCode, instanceId), new TypeToken<List<FieldDataLogVO>>() {
        }.getType());
    }
}
