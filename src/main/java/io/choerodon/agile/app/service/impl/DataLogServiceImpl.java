package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.DataLogCreateVO;
import io.choerodon.agile.api.vo.DataLogVO;
import io.choerodon.agile.api.vo.FieldDataLogDTO;
import io.choerodon.agile.api.vo.StatusMapVO;
import io.choerodon.agile.app.service.DataLogService;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.infra.common.enums.ObjectSchemeCode;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.DataLogDTO;
import io.choerodon.agile.infra.dataobject.DataLogStatusChangeDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.feign.FoundationFeignClient;
import io.choerodon.agile.infra.mapper.DataLogMapper;
import io.choerodon.agile.infra.repository.DataLogRepository;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
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
    private UserService userService;
    @Autowired
    private FoundationFeignClient foundationFeignClient;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public DataLogVO createDataLog(Long projectId, DataLogCreateVO createVO) {
        DataLogDTO dataLogDTO = modelMapper.map(createVO, DataLogDTO.class);
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
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<Long> createByIds = dataLogVOS.stream().filter(dataLogDTO -> dataLogDTO.getCreatedBy() != null && !Objects.equals(dataLogDTO.getCreatedBy(), 0L)).map(DataLogVO::getCreatedBy).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(createByIds, true);
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
                StatusMapVO statusMapVO = statusMapDTOMap.get(Long.parseLong(dto.getNewValue()));
                dto.setCategoryCode(statusMapVO != null ? statusMapVO.getType() : null);
            }
            if (dto.getIsCusLog() == null) {
                dto.setIsCusLog(false);
            }
        }
    }

    @Override
    public DataLogDTO create(DataLogDTO dataLogDTO) {
        if (dataLogMapper.insert(dataLogDTO) != 1) {
            throw new CommonException("error.dataLog.insert");
        }
        return dataLogMapper.selectByPrimaryKey(dataLogDTO.getLogId());
    }

    @Override
    public void delete(DataLogDTO dataLogDTO) {
        dataLogMapper.delete(dataLogDTO);
    }

    @Override
    public void batchDeleteErrorDataLog(Set<Long> dataLogIds) {
        dataLogMapper.batchDeleteErrorDataLog(dataLogIds);
    }

    @Override
    public void batchUpdateErrorDataLog(Set<DataLogStatusChangeDTO> dataLogStatusChangeDTOS) {
        dataLogMapper.batchUpdateErrorDataLog(dataLogStatusChangeDTOS);
    }

}
