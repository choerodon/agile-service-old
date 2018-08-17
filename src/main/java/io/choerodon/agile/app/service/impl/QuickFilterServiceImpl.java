package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.QuickFilterDTO;
import io.choerodon.agile.api.dto.QuickFilterSequenceDTO;
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
import org.springframework.transaction.annotation.Transactional;

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
                sqlQuery.append(" issue_id in ( select issue_id from agile_component_issue_rel where " + field +" " + operation + " " + value + " ) ");
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
                    sqlQuery.append(" unix_timestamp(" + field + ")" + " " + quickFilterValueDTO.getOperation() + " " + "unix_timestamp('" + value+"') ");
                    break;
                case "last_update_date":
                    sqlQuery.append(" unix_timestamp(" + field + ")" + " " + quickFilterValueDTO.getOperation() + " " + "unix_timestamp('" + value+"') ");
                    break;
                default:
                    sqlQuery.append(" " + field + " " + quickFilterValueDTO.getOperation() + " " + value + " ");
                    break;
            }
            int length = relationOperations.size();
            if (idx < length && !relationOperations.get(idx).isEmpty()) {
                sqlQuery.append(relationOperations.get(idx) +" ");
                idx ++;
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
        String sqlQuery = getSqlQuery(quickFilterDTO.getQuickFilterValueDTOList(), quickFilterDTO.getRelationOperations(), quickFilterDTO.getChildIncluded());
        QuickFilterE quickFilterE = ConvertHelper.convert(quickFilterDTO, QuickFilterE.class);
        quickFilterE.setSqlQuery(sqlQuery);
        //设置编号
        Integer sequence = quickFilterMapper.queryMaxSequenceByProject(projectId);
        quickFilterE.setSequence(sequence == null ? 0 : sequence + 1);
        return ConvertHelper.convert(quickFilterRepository.create(quickFilterE), QuickFilterDTO.class);
    }

    @Override
    public QuickFilterDTO update(Long projectId, Long filterId, QuickFilterDTO quickFilterDTO) {
        if (!projectId.equals(quickFilterDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
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
    public List<QuickFilterDTO> listByProjectId(Long projectId) {
        return ConvertHelper.convertList(quickFilterMapper.queryFiltersByProjectId(projectId), QuickFilterDTO.class);
    }

    @Override
    public QuickFilterDTO dragFilter(Long projectId, QuickFilterSequenceDTO quickFilterSequenceDTO) {
        if (quickFilterSequenceDTO.getAfterSequence() == null && quickFilterSequenceDTO.getBeforeSequence() == null) {
            throw new CommonException("error.dragFilter.noSequence");
        }
        QuickFilterE quickFilterE = ConvertHelper.convert(quickFilterMapper.selectByPrimaryKey(quickFilterSequenceDTO.getFilterId()),QuickFilterE.class);
        if (quickFilterE == null) {
            throw new CommonException(NOT_FOUND);
        }
        Integer sequence;
        if (quickFilterSequenceDTO.getAfterSequence() == null) {
            Integer maxSequence = quickFilterMapper.queryMaxAfterSequence(quickFilterSequenceDTO.getBeforeSequence(), projectId);
            sequence = maxSequence != null ? maxSequence + 1 : quickFilterSequenceDTO.getBeforeSequence();
        } else {
            sequence = quickFilterSequenceDTO.getAfterSequence() + 1;
        }
        handleSequence(quickFilterSequenceDTO, sequence, projectId, quickFilterE);
        return ConvertHelper.convert(quickFilterMapper.selectByPrimaryKey(quickFilterSequenceDTO.getFilterId()),QuickFilterDTO.class);

    }

    private void handleSequence(QuickFilterSequenceDTO quickFilterSequenceDTO, Integer sequence, Long projectId, QuickFilterE quickFilterE) {
        if (quickFilterSequenceDTO.getBeforeSequence() == null) {
            Integer minSequence = quickFilterMapper.queryMinBeforeSequence(quickFilterSequenceDTO.getAfterSequence(), projectId);
            if (minSequence == null) {
                quickFilterE.setSequence(sequence);
                quickFilterRepository.update(quickFilterE);
            } else {
                quickFilterRepository.batchUpdateSequence(sequence, projectId,1);
            }
        } else {
            if (sequence > quickFilterSequenceDTO.getBeforeSequence()) {
                Integer add = sequence - quickFilterSequenceDTO.getBeforeSequence() + 1;
                quickFilterRepository.batchUpdateSequence(sequence, projectId,add);
                if (quickFilterSequenceDTO.getAfterSequence() == null) {
                    quickFilterE.setSequence(sequence);
                    quickFilterRepository.update(quickFilterE);
                }
            } else {
                Integer update = sequence;
                if (sequence < quickFilterSequenceDTO.getAfterSequence()) {
                    Integer addUpdate = quickFilterSequenceDTO.getAfterSequence() - sequence + 1;
                    update = update + addUpdate;
                    quickFilterRepository.batchUpdateSequence(quickFilterSequenceDTO.getAfterSequence(), projectId, addUpdate + 1);
                }
                quickFilterE.setSequence(update);
                quickFilterRepository.update(quickFilterE);
            }
        }
    }

}
