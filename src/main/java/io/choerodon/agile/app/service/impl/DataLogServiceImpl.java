package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.DataLogCreateVO;
import io.choerodon.agile.api.vo.DataLogVO;
import io.choerodon.agile.api.vo.FieldDataLogDTO;
import io.choerodon.agile.api.vo.StatusMapDTO;
import io.choerodon.agile.app.service.DataLogService;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.infra.common.enums.ObjectSchemeCode;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.DataLogDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.feign.FoundationFeignClient;
import io.choerodon.agile.infra.mapper.DataLogMapper;
import io.choerodon.agile.infra.repository.DataLogRepository;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
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
    public DataLogVO create(Long projectId, DataLogCreateVO createDTO) {
        DataLogDTO dataLogDTO = modelMapper.map(createDTO, DataLogDTO.class);
        dataLogDTO.setProjectId(projectId);
        if (dataLogMapper.insert(dataLogDTO) != 1) {
            throw new CommonException("error.dataLog.insert");
        }
        return modelMapper.map(dataLogMapper.selectByPrimaryKey(dataLogDTO.getLogId()), DataLogVO.class);
    }

    @Override
    public List<DataLogVO> listByIssueId(Long projectId, Long issueId) {
        List<DataLogVO> dataLogVOS = modelMapper.map(dataLogMapper.selectByIssueId(projectId, issueId), new TypeToken<List<DataLogVO>>() {
        }.getType());
        List<FieldDataLogDTO> fieldDataLogDTOs = foundationFeignClient.queryDataLogByInstanceId(projectId, issueId, ObjectSchemeCode.AGILE_ISSUE).getBody();
        for (FieldDataLogDTO fieldDataLogDTO : fieldDataLogDTOs) {
            DataLogVO dataLogVO = modelMapper.map(fieldDataLogDTO, DataLogVO.class);
            dataLogVO.setField(fieldDataLogDTO.getFieldCode());
            dataLogVO.setIssueId(fieldDataLogDTO.getInstanceId());
            dataLogVO.setIsCusLog(true);
            dataLogVOS.add(dataLogVO);
        }
        fillUserAndStatus(projectId, dataLogVOS);
        return dataLogVOS.stream().sorted(Comparator.comparing(DataLogVO::getCreationDate).reversed()).collect(Collectors.toList());
    }

    /**
     * 填充用户信息
     *
     * @param projectId
     * @param dataLogVOS
     */
    private void fillUserAndStatus(Long projectId, List<DataLogVO> dataLogVOS) {
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<Long> createByIds = dataLogVOS.stream().filter(dataLogDTO -> dataLogDTO.getCreatedBy() != null && !Objects.equals(dataLogDTO.getCreatedBy(), 0L)).map(DataLogVO::getCreatedBy).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(createByIds, true);
        for (DataLogVO dto : dataLogVOS) {
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
