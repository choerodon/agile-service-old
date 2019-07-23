package io.choerodon.agile.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.ConfigCodeVO;
import io.choerodon.agile.api.vo.StateMachineConfigVO;
import io.choerodon.agile.app.service.ConfigCodeService;
import io.choerodon.agile.app.service.StateMachineConfigService;
import io.choerodon.agile.infra.dataobject.StateMachineConfigDTO;
import io.choerodon.agile.infra.dataobject.StateMachineConfigDraftDTO;
import io.choerodon.agile.infra.enums.ConfigType;
import io.choerodon.agile.infra.mapper.StateMachineConfigDraftMapper;
import io.choerodon.agile.infra.mapper.StateMachineConfigMapper;
import io.choerodon.agile.infra.utils.EnumUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StateMachineConfigServiceImpl implements StateMachineConfigService {

    private static final String ERROR_STATUS_TYPE_ILLEGAL = "error.status.type.illegal";
    @Autowired
    private StateMachineConfigDraftMapper configDraftMapper;
    @Autowired
    private StateMachineConfigMapper configDeployMapper;
    @Autowired
    private ConfigCodeService configCodeService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StateMachineConfigVO create(Long organizationId, Long stateMachineId, Long transformId, StateMachineConfigVO configDTO) {
        if (!EnumUtil.contain(ConfigType.class, configDTO.getType())) {
            throw new CommonException(ERROR_STATUS_TYPE_ILLEGAL);
        }
        //验证configCode
        checkCode(transformId, configDTO.getType(), configDTO.getCode());

        configDTO.setTransformId(transformId);
        configDTO.setOrganizationId(organizationId);
        StateMachineConfigDraftDTO config = modelMapper.map(configDTO, StateMachineConfigDraftDTO.class);
        config.setStateMachineId(stateMachineId);
        int isInsert = configDraftMapper.insert(config);
        if (isInsert != 1) {
            throw new CommonException("error.stateMachineConfig.create");
        }
        config = configDraftMapper.queryById(organizationId, config.getId());
        return modelMapper.map(config, StateMachineConfigVO.class);
    }

    @Override
    public Boolean delete(Long organizationId, Long configId) {
        StateMachineConfigDraftDTO config = new StateMachineConfigDraftDTO();
        config.setId(configId);
        config.setOrganizationId(organizationId);
        int isDelete = configDraftMapper.delete(config);
        if (isDelete != 1) {
            throw new CommonException("error.stateMachineConfig.delete");
        }
        return true;
    }

    @Override
    public List<StateMachineConfigVO> queryByTransformId(Long organizationId, Long transformId, String type, Boolean isDraft) {
        if (type != null && !EnumUtil.contain(ConfigType.class, type)) {
            throw new CommonException(ERROR_STATUS_TYPE_ILLEGAL);
        }
        List<StateMachineConfigVO> configVOS;
        if (isDraft) {
            List<StateMachineConfigDraftDTO> configs = configDraftMapper.queryWithCodeInfo(organizationId, transformId, type);
            configVOS = modelMapper.map(configs, new TypeToken<List<StateMachineConfigVO>>() {
            }.getType());
        } else {
            List<StateMachineConfigDTO> configs = configDeployMapper.queryWithCodeInfo(organizationId, transformId, type);
            configVOS = modelMapper.map(configs, new TypeToken<List<StateMachineConfigVO>>() {
            }.getType());
        }
        return configVOS;
    }

    @Override
    public List<StateMachineConfigVO> queryDeployByTransformIds(Long organizationId, String type, List<Long> transformIds) {
        if (!EnumUtil.contain(ConfigType.class, type)) {
            throw new CommonException(ERROR_STATUS_TYPE_ILLEGAL);
        }
        if (transformIds != null && !transformIds.isEmpty()) {
            List<StateMachineConfigDTO> configs = configDeployMapper.queryWithCodeInfoByTransformIds(organizationId, type, transformIds);
            return modelMapper.map(configs, new TypeToken<List<StateMachineConfigVO>>() {
            }.getType());
        } else {
            return Collections.emptyList();
        }
    }

    public void checkCode(Long transformId, String type, String code) {
        List<ConfigCodeVO> configCodeVOS = configCodeService.queryByType(type);
        if (configCodeVOS.stream().noneMatch(configCodeDTO -> configCodeDTO.getCode().equals(code))) {
            throw new CommonException("error.configCode.illegal");
        }
        StateMachineConfigDraftDTO configDraft = new StateMachineConfigDraftDTO();
        configDraft.setTransformId(transformId);
        configDraft.setCode(code);
        if (!configDraftMapper.select(configDraft).isEmpty()) {
            throw new CommonException("error.configCode.exist");
        }

    }
}
