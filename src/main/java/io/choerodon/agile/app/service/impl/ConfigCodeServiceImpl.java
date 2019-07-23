package io.choerodon.agile.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.ConfigCodeVO;
import io.choerodon.agile.api.vo.PropertyData;
import io.choerodon.agile.app.service.ConfigCodeService;
import io.choerodon.agile.infra.dataobject.ConfigCodeDTO;
import io.choerodon.agile.infra.dataobject.StateMachineConfigDraftDTO;
import io.choerodon.agile.infra.enums.ConfigType;
import io.choerodon.agile.infra.mapper.ConfigCodeMapper;
import io.choerodon.agile.infra.mapper.StateMachineConfigDraftMapper;
import io.choerodon.agile.infra.utils.EnumUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @date 2018/10/10
 */
@Service
public class ConfigCodeServiceImpl implements ConfigCodeService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigCodeServiceImpl.class);
    @Autowired
    private ConfigCodeMapper configCodeMapper;
    @Autowired
    private StateMachineConfigDraftMapper configDraftMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ConfigCodeVO> queryByType(String type) {
        if (!EnumUtil.contain(ConfigType.class, type)) {
            throw new CommonException("error.status.type.illegal");
        }
        ConfigCodeDTO configCode = new ConfigCodeDTO();
        configCode.setType(type);
        List<ConfigCodeDTO> configCodes = configCodeMapper.select(configCode);
        return modelMapper.map(configCodes, new TypeToken<List<ConfigCodeVO>>() {
        }.getType());
    }

    @Override
    public List<ConfigCodeVO> queryByTransformId(Long organizationId, Long transformId, String type) {
        if (type == null) {
            throw new CommonException("error.type.notNull");
        }
        if (!EnumUtil.contain(ConfigType.class, type)) {
            throw new CommonException("error.status.type.illegal");
        }
        StateMachineConfigDraftDTO config = new StateMachineConfigDraftDTO();
        config.setOrganizationId(organizationId);
        config.setTransformId(transformId);
        config.setType(type);
        List<StateMachineConfigDraftDTO> configs = configDraftMapper.select(config);
        List<String> configCodes = configs.stream().map(StateMachineConfigDraftDTO::getCode).collect(Collectors.toList());
        //过滤掉已经配置的，返回未配置的code
        return queryByType(type).stream().filter(configCodeDTO -> !configCodes.contains(configCodeDTO.getCode())).collect(Collectors.toList());
    }

    @Override
    public void handlePropertyData(PropertyData propertyData) {
        String service = propertyData.getServiceName();
        if (service == null) {
            throw new CommonException("error.handlePropertyData.service.notNull");
        }
        //先删除该服务的ConfigCode
        ConfigCodeDTO delete = new ConfigCodeDTO();
        delete.setService(propertyData.getServiceName());
        configCodeMapper.delete(delete);
        //再插入扫描到的ConfigCode
        List<ConfigCodeVO> configCodeVOS = propertyData.getList();
        configCodeVOS.forEach(configCode -> {
            configCode.setService(service);
            configCodeMapper.insert(modelMapper.map(configCode, ConfigCodeDTO.class));
            logger.info("handlePropertyData service:{} insert code:{} successful", service, configCode.getCode());
        });
        logger.info("handlePropertyData load service:{} successful", service);
    }
}
