package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.DataLogDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.app.assembler.DataLogAssembler;
import io.choerodon.agile.app.service.DataLogService;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.domain.agile.repository.DataLogRepository;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.mapper.DataLogMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    @Override
    public DataLogDTO create(Long projectId, DataLogDTO dataLogDTO) {
        if (!projectId.equals(dataLogDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        return ConvertHelper.convert(dataLogRepository.create(ConvertHelper.convert(dataLogDTO, DataLogE.class)), DataLogDTO.class) ;
    }

    @Override
    public List<DataLogDTO> listByIssueId(Long projectId, Long issueId) {
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        return dataLogAssembler.dataLogDOToDTO(dataLogMapper.selectByIssueId(projectId, issueId), statusMapDTOMap);
    }

}
