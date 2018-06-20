package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.QuickFilterDTO;
import io.choerodon.agile.api.dto.QuickFilterValueDTO;
import io.choerodon.agile.app.service.QuickFilterService;
import io.choerodon.agile.domain.agile.entity.QuickFilterE;
import io.choerodon.agile.domain.agile.repository.QuickFilterRepository;
import io.choerodon.agile.infra.dataobject.QuickFilterDO;
import io.choerodon.agile.infra.mapper.QuickFilterFieldMapper;
import io.choerodon.agile.infra.mapper.QuickFilterMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class QuickFilterServiceImpl implements QuickFilterService {

    @Autowired
    private QuickFilterRepository quickFilterRepository;

    @Autowired
    private QuickFilterMapper quickFilterMapper;

    @Autowired
    private QuickFilterFieldMapper quickFilterFieldMapper;

    private String getSqlQuery(List<QuickFilterValueDTO> quickFilterValueDTOList, List<String> relationOperations) {
        String sqlQuery = "";
        int idx = 0;
        for (QuickFilterValueDTO quickFilterValueDTO : quickFilterValueDTOList) {
            String field = quickFilterFieldMapper.selectByPrimaryKey(quickFilterValueDTO.getFieldId()).getField();
            sqlQuery = sqlQuery + field + quickFilterValueDTO.getOperation() + quickFilterValueDTO.getValue() + " ";
            int length = relationOperations.size();
            if (idx < length && !relationOperations.get(idx).isEmpty()) {
                sqlQuery = sqlQuery + relationOperations.get(idx) +" ";
                idx ++;
            }
        }
        return sqlQuery;
    }

    @Override
    public QuickFilterDTO create(Long projectId, QuickFilterDTO quickFilterDTO) {
        if (!projectId.equals(quickFilterDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        String sqlQuery = getSqlQuery(quickFilterDTO.getQuickFilterValueDTOList(), quickFilterDTO.getRelationOperations());
        QuickFilterE quickFilterE = ConvertHelper.convert(quickFilterDTO, QuickFilterE.class);
        quickFilterE.setSqlQuery(sqlQuery);
        return ConvertHelper.convert(quickFilterRepository.create(quickFilterE), QuickFilterDTO.class);
    }

    @Override
    public QuickFilterDTO update(Long projectId, Long filterId, QuickFilterDTO quickFilterDTO) {
        if (!projectId.equals(quickFilterDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        quickFilterDTO.setFilterId(filterId);
        return ConvertHelper.convert(quickFilterRepository.update(ConvertHelper.convert(quickFilterDTO, QuickFilterE.class)), QuickFilterDTO.class);
    }

    @Override
    public void deleteById(Long projectId, Long filterId) {
        quickFilterRepository.deleteById(filterId);
    }

    @Override
    public QuickFilterDTO queryById(Long projectId, Long filterId) {
        QuickFilterDO quickFilterDO = quickFilterMapper.selectByPrimaryKey(filterId);
        if (quickFilterDO == null) {
            throw new CommonException("error.quickFilter.get");
        }
        return ConvertHelper.convert(quickFilterDO, QuickFilterDTO.class);
    }

    @Override
    public List<QuickFilterDTO> listByProjectId(Long projectId) {
        QuickFilterDO quickFilterDO = new QuickFilterDO();
        quickFilterDO.setProjectId(projectId);
        return ConvertHelper.convertList(quickFilterMapper.select(quickFilterDO), QuickFilterDTO.class);
    }
}
