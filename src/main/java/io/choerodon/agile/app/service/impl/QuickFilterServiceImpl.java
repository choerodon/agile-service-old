package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.QuickFilterDTO;
import io.choerodon.agile.api.dto.QuickFilterSearchDTO;
import io.choerodon.agile.api.dto.QuickFilterSequenceDTO;
import io.choerodon.agile.api.dto.QuickFilterValueDTO;
import io.choerodon.agile.app.service.QuickFilterService;
import io.choerodon.agile.domain.agile.entity.QuickFilterE;
import io.choerodon.agile.domain.agile.repository.QuickFilterRepository;
import io.choerodon.agile.infra.dataobject.QuickFilterDO;
import io.choerodon.agile.infra.mapper.QuickFilterFieldMapper;
import io.choerodon.agile.infra.mapper.QuickFilterMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class QuickFilterServiceImpl implements QuickFilterService {

    private static final String NOT_IN = "not in";
    private static final String IS_NOT = "is not";

    @Autowired
    private QuickFilterRepository quickFilterRepository;

    @Autowired
    private QuickFilterMapper quickFilterMapper;

    @Autowired
    private QuickFilterFieldMapper quickFilterFieldMapper;

    private static final String NOT_FOUND = "error.QuickFilter.notFound";

    private void dealCaseComponent(String field, String value, String operation, StringBuilder sqlQuery) {
        if ("null".equals(value)) {
            if ("is".equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_component_issue_rel )  ");
            } else if (IS_NOT.equals(operation)) {
                sqlQuery.append(" issue_id in ( select issue_id from agile_component_issue_rel )  ");
            }
        } else {
            if (NOT_IN.equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_component_issue_rel where component_id in " + value + " ) ");
            } else {
                sqlQuery.append(" issue_id in ( select issue_id from agile_component_issue_rel where " + field + " " + operation + " " + value + " ) ");
            }
        }
    }

    private void dealFixVersion(QuickFilterValueDTO quickFilterValueDTO, String field, String value, String operation, StringBuilder sqlQuery) {
        if ("null".equals(value)) {
            if ("is".equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_version_issue_rel where relation_type = 'fix' ) ");
            } else if (IS_NOT.equals(operation)) {
                sqlQuery.append(" issue_id in ( select issue_id from agile_version_issue_rel where relation_type = 'fix' ) ");
            }
        } else {
            if (NOT_IN.equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_version_issue_rel where version_id in " + value + " and relation_type = 'fix' ) ");
            } else {
                sqlQuery.append(" issue_id in ( select issue_id from agile_version_issue_rel where " + field + " " + quickFilterValueDTO.getOperation() + " " + value + " and relation_type = 'fix' ) ");
            }
        }
    }

    private void dealInfluenceVersion(QuickFilterValueDTO quickFilterValueDTO, String field, String value, String operation, StringBuilder sqlQuery) {
        if ("null".equals(value)) {
            if ("is".equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_version_issue_rel where relation_type = 'influence' ) ");
            } else if (IS_NOT.equals(operation)) {
                sqlQuery.append(" issue_id in ( select issue_id from agile_version_issue_rel where relation_type = 'influence' ) ");
            }
        } else {
            if (NOT_IN.equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_version_issue_rel where version_id in " + value + " and relation_type = 'influence' ) ");
            } else {
                sqlQuery.append(" issue_id in ( select issue_id from agile_version_issue_rel where " + field + " " + quickFilterValueDTO.getOperation() + " " + value + " and relation_type = 'influence' ) ");
            }
        }
    }

    private void dealCaseVersion(QuickFilterValueDTO quickFilterValueDTO, String field, String value, String operation, StringBuilder sqlQuery) {
        if ("fix_version".equals(quickFilterValueDTO.getFieldCode())) {
            dealFixVersion(quickFilterValueDTO, field, value, operation, sqlQuery);
        } else if ("influence_version".equals(quickFilterValueDTO.getFieldCode())) {
            dealInfluenceVersion(quickFilterValueDTO, field, value, operation, sqlQuery);
        }
    }

    private void dealCaseLabel(String field, String value, String operation, StringBuilder sqlQuery) {
        if ("null".equals(value)) {
            if ("is".equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_label_issue_rel ) ");
            } else if (IS_NOT.equals(operation)) {
                sqlQuery.append(" issue_id in ( select issue_id from agile_label_issue_rel ) ");
            }
        } else {
            if (NOT_IN.equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_label_issue_rel where label_id in " + value + " ) ");
            } else {
                sqlQuery.append(" issue_id in ( select issue_id from agile_label_issue_rel where " + field + " " + operation + " " + value + " ) ");
            }
        }
    }

    private void dealCaseSprint(String field, String value, String operation, StringBuilder sqlQuery) {
        if ("null".equals(value)) {
            if ("is".equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_issue_sprint_rel ) ");
            } else if (IS_NOT.equals(operation)) {
                sqlQuery.append(" issue_id in ( select issue_id from agile_issue_sprint_rel ) ");
            }
        } else {
            if (NOT_IN.equals(operation)) {
                sqlQuery.append(" issue_id not in ( select issue_id from agile_issue_sprint_rel where sprint_id in " + value + " ) ");
            } else {
                sqlQuery.append(" issue_id in ( select issue_id from agile_issue_sprint_rel where " + field + " " + operation + " " + value + " ) ");
            }
        }
    }

    private String getSqlQuery(List<QuickFilterValueDTO> quickFilterValueDTOList, List<String> relationOperations, Boolean childIncluded) {
        StringBuilder sqlQuery = new StringBuilder();
        int idx = 0;
        for (QuickFilterValueDTO quickFilterValueDTO : quickFilterValueDTOList) {
            String field = quickFilterFieldMapper.selectByPrimaryKey(quickFilterValueDTO.getFieldCode()).getField();
            String value = "'null'".equals(quickFilterValueDTO.getValue()) ? "null" : quickFilterValueDTO.getValue();
            String operation = quickFilterValueDTO.getOperation();
            switch (field) {
                case "component_id":
                    dealCaseComponent(field, value, operation, sqlQuery);
                    break;
                case "version_id":
                    dealCaseVersion(quickFilterValueDTO, field, value, operation, sqlQuery);
                    break;
                case "label_id":
                    dealCaseLabel(field, value, operation, sqlQuery);
                    break;
                case "sprint_id":
                    dealCaseSprint(field, value, operation, sqlQuery);
                    break;
                case "creation_date":
                    sqlQuery.append(" unix_timestamp(" + field + ")" + " " + quickFilterValueDTO.getOperation() + " " + "unix_timestamp('" + value + "') ");
                    break;
                case "last_update_date":
                    sqlQuery.append(" unix_timestamp(" + field + ")" + " " + quickFilterValueDTO.getOperation() + " " + "unix_timestamp('" + value + "') ");
                    break;
                default:
                    sqlQuery.append(" " + field + " " + quickFilterValueDTO.getOperation() + " " + value + " ");
                    break;
            }
            int length = relationOperations.size();
            if (idx < length && !relationOperations.get(idx).isEmpty()) {
                sqlQuery.append(relationOperations.get(idx) + " ");
                idx++;
            }
        }
        if (!childIncluded) {
            sqlQuery.append(" and type_code != 'sub_task' ");
        }
        return sqlQuery.toString();
    }

    @Override
    public QuickFilterDTO create(Long projectId, QuickFilterDTO quickFilterDTO) {
        if (!projectId.equals(quickFilterDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        if (checkName(projectId, quickFilterDTO.getName())) {
            throw new CommonException("error.quickFilterName.exist");
        }
        String sqlQuery = getSqlQuery(quickFilterDTO.getQuickFilterValueDTOList(), quickFilterDTO.getRelationOperations(), quickFilterDTO.getChildIncluded());
        QuickFilterE quickFilterE = ConvertHelper.convert(quickFilterDTO, QuickFilterE.class);
        quickFilterE.setSqlQuery(sqlQuery);
        //设置编号
        Integer sequence = quickFilterMapper.queryMaxSequenceByProject(projectId);
        quickFilterE.setSequence(sequence == null ? 0 : sequence + 1);
        return ConvertHelper.convert(quickFilterRepository.create(quickFilterE), QuickFilterDTO.class);
    }

    private Boolean checkNameUpdate(Long projectId, Long filterId, String quickFilterName) {
        QuickFilterDO quickFilterDO = quickFilterMapper.selectByPrimaryKey(filterId);
        if (quickFilterName.equals(quickFilterDO.getName())) {
            return false;
        }
        QuickFilterDO check = new QuickFilterDO();
        check.setProjectId(projectId);
        check.setName(quickFilterName);
        List<QuickFilterDO> quickFilterDOList = quickFilterMapper.select(check);
        return quickFilterDOList != null && !quickFilterDOList.isEmpty();
    }

    @Override
    public QuickFilterDTO update(Long projectId, Long filterId, QuickFilterDTO quickFilterDTO) {
        if (!projectId.equals(quickFilterDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        if (quickFilterDTO.getName() != null && checkNameUpdate(projectId, filterId, quickFilterDTO.getName())) {
            throw new CommonException("error.quickFilterName.exist");
        }
        String sqlQuery = getSqlQuery(quickFilterDTO.getQuickFilterValueDTOList(), quickFilterDTO.getRelationOperations(), quickFilterDTO.getChildIncluded());
        quickFilterDTO.setFilterId(filterId);
        QuickFilterE quickFilterE = ConvertHelper.convert(quickFilterDTO, QuickFilterE.class);
        quickFilterE.setSqlQuery(sqlQuery);
        return ConvertHelper.convert(quickFilterRepository.update(quickFilterE), QuickFilterDTO.class);
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
    public List<QuickFilterDTO> listByProjectId(Long projectId, QuickFilterSearchDTO quickFilterSearchDTO) {
        List<QuickFilterDO> quickFilterDOList = quickFilterMapper.queryFiltersByProjectId(projectId, quickFilterSearchDTO.getFilterName(), quickFilterSearchDTO.getContents());
        if (quickFilterDOList != null && !quickFilterDOList.isEmpty()) {
            return ConvertHelper.convertList(quickFilterDOList, QuickFilterDTO.class);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public QuickFilterDTO dragFilter(Long projectId, QuickFilterSequenceDTO quickFilterSequenceDTO) {
        if (quickFilterSequenceDTO.getAfterSequence() == null && quickFilterSequenceDTO.getBeforeSequence() == null) {
            throw new CommonException("error.dragFilter.noSequence");
        }
        QuickFilterE quickFilterE = ConvertHelper.convert(quickFilterMapper.selectByPrimaryKey(quickFilterSequenceDTO.getFilterId()), QuickFilterE.class);
        if (quickFilterE == null) {
            throw new CommonException(NOT_FOUND);
        } else {
            if (quickFilterSequenceDTO.getAfterSequence() == null) {
                Integer maxSequence = quickFilterMapper.queryMaxAfterSequence(quickFilterSequenceDTO.getBeforeSequence(), projectId);
                quickFilterSequenceDTO.setAfterSequence(maxSequence);
            } else if (quickFilterSequenceDTO.getBeforeSequence() == null) {
                Integer minSequence = quickFilterMapper.queryMinBeforeSequence(quickFilterSequenceDTO.getAfterSequence(), projectId);
                quickFilterSequenceDTO.setBeforeSequence(minSequence);
            }
            handleSequence(quickFilterSequenceDTO, projectId, quickFilterE);
        }
        return ConvertHelper.convert(quickFilterMapper.selectByPrimaryKey(quickFilterSequenceDTO.getFilterId()), QuickFilterDTO.class);

    }

    private void handleSequence(QuickFilterSequenceDTO quickFilterSequenceDTO, Long projectId, QuickFilterE quickFilterE) {
        if (quickFilterSequenceDTO.getBeforeSequence() == null) {
            quickFilterE.setSequence(quickFilterSequenceDTO.getAfterSequence() + 1);
            quickFilterRepository.update(quickFilterE);
        } else if (quickFilterSequenceDTO.getAfterSequence() == null) {
            if (quickFilterE.getSequence() > quickFilterSequenceDTO.getBeforeSequence()) {
                Integer add = quickFilterE.getSequence() - quickFilterSequenceDTO.getBeforeSequence();
                if (add > 0) {
                    quickFilterE.setSequence(quickFilterSequenceDTO.getBeforeSequence() - 1);
                    quickFilterRepository.update(quickFilterE);
                } else {
                    quickFilterRepository.batchUpdateSequence(quickFilterSequenceDTO.getBeforeSequence(), projectId,
                            quickFilterE.getSequence() - quickFilterSequenceDTO.getBeforeSequence() + 1, quickFilterE.getFilterId());
                }
            }
        } else {
            Integer sequence = quickFilterSequenceDTO.getAfterSequence() + 1;
            quickFilterE.setSequence(sequence);
            quickFilterRepository.update(quickFilterE);
            Integer update = sequence - quickFilterSequenceDTO.getBeforeSequence();
            if (update >= 0) {
                quickFilterRepository.batchUpdateSequence(quickFilterSequenceDTO.getBeforeSequence(), projectId, update + 1, quickFilterE.getFilterId());
            }
        }
    }

    @Override
    public Boolean checkName(Long projectId, String quickFilterName) {
        QuickFilterDO quickFilterDO = new QuickFilterDO();
        quickFilterDO.setProjectId(projectId);
        quickFilterDO.setName(quickFilterName);
        List<QuickFilterDO> quickFilterDOList = quickFilterMapper.select(quickFilterDO);
        return quickFilterDOList != null && !quickFilterDOList.isEmpty();
    }

}
