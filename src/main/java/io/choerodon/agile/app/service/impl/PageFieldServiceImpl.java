package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.infra.dataobject.ObjectSchemeFieldDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.FieldOptionService;
import io.choerodon.agile.app.service.FieldValueService;
import io.choerodon.agile.app.service.PageFieldService;
import io.choerodon.agile.infra.annotation.CopyPageField;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.enums.*;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.utils.EnumUtil;
import io.choerodon.agile.infra.utils.FieldValueUtil;
import io.choerodon.agile.infra.utils.RankUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PageFieldServiceImpl implements PageFieldService {

    private static final String ERROR_PAGECODE_ILLEGAL = "error.pageCode.illegal";
    private static final String ERROR_CONTEXT_ILLEGAL = "error.context.illegal";
    private static final String ERROR_SCHEMECODE_ILLEGAL = "error.schemeCode.illegal";
    private static final String ERROR_FIELDCODE_ILLEGAL = "error.fieldCode.illegal";
    private static final String ERROR_PAGEFIELD_CREATE = "error.pageField.create";
    private static final String ERROR_PAGEFIELD_DELETE = "error.pageField.delete";
    private static final String ERROR_PAGEFIELD_NOTFOUND = "error.pageField.notFound";
    private static final String ERROR_PAGEFIELD_UPDATE = "error.pageField.update";

    @Autowired
    private PageFieldMapper pageFieldMapper;
    @Autowired
    private PageMapper pageMapper;
    @Autowired
    private ObjectSchemeFieldMapper objectSchemeFieldMapper;
    @Autowired
    private ProjectPageFieldMapper projectPageFieldMapper;
    @Autowired
    private FieldOptionService optionService;
    @Autowired
    private FieldValueService fieldValueService;
    @Autowired
    private LookupValueMapper lookupValueMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PageFieldDTO baseCreate(PageFieldDTO field) {
        if (pageFieldMapper.insert(field) != 1) {
            throw new CommonException(ERROR_PAGEFIELD_CREATE);
        }
        return pageFieldMapper.selectByPrimaryKey(field.getId());
    }

    @Override
    public void baseDelete(Long fieldId) {
        if (pageFieldMapper.deleteByPrimaryKey(fieldId) != 1) {
            throw new CommonException(ERROR_PAGEFIELD_DELETE);
        }
    }

    @Override
    public void baseUpdate(PageFieldDTO pageField) {
        if (pageFieldMapper.updateByPrimaryKeySelective(pageField) != 1) {
            throw new CommonException(ERROR_PAGEFIELD_UPDATE);
        }
    }

    @Override
    public PageFieldDTO baseQueryById(Long organizationId, Long projectId, Long pageFieldId) {
        PageFieldDTO pageField = pageFieldMapper.selectByPrimaryKey(pageFieldId);
        if (pageField == null) {
            throw new CommonException(ERROR_PAGEFIELD_NOTFOUND);
        }
        return pageField;
    }

    @Override
    public Map<String, Object> listQuery(Long organizationId, Long projectId, String pageCode, String context) {
        Map<String, Object> result = new HashMap<>(2);
        if (!EnumUtil.contain(PageCode.class, pageCode)) {
            throw new CommonException(ERROR_PAGECODE_ILLEGAL);
        }
        if (context != null && !EnumUtil.contain(ObjectSchemeFieldContext.class, context)) {
            throw new CommonException(ERROR_CONTEXT_ILLEGAL);
        }
        List<PageFieldDTO> pageFields = queryPageField(organizationId, projectId, pageCode, context);
        List<PageFieldVO> pageFieldVOS = modelMapper.map(pageFields, new TypeToken<List<PageFieldVO>>() {
        }.getType());
        fillContextName(pageFieldVOS);
        PageDTO select = new PageDTO();
        select.setPageCode(pageCode);
        result.put("name", pageMapper.selectOne(select).getName());
        result.put("content", pageFieldVOS);
        return result;
    }

    /**
     * 填充contextName
     *
     * @param pageFieldVOS
     */
    private void fillContextName(List<PageFieldVO> pageFieldVOS) {
        LookupTypeWithValuesDTO typeWithValues = lookupValueMapper.queryLookupValueByCode(LookupType.CONTEXT);
        Map<String, String> codeMap = typeWithValues.getLookupValues().stream().collect(Collectors.toMap(LookupValueDTO::getValueCode, LookupValueDTO::getName));
        for (PageFieldVO pageFieldVO : pageFieldVOS) {
            String[] contextCodes = pageFieldVO.getContext().split(",");
            List<String> contextNames = new ArrayList<>(contextCodes.length);
            for (String contextCode : contextCodes) {
                contextNames.add(codeMap.get(contextCode));
            }
            pageFieldVO.setContextName(contextNames.stream().collect(Collectors.joining(",")));
        }
    }

    /**
     * 若没有项目层配置则获取组织层配置
     *
     * @param organizationId
     * @param projectId
     * @param pageCode
     * @return
     */
    @Override
    public List<PageFieldDTO> queryPageField(Long organizationId, Long projectId, String pageCode, String context) {
        List<PageFieldDTO> pageFields;
        if (projectId != null && projectPageFieldMapper.queryOne(organizationId, projectId) != null) {
            pageFields = pageFieldMapper.listQuery(organizationId, projectId, pageCode, context);
        } else {
            pageFields = pageFieldMapper.listQuery(organizationId, null, pageCode, context);
        }
        //若没有数据则初始化【修复旧数据】
        if (pageFields.isEmpty()) {
            initPageFieldByOrg(organizationId);
            if (projectId != null && projectPageFieldMapper.queryOne(organizationId, projectId) != null) {
                pageFields = pageFieldMapper.listQuery(organizationId, projectId, pageCode, context);
            } else {
                pageFields = pageFieldMapper.listQuery(organizationId, null, pageCode, context);
            }
        }
        return FieldCode.pageFieldsFilter(organizationId, projectId, pageFields);
    }

    @Override
    @CopyPageField
    public PageFieldVO adjustFieldOrder(Long organizationId, Long projectId, String pageCode, AdjustOrderVO adjustOrder) {
        if (!EnumUtil.contain(PageCode.class, pageCode)) {
            throw new CommonException(ERROR_PAGECODE_ILLEGAL);
        }
        PageFieldDTO current = pageFieldMapper.queryByFieldId(organizationId, projectId, pageCode, adjustOrder.getCurrentFieldId());
        PageFieldDTO outset = pageFieldMapper.queryByFieldId(organizationId, projectId, pageCode, adjustOrder.getOutsetFieldId());
        PageFieldDTO update = new PageFieldDTO();
        update.setId(current.getId());
        update.setObjectVersionNumber(current.getObjectVersionNumber());
        if (adjustOrder.getBefore()) {
            update.setRank(RankUtil.genNext(outset.getRank()));
        } else {
            String rightRank = pageFieldMapper.queryRightRank(organizationId, projectId, pageCode, outset.getRank());
            if (rightRank == null) {
                update.setRank(RankUtil.genPre(outset.getRank()));
            } else {
                update.setRank(RankUtil.between(outset.getRank(), rightRank));
            }
        }
        baseUpdate(update);
        return modelMapper.map(pageFieldMapper.queryByFieldId(organizationId, projectId, pageCode, current.getFieldId()), PageFieldVO.class);
    }

    @Override
    @CopyPageField
    public PageFieldVO update(Long organizationId, Long projectId, String pageCode, Long fieldId, PageFieldUpdateVO updateDTO) {
        if (!EnumUtil.contain(PageCode.class, pageCode)) {
            throw new CommonException(ERROR_PAGECODE_ILLEGAL);
        }
        PageFieldDTO field = pageFieldMapper.queryByFieldId(organizationId, projectId, pageCode, fieldId);
        PageFieldDTO update = modelMapper.map(updateDTO, PageFieldDTO.class);
        update.setId(field.getId());
        baseUpdate(update);
        return modelMapper.map(pageFieldMapper.queryByFieldId(organizationId, projectId, pageCode, fieldId), PageFieldVO.class);
    }

    @Override
    public synchronized void initPageFieldByOrg(Long organizationId) {
        if (pageFieldMapper.listQuery(organizationId, null, null, null).isEmpty()) {
            //查询page
            List<PageDTO> pages = pageMapper.fulltextSearch(organizationId, new PageSearchVO());
            Map<String, Long> pageMap = pages.stream().collect(Collectors.toMap(PageDTO::getPageCode, PageDTO::getId));
            //查询field
            List<ObjectSchemeFieldDTO> fields = objectSchemeFieldMapper.listQuery(organizationId, null, new ObjectSchemeFieldSearchVO());
            Map<String, Map<String, Long>> schemeCodeFieldMap = fields.stream().collect(Collectors.groupingBy(ObjectSchemeFieldDTO::getSchemeCode, Collectors.toMap(ObjectSchemeFieldDTO::getCode, ObjectSchemeFieldDTO::getId)));
            handleInitPageFieldE(organizationId, schemeCodeFieldMap, pageMap);
        }
    }

    private void handleInitPageFieldE(Long organizationId, Map<String, Map<String, Long>> schemeCodeFieldMap, Map<String, Long> pageMap) {
        Class[] clzes = InitPageFieldE.class.getClasses();
        Arrays.asList(clzes).forEach(cls -> {
            List<InitPageFieldVO> initPageFields = modelMapper.map(Arrays.asList(cls.getEnumConstants()), new TypeToken<List<InitPageFieldVO>>() {
            }.getType());
            String rank = RankUtil.mid();
            for (InitPageFieldVO pageField : initPageFields) {
                Map<String, Long> fieldMap = schemeCodeFieldMap.get(pageField.getSchemeCode());
                if (fieldMap == null) {
                    throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
                }
                Long fieldId = fieldMap.get(pageField.getFieldCode());
                if (fieldId == null) {
                    throw new CommonException(ERROR_FIELDCODE_ILLEGAL);
                }
                pageField.setFieldId(fieldId);
                Long pageId = pageMap.get(pageField.getPageCode());
                if (pageId == null) {
                    throw new CommonException(ERROR_PAGECODE_ILLEGAL);
                }
                pageField.setPageId(pageId);
                //设置rank
                pageField.setRank(rank);
                rank = RankUtil.genPre(rank);
            }
            List<PageFieldDTO> pageFields = modelMapper.map(initPageFields, new TypeToken<List<PageFieldDTO>>() {
            }.getType());
            pageFieldMapper.batchInsert(organizationId, null, pageFields);
        });
    }

    @Override
    @CopyPageField
    public void createByFieldWithPro(Long organizationId, Long projectId, ObjectSchemeFieldDTO field) {
        //查询page
        PageSearchVO searchDTO = new PageSearchVO();
        searchDTO.setSchemeCode(field.getSchemeCode());
        List<PageDTO> pages = pageMapper.fulltextSearch(organizationId, searchDTO);
        pages.forEach(page -> {
            //创建pageField
            PageFieldDTO pageField = new PageFieldDTO();
            pageField.setProjectId(projectId);
            pageField.setOrganizationId(organizationId);
            pageField.setDisplay(false);
            pageField.setFieldId(field.getId());
            pageField.setPageId(page.getId());
            String minRank = pageFieldMapper.queryMinRank(organizationId, projectId, page.getPageCode());
            //若没有数据则初始化【修复旧数据】
            if (minRank == null) {
                initPageFieldByOrg(organizationId);
                minRank = pageFieldMapper.queryMinRank(organizationId, projectId, page.getPageCode());
            }
            pageField.setRank(RankUtil.genPre(minRank));
            baseCreate(pageField);
        });
    }

    @Override
    public void createByFieldWithOrg(Long organizationId, ObjectSchemeFieldDTO field) {
        //项目层自定义同样需要创建字段
        List<ProjectPageFieldDTO> projectPageFields = projectPageFieldMapper.queryByOrgId(organizationId);
        //查询page
        PageSearchVO searchDTO = new PageSearchVO();
        searchDTO.setSchemeCode(field.getSchemeCode());
        List<PageDTO> pages = pageMapper.fulltextSearch(organizationId, searchDTO);
        pages.forEach(page -> {
            //组织层创建pageField
            PageFieldDTO pageField = new PageFieldDTO();
            pageField.setOrganizationId(organizationId);
            pageField.setDisplay(false);
            pageField.setFieldId(field.getId());
            pageField.setPageId(page.getId());
            String minRank = pageFieldMapper.queryMinRank(organizationId, null, page.getPageCode());
            //若没有数据则初始化【修复旧数据】
            if (minRank == null) {
                initPageFieldByOrg(organizationId);
                minRank = pageFieldMapper.queryMinRank(organizationId, null, page.getPageCode());
            }
            pageField.setRank(RankUtil.genPre(minRank));
            baseCreate(pageField);
            //项目层创建pageField
            projectPageFields.forEach(projectPageField -> {
                pageField.setId(null);
                pageField.setProjectId(projectPageField.getProjectId());
                baseCreate(pageField);
            });
        });
    }

    @Override
    public void deleteByFieldId(Long fieldId) {
        pageFieldMapper.deleteByFieldId(fieldId);
    }

    @Override
    public List<PageFieldViewVO> queryPageFieldViewList(Long organizationId, Long projectId, PageFieldViewParamVO paramDTO) {
        if (!EnumUtil.contain(PageCode.class, paramDTO.getPageCode())) {
            throw new CommonException(ERROR_PAGECODE_ILLEGAL);
        }
        if (!EnumUtil.contain(ObjectSchemeCode.class, paramDTO.getSchemeCode())) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        if (!EnumUtil.contain(ObjectSchemeFieldContext.class, paramDTO.getContext())) {
            throw new CommonException(ERROR_CONTEXT_ILLEGAL);
        }
        List<PageFieldDTO> pageFields = queryPageField(organizationId, projectId, paramDTO.getPageCode(), paramDTO.getContext());
        //modelMapper设置严格匹配策略
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        pageFields = pageFields.stream().filter(PageFieldDTO::getDisplay).collect(Collectors.toList());
        List<PageFieldViewVO> pageFieldViews = modelMapper.map(pageFields, new TypeToken<List<PageFieldViewVO>>() {
        }.getType());
        //填充option
        optionService.fillOptions(organizationId, projectId, pageFieldViews);
        FieldValueUtil.handleDefaultValue(pageFieldViews);
        return pageFieldViews;
    }

    @Override
    public List<PageFieldViewVO> queryPageFieldViewListWithInstanceId(Long organizationId, Long projectId, Long instanceId, PageFieldViewParamVO paramDTO) {
        List<PageFieldViewVO> pageFieldViews = queryPageFieldViewList(organizationId, projectId, paramDTO);
        //填充value
        fieldValueService.fillValues(organizationId, projectId, instanceId, paramDTO.getSchemeCode(), pageFieldViews);
        return pageFieldViews;
    }

    @Override
    public Map<Long, Map<String, String>> queryFieldValueWithIssueIdsForAgileExport(Long organizationId, Long projectId, List<Long> instanceIds) {
        Map<Long, Map<String, String>> result = new HashMap<>();
        instanceIds.forEach(instanceId -> result.put(instanceId, fieldValueService
                .queryFieldValueMapWithInstanceId(organizationId, projectId, instanceId)));
        return result;
    }
}
