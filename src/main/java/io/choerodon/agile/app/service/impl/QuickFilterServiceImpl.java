package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.QuickFilterSequenceVO;
import io.choerodon.agile.api.vo.QuickFilterVO;
import io.choerodon.agile.api.vo.QuickFilterSearchVO;
import io.choerodon.agile.api.vo.QuickFilterValueVO;
import io.choerodon.agile.app.service.QuickFilterService;
import io.choerodon.agile.infra.dataobject.QuickFilterDTO;
import io.choerodon.agile.infra.mapper.QuickFilterFieldMapper;
import io.choerodon.agile.infra.mapper.QuickFilterMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
    private QuickFilterMapper quickFilterMapper;

    @Autowired
    private QuickFilterFieldMapper quickFilterFieldMapper;

    private static final String NOT_FOUND = "error.QuickFilter.notFound";

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

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

    private void dealFixVersion(QuickFilterValueVO quickFilterValueVO, String field, String value, String operation, StringBuilder sqlQuery) {
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
                sqlQuery.append(" issue_id in ( select issue_id from agile_version_issue_rel where " + field + " " + quickFilterValueVO.getOperation() + " " + value + " and relation_type = 'fix' ) ");
            }
        }
    }

    private void dealInfluenceVersion(QuickFilterValueVO quickFilterValueVO, String field, String value, String operation, StringBuilder sqlQuery) {
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
                sqlQuery.append(" issue_id in ( select issue_id from agile_version_issue_rel where " + field + " " + quickFilterValueVO.getOperation() + " " + value + " and relation_type = 'influence' ) ");
            }
        }
    }

    private void dealCaseVersion(QuickFilterValueVO quickFilterValueVO, String field, String value, String operation, StringBuilder sqlQuery) {
        if ("fix_version".equals(quickFilterValueVO.getFieldCode())) {
            dealFixVersion(quickFilterValueVO, field, value, operation, sqlQuery);
        } else if ("influence_version".equals(quickFilterValueVO.getFieldCode())) {
            dealInfluenceVersion(quickFilterValueVO, field, value, operation, sqlQuery);
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

    private String getSqlQuery(List<QuickFilterValueVO> quickFilterValueVOList, List<String> relationOperations, Boolean childIncluded) {
        StringBuilder sqlQuery = new StringBuilder();
        int idx = 0;
        for (QuickFilterValueVO quickFilterValueVO : quickFilterValueVOList) {
            String field = quickFilterFieldMapper.selectByPrimaryKey(quickFilterValueVO.getFieldCode()).getField();
            String value = "'null'".equals(quickFilterValueVO.getValue()) ? "null" : quickFilterValueVO.getValue();
            String operation = quickFilterValueVO.getOperation();
            switch (field) {
                case "component_id":
                    dealCaseComponent(field, value, operation, sqlQuery);
                    break;
                case "version_id":
                    dealCaseVersion(quickFilterValueVO, field, value, operation, sqlQuery);
                    break;
                case "label_id":
                    dealCaseLabel(field, value, operation, sqlQuery);
                    break;
                case "sprint_id":
                    dealCaseSprint(field, value, operation, sqlQuery);
                    break;
                case "creation_date":
                    sqlQuery.append(" unix_timestamp(" + field + ")" + " " + quickFilterValueVO.getOperation() + " " + "unix_timestamp('" + value + "') ");
                    break;
                case "last_update_date":
                    sqlQuery.append(" unix_timestamp(" + field + ")" + " " + quickFilterValueVO.getOperation() + " " + "unix_timestamp('" + value + "') ");
                    break;
                default:
                    sqlQuery.append(" " + field + " " + quickFilterValueVO.getOperation() + " " + value + " ");
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
    public QuickFilterVO create(Long projectId, QuickFilterVO quickFilterVO) {
        if (!projectId.equals(quickFilterVO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        if (checkName(projectId, quickFilterVO.getName())) {
            throw new CommonException("error.quickFilterName.exist");
        }
        String sqlQuery = getSqlQuery(quickFilterVO.getQuickFilterValueVOList(), quickFilterVO.getRelationOperations(), quickFilterVO.getChildIncluded());
        QuickFilterDTO quickFilterDTO = modelMapper.map(quickFilterVO, QuickFilterDTO.class);
        quickFilterDTO.setSqlQuery(sqlQuery);
        //设置编号
        Integer sequence = quickFilterMapper.queryMaxSequenceByProject(projectId);
        quickFilterDTO.setSequence(sequence == null ? 0 : sequence + 1);
        if (quickFilterMapper.insert(quickFilterDTO) != 1) {
            throw new CommonException("error.quickFilter.insert");
        }
        return modelMapper.map(quickFilterMapper.selectByPrimaryKey(quickFilterDTO.getFilterId()), QuickFilterVO.class);
    }

    private Boolean checkNameUpdate(Long projectId, Long filterId, String quickFilterName) {
        QuickFilterDTO quickFilterDTO = quickFilterMapper.selectByPrimaryKey(filterId);
        if (quickFilterName.equals(quickFilterDTO.getName())) {
            return false;
        }
        QuickFilterDTO check = new QuickFilterDTO();
        check.setProjectId(projectId);
        check.setName(quickFilterName);
        List<QuickFilterDTO> quickFilterDTOList = quickFilterMapper.select(check);
        return quickFilterDTOList != null && !quickFilterDTOList.isEmpty();
    }

    @Override
    public QuickFilterVO update(Long projectId, Long filterId, QuickFilterVO quickFilterVO) {
        if (!projectId.equals(quickFilterVO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        if (quickFilterVO.getName() != null && checkNameUpdate(projectId, filterId, quickFilterVO.getName())) {
            throw new CommonException("error.quickFilterName.exist");
        }
        String sqlQuery = getSqlQuery(quickFilterVO.getQuickFilterValueVOList(), quickFilterVO.getRelationOperations(), quickFilterVO.getChildIncluded());
        quickFilterVO.setFilterId(filterId);
        QuickFilterDTO quickFilterDTO = modelMapper.map(quickFilterVO, QuickFilterDTO.class);
        quickFilterDTO.setSqlQuery(sqlQuery);
        return updateBySelective(quickFilterDTO);
    }

    @Override
    public void deleteById(Long projectId, Long filterId) {
        QuickFilterDTO quickFilterDTO = quickFilterMapper.selectByPrimaryKey(filterId);
        if (quickFilterDTO == null) {
            throw new CommonException("error.quickFilter.get");
        }
        if (quickFilterMapper.deleteByPrimaryKey(filterId) != 1) {
            throw new CommonException("error.quickFilter.delete");
        }
    }

    @Override
    public QuickFilterVO queryById(Long projectId, Long filterId) {
        QuickFilterDTO quickFilterDTO = quickFilterMapper.selectByPrimaryKey(filterId);
        if (quickFilterDTO == null) {
            throw new CommonException("error.quickFilter.get");
        }
        return modelMapper.map(quickFilterDTO, QuickFilterVO.class);
    }

    @Override
    public List<QuickFilterVO> listByProjectId(Long projectId, QuickFilterSearchVO quickFilterSearchVO) {
        List<QuickFilterDTO> quickFilterDTOList = quickFilterMapper.queryFiltersByProjectId(projectId, quickFilterSearchVO.getFilterName(), quickFilterSearchVO.getContents());
        if (quickFilterDTOList != null && !quickFilterDTOList.isEmpty()) {
            return modelMapper.map(quickFilterDTOList, new TypeToken<List<QuickFilterVO>>(){}.getType());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public QuickFilterVO dragFilter(Long projectId, QuickFilterSequenceVO quickFilterSequenceVO) {
        if (quickFilterSequenceVO.getAfterSequence() == null && quickFilterSequenceVO.getBeforeSequence() == null) {
            throw new CommonException("error.dragFilter.noSequence");
        }
        QuickFilterDTO quickFilterDTO = modelMapper.map(quickFilterMapper.selectByPrimaryKey(quickFilterSequenceVO.getFilterId()), QuickFilterDTO.class);
        if (quickFilterDTO == null) {
            throw new CommonException(NOT_FOUND);
        } else {
            if (quickFilterSequenceVO.getAfterSequence() == null) {
                Integer maxSequence = quickFilterMapper.queryMaxAfterSequence(quickFilterSequenceVO.getBeforeSequence(), projectId);
                quickFilterSequenceVO.setAfterSequence(maxSequence);
            } else if (quickFilterSequenceVO.getBeforeSequence() == null) {
                Integer minSequence = quickFilterMapper.queryMinBeforeSequence(quickFilterSequenceVO.getAfterSequence(), projectId);
                quickFilterSequenceVO.setBeforeSequence(minSequence);
            }
            handleSequence(quickFilterSequenceVO, projectId, quickFilterDTO);
        }
        return modelMapper.map(quickFilterMapper.selectByPrimaryKey(quickFilterSequenceVO.getFilterId()), QuickFilterVO.class);

    }

    private void handleSequence(QuickFilterSequenceVO quickFilterSequenceVO, Long projectId, QuickFilterDTO quickFilterDTO) {
        if (quickFilterSequenceVO.getBeforeSequence() == null) {
            quickFilterDTO.setSequence(quickFilterSequenceVO.getAfterSequence() + 1);
            updateBySelective(quickFilterDTO);
        } else if (quickFilterSequenceVO.getAfterSequence() == null) {
            if (quickFilterDTO.getSequence() > quickFilterSequenceVO.getBeforeSequence()) {
                Integer add = quickFilterDTO.getSequence() - quickFilterSequenceVO.getBeforeSequence();
                if (add > 0) {
                    quickFilterDTO.setSequence(quickFilterSequenceVO.getBeforeSequence() - 1);
                    updateBySelective(quickFilterDTO);
                } else {
                    quickFilterMapper.batchUpdateSequence(quickFilterSequenceVO.getBeforeSequence(), projectId,
                            quickFilterDTO.getSequence() - quickFilterSequenceVO.getBeforeSequence() + 1, quickFilterDTO.getFilterId());
                }
            }
        } else {
            Integer sequence = quickFilterSequenceVO.getAfterSequence() + 1;
            quickFilterDTO.setSequence(sequence);
            updateBySelective(quickFilterDTO);
            Integer update = sequence - quickFilterSequenceVO.getBeforeSequence();
            if (update >= 0) {
                quickFilterMapper.batchUpdateSequence(quickFilterSequenceVO.getBeforeSequence(), projectId, update + 1, quickFilterDTO.getFilterId());
            }
        }
    }

    @Override
    public Boolean checkName(Long projectId, String quickFilterName) {
        QuickFilterDTO quickFilterDTO = new QuickFilterDTO();
        quickFilterDTO.setProjectId(projectId);
        quickFilterDTO.setName(quickFilterName);
        List<QuickFilterDTO> quickFilterDTOList = quickFilterMapper.select(quickFilterDTO);
        return quickFilterDTOList != null && !quickFilterDTOList.isEmpty();
    }

    public QuickFilterVO updateBySelective(QuickFilterDTO quickFilterDTO) {
        if (quickFilterMapper.updateByPrimaryKeySelective(quickFilterDTO) != 1) {
            throw new CommonException("error.quickFilter.update");
        }
        return modelMapper.map(quickFilterMapper.selectByPrimaryKey(quickFilterDTO.getFilterId()), QuickFilterVO.class);
    }

}
