package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.DataLogDTO;
import io.choerodon.agile.app.service.DataLogService;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.domain.agile.repository.DataLogRepository;
import io.choerodon.agile.infra.mapper.DataLogMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class DataLogServiceImpl implements DataLogService {

    @Autowired
    private DataLogRepository dataLogRepository;

    @Autowired
    private DataLogMapper dataLogMapper;

    @Override
    public DataLogDTO create(Long projectId, DataLogDTO dataLogDTO) {
        if (!projectId.equals(dataLogDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        return ConvertHelper.convert(dataLogRepository.create(ConvertHelper.convert(dataLogDTO, DataLogE.class)), DataLogDTO.class) ;
    }

    @Override
    public List<DataLogDTO> listByIssueId(Long projectId, Long issueId) {
        return ConvertHelper.convertList(dataLogMapper.selectByIssueId(projectId, issueId), DataLogDTO.class);
    }

}
