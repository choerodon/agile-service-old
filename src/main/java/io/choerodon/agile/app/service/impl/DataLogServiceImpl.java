package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.DataLogCreateDTO;
import io.choerodon.agile.api.dto.DataLogDTO;
import io.choerodon.agile.api.dto.FieldDataLogDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.app.service.DataLogService;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.infra.common.enums.ObjectSchemeCode;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.feign.FoundationFeignClient;
import io.choerodon.agile.infra.mapper.DataLogMapper;
import io.choerodon.agile.infra.repository.DataLogRepository;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.core.convertor.ConvertHelper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private UserRepository userRepository;
    @Autowired
    private FoundationFeignClient foundationFeignClient;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public DataLogDTO create(Long projectId, DataLogCreateDTO createDTO) {
        DataLogE dataLogE = modelMapper.map(createDTO, DataLogE.class);
        dataLogE.setProjectId(projectId);
        return ConvertHelper.convert(dataLogRepository.create(dataLogE), DataLogDTO.class);
    }

    @Override
    public List<DataLogDTO> listByIssueId(Long projectId, Long issueId) {
        List<DataLogDTO> dataLogDTOS = modelMapper.map(dataLogMapper.selectByIssueId(projectId, issueId), new TypeToken<List<DataLogDTO>>() {
        }.getType());
        List<FieldDataLogDTO> fieldDataLogDTOs = foundationFeignClient.queryDataLogByInstanceId(projectId, issueId, ObjectSchemeCode.AGILE_ISSUE).getBody();
        for (FieldDataLogDTO fieldDataLogDTO : fieldDataLogDTOs) {
            DataLogDTO dataLogDTO = modelMapper.map(fieldDataLogDTO, DataLogDTO.class);
            dataLogDTO.setField(fieldDataLogDTO.getFieldCode());
            dataLogDTO.setIssueId(fieldDataLogDTO.getInstanceId());
            dataLogDTO.setIsCusLog(true);
            dataLogDTOS.add(dataLogDTO);
        }
        fillUserAndStatus(projectId, dataLogDTOS);
        return dataLogDTOS.stream().sorted(Comparator.comparing(DataLogDTO::getCreationDate)).collect(Collectors.toList());
    }

    /**
     * 填充用户信息
     *
     * @param projectId
     * @param dataLogDTOS
     */
    private void fillUserAndStatus(Long projectId, List<DataLogDTO> dataLogDTOS) {
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<Long> createByIds = dataLogDTOS.stream().filter(dataLogDTO -> dataLogDTO.getCreatedBy() != null && !Objects.equals(dataLogDTO.getCreatedBy(), 0L)).map(DataLogDTO::getCreatedBy).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(createByIds, true);
        for (DataLogDTO dto : dataLogDTOS) {
            UserMessageDO userMessageDO = usersMap.get(dto.getCreatedBy());
            String name = userMessageDO != null ? userMessageDO.getName() : null;
            String loginName = userMessageDO != null ? userMessageDO.getLoginName() : null;
            String realName = userMessageDO != null ? userMessageDO.getRealName() : null;
            String imageUrl = userMessageDO != null ? userMessageDO.getImageUrl() : null;
            String email = userMessageDO != null ? userMessageDO.getEmail() : null;
            dto.setName(name);
            dto.setLoginName(loginName);
            dto.setRealName(realName);
            dto.setImageUrl(imageUrl);
            dto.setEmail(email);
            if ("status".equals(dto.getField())) {
                StatusMapDTO statusMapDTO = statusMapDTOMap.get(Long.parseLong(dto.getNewValue()));
                dto.setCategoryCode(statusMapDTO != null ? statusMapDTO.getType() : null);
            }
            if (dto.getIsCusLog() == null) {
                dto.setIsCusLog(false);
            }
        }
    }

}
