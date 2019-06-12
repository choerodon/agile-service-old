package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.DataLogCreateDTO;
import io.choerodon.agile.api.dto.DataLogDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.app.assembler.DataLogAssembler;
import io.choerodon.agile.app.service.DataLogService;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.common.enums.ObjectSchemeCode;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.feign.FoundationFeignClient;
import io.choerodon.agile.infra.mapper.DataLogMapper;
import io.choerodon.agile.infra.repository.DataLogRepository;
import io.choerodon.core.convertor.ConvertHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DataLogServiceImpl implements DataLogService {

    @Autowired
    private DataLogRepository dataLogRepository;
    @Autowired
    private DataLogMapper dataLogMapper;
    @Autowired
    private DataLogAssembler dataLogAssembler;
    @Autowired
    private FoundationFeignClient foundationFeignClient;

    private ModelMapper modelMapper = new ModelMapper();
    private static final String CUS_PREFIX = "cus_";

    @Override
    public DataLogDTO create(Long projectId, DataLogCreateDTO createDTO) {
        DataLogE dataLogE = modelMapper.map(createDTO, DataLogE.class);
        dataLogE.setProjectId(projectId);
        return ConvertHelper.convert(dataLogRepository.create(dataLogE), DataLogDTO.class);
    }

    @Override
    public List<DataLogDTO> listByIssueId(Long projectId, Long issueId) {
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<DataLogDTO> dataLogDTOS = dataLogAssembler.dataLogDOToDTO(dataLogMapper.selectByIssueId(projectId, issueId), statusMapDTOMap);
        List<String> cusFields = dataLogDTOS.stream().map(DataLogDTO::getField).filter(x -> x.startsWith(CUS_PREFIX)).collect(Collectors.toList());
        if (!cusFields.isEmpty()) {
            //拼凑自定义字段的fieldName，以便于前端展示
            Map<String, String> cusMap = foundationFeignClient.queryFieldNameMap(projectId, organizationId, ObjectSchemeCode.AGILE_ISSUE, cusFields).getBody();
            dataLogDTOS.forEach(dataLog -> dataLog.setFieldName(cusMap.get(dataLog.getField())));
        }
        return dataLogDTOS;
    }

}
