package io.choerodon.agile.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.infra.dataobject.LookupTypeWithValuesDTO;
import io.choerodon.agile.infra.dataobject.LookupValueDTO;
import io.choerodon.agile.infra.dataobject.ObjectSchemeDTO;
import io.choerodon.agile.infra.dataobject.ObjectSchemeFieldDTO;
import io.choerodon.agile.infra.enums.*;
import io.choerodon.agile.infra.mapper.LookupValueMapper;
import io.choerodon.agile.infra.mapper.ObjectSchemeFieldMapper;
import io.choerodon.agile.infra.mapper.ObjectSchemeMapper;
import io.choerodon.agile.infra.utils.EnumUtil;
import io.choerodon.agile.infra.utils.FieldValueUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ObjectSchemeFieldServiceImpl implements ObjectSchemeFieldService {
    private static final String ERROR_FIELD_ILLEGAL = "error.field.illegal";
    private static final String ERROR_FIELD_CREATE = "error.field.create";
    private static final String ERROR_FIELD_DELETE = "error.field.delete";
    private static final String ERROR_FIELD_NOTFOUND = "error.field.notFound";
    private static final String ERROR_FIELD_UPDATE = "error.field.update";
    private static final String ERROR_SCHEMECODE_ILLEGAL = "error.schemeCode.illegal";
    private static final String ERROR_CONTEXT_ILLEGAL = "error.context.illegal";
    private static final String ERROR_FIELDTYPE_ILLEGAL = "error.fieldType.illegal";
    private static final String ERROR_FIELD_NAMEEXIST = "error.field.nameExist";
    private static final String ERROR_FIELD_CODEEXIST = "error.field.codeExist";
    private static final String ERROR_FIELD_REQUIRED_NEED_DEFAULT_VALUE = "error.field.requiredNeedDefaultValue";
    @Autowired
    private ObjectSchemeFieldMapper objectSchemeFieldMapper;
    @Autowired
    private ObjectSchemeMapper objectSchemeMapper;
    @Autowired
    private FieldOptionService fieldOptionService;
    @Autowired
    private PageFieldService pageFieldService;
    @Autowired
    private FieldValueService fieldValueService;
    @Autowired
    private LookupValueMapper lookupValueMapper;
    @Autowired
    private FieldDataLogService fieldDataLogService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ObjectSchemeFieldDTO baseCreate(ObjectSchemeFieldDTO field) {
        field.setSystem(false);
        field.setRequired(false);
        if (objectSchemeFieldMapper.insert(field) != 1) {
            throw new CommonException(ERROR_FIELD_CREATE);
        }
        return objectSchemeFieldMapper.selectByPrimaryKey(field.getId());
    }

    @Override
    public void baseDelete(Long fieldId) {
        if (objectSchemeFieldMapper.deleteByPrimaryKey(fieldId) != 1) {
            throw new CommonException(ERROR_FIELD_DELETE);
        }
    }

    @Override
    public void baseUpdate(ObjectSchemeFieldDTO field) {
        if (objectSchemeFieldMapper.updateByPrimaryKeySelective(field) != 1) {
            throw new CommonException(ERROR_FIELD_UPDATE);
        }
    }

    @Override
    public ObjectSchemeFieldDTO baseQueryById(Long organizationId, Long projectId, Long fieldId) {
        ObjectSchemeFieldDTO field = objectSchemeFieldMapper.queryById(fieldId);
        if (field == null) {
            throw new CommonException(ERROR_FIELD_NOTFOUND);
        }
        if (!field.getOrganizationId().equals(organizationId) && !field.getOrganizationId().equals(0L)) {
            throw new CommonException(ERROR_FIELD_ILLEGAL);
        }
        if (field.getProjectId() != null && !field.getProjectId().equals(projectId) && !field.getProjectId().equals(0L)) {
            throw new CommonException(ERROR_FIELD_ILLEGAL);
        }
        return field;
    }

    @Override
    public List<ObjectSchemeFieldDTO> listQuery(Long organizationId, Long projectId, ObjectSchemeFieldSearchVO searchDTO) {
        List<ObjectSchemeFieldDTO> fields = objectSchemeFieldMapper.listQuery(organizationId, projectId, searchDTO);
        return FieldCode.objectSchemeFieldsFilter(organizationId, projectId, fields);
    }

    @Override
    public ObjectSchemeFieldDTO queryByFieldCode(Long organizationId, Long projectId, String fieldCode) {
        return objectSchemeFieldMapper.queryByFieldCode(organizationId, projectId, fieldCode);
    }

    @Override
    public Map<String, Object> listQuery(Long organizationId, Long projectId, String schemeCode) {
        Map<String, Object> result = new HashMap<>(2);
        if (!EnumUtil.contain(ObjectSchemeCode.class, schemeCode)) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        ObjectSchemeFieldSearchVO searchDTO = new ObjectSchemeFieldSearchVO();
        searchDTO.setSchemeCode(schemeCode);
        List<ObjectSchemeFieldVO> fieldDTOS = modelMapper.map(listQuery(organizationId, projectId, searchDTO), new TypeToken<List<ObjectSchemeFieldVO>>() {
        }.getType());
        fillContextName(fieldDTOS);
        ObjectSchemeDTO select = new ObjectSchemeDTO();
        select.setSchemeCode(schemeCode);
        result.put("name", objectSchemeMapper.selectOne(select).getName());
        result.put("content", fieldDTOS);
        return result;
    }

    /**
     * 填充contextName
     *
     * @param fieldDTOS
     */
    private void fillContextName(List<ObjectSchemeFieldVO> fieldDTOS) {
        LookupTypeWithValuesDTO typeWithValues = lookupValueMapper.queryLookupValueByCode(LookupType.CONTEXT);
        Map<String, String> codeMap = typeWithValues.getLookupValues().stream().collect(Collectors.toMap(LookupValueDTO::getValueCode, LookupValueDTO::getName));
        for (ObjectSchemeFieldVO fieldDTO : fieldDTOS) {
            String[] contextCodes = fieldDTO.getContext().split(",");
            List<String> contextNames = new ArrayList<>(contextCodes.length);
            for (String contextCode : contextCodes) {
                contextNames.add(codeMap.get(contextCode));
            }
            fieldDTO.setContextName(contextNames.stream().collect(Collectors.joining(",")));
        }
    }

    @Override
    public ObjectSchemeFieldDetailVO create(Long organizationId, Long projectId, ObjectSchemeFieldCreateVO fieldCreateDTO) {
        if (!EnumUtil.contain(FieldType.class, fieldCreateDTO.getFieldType())) {
            throw new CommonException(ERROR_FIELDTYPE_ILLEGAL);
        }
        if (checkName(organizationId, projectId, fieldCreateDTO.getName(), fieldCreateDTO.getSchemeCode())) {
            throw new CommonException(ERROR_FIELD_NAMEEXIST);
        }
        if (checkCode(organizationId, projectId, fieldCreateDTO.getCode(), fieldCreateDTO.getSchemeCode())) {
            throw new CommonException(ERROR_FIELD_CODEEXIST);
        }
        for (String context : fieldCreateDTO.getContext()) {
            if (!EnumUtil.contain(ObjectSchemeFieldContext.class, context)) {
                throw new CommonException(ERROR_CONTEXT_ILLEGAL);
            }
        }
        ObjectSchemeFieldDTO field = modelMapper.map(fieldCreateDTO, ObjectSchemeFieldDTO.class);
        field.setContext(Arrays.asList(fieldCreateDTO.getContext()).stream().collect(Collectors.joining(",")));
        field.setOrganizationId(organizationId);
        field.setProjectId(projectId);
        baseCreate(field);
        //创建pageField
        if (projectId != null) {
            pageFieldService.createByFieldWithPro(organizationId, projectId, field);
        } else {
            pageFieldService.createByFieldWithOrg(organizationId, field);
        }

        return queryById(organizationId, projectId, field.getId());
    }

    @Override
    public ObjectSchemeFieldDetailVO queryById(Long organizationId, Long projectId, Long fieldId) {
        ObjectSchemeFieldDTO field = baseQueryById(organizationId, projectId, fieldId);
        ObjectSchemeFieldDetailVO fieldDetailDTO = modelMapper.map(field, ObjectSchemeFieldDetailVO.class);
        fieldDetailDTO.setContext(field.getContext().split(","));
        //获取字段选项，并设置默认值
        List<FieldOptionVO> fieldOptions = fieldOptionService.queryByFieldId(organizationId, fieldId);
        if (!fieldOptions.isEmpty() && field.getDefaultValue() != null) {
            List<String> defaultIds = Arrays.asList(field.getDefaultValue().split(","));
            fieldOptions.forEach(fieldOption -> {
                if (defaultIds.contains(fieldOption.getId().toString())) {
                    fieldOption.setIsDefault(true);
                } else {
                    fieldOption.setIsDefault(false);
                }
            });
            fieldDetailDTO.setFieldOptions(fieldOptions);
        }
        FieldValueUtil.handleDefaultValue(fieldDetailDTO);
        return fieldDetailDTO;
    }

    @Override
    public void delete(Long organizationId, Long projectId, Long fieldId) {
        ObjectSchemeFieldDTO field = baseQueryById(organizationId, projectId, fieldId);
        //组织层无法删除项目层
        if (projectId == null && field.getProjectId() != null) {
            throw new CommonException(ERROR_FIELD_ILLEGAL);
        }
        //项目层无法删除组织层
        if (projectId != null && field.getProjectId() == null) {
            throw new CommonException(ERROR_FIELD_ILLEGAL);
        }
        //无法删除系统字段
        if (field.getSystem()) {
            throw new CommonException(ERROR_FIELD_ILLEGAL);
        }
        baseDelete(fieldId);
        //删除pageFields
        pageFieldService.deleteByFieldId(fieldId);
        //删除字段值
        fieldValueService.deleteByFieldId(fieldId);
        //删除日志
        fieldDataLogService.deleteByFieldId(projectId, fieldId);
    }

    @Override
    public ObjectSchemeFieldDetailVO update(Long organizationId, Long projectId, Long fieldId, ObjectSchemeFieldUpdateVO updateDTO) {
        //处理字段选项
        if (updateDTO.getFieldOptions() != null) {
            String defaultIds = fieldOptionService.handleFieldOption(organizationId, fieldId, updateDTO.getFieldOptions());
            if (defaultIds != null && !"".equals(defaultIds)) {
                updateDTO.setDefaultValue(defaultIds);
            }
        }
        ObjectSchemeFieldDTO field = baseQueryById(organizationId, projectId, fieldId);
        if (field.getRequired() && "".equals(updateDTO.getDefaultValue())) {
            throw new CommonException(ERROR_FIELD_REQUIRED_NEED_DEFAULT_VALUE);
        }
        ObjectSchemeFieldDTO update = modelMapper.map(updateDTO, ObjectSchemeFieldDTO.class);
        //处理context
        String[] contexts = updateDTO.getContext();
        if (contexts != null && contexts.length != 0) {
            for (String context : contexts) {
                if (!EnumUtil.contain(ObjectSchemeFieldContext.class, context)) {
                    throw new CommonException(ERROR_CONTEXT_ILLEGAL);
                }
            }
            update.setContext(Arrays.asList(contexts).stream().collect(Collectors.joining(",")));
        }
        update.setId(fieldId);
        baseUpdate(update);
        return queryById(organizationId, projectId, fieldId);
    }

    @Override
    public Boolean checkName(Long organizationId, Long projectId, String name, String schemeCode) {
        if (!EnumUtil.contain(ObjectSchemeCode.class, schemeCode)) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        ObjectSchemeFieldSearchVO search = new ObjectSchemeFieldSearchVO();
        search.setName(name);
        search.setSchemeCode(schemeCode);
        return !listQuery(organizationId, projectId, search).isEmpty();
    }

    @Override
    public Boolean checkCode(Long organizationId, Long projectId, String code, String schemeCode) {
        if (!EnumUtil.contain(ObjectSchemeCode.class, schemeCode)) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        ObjectSchemeFieldSearchVO search = new ObjectSchemeFieldSearchVO();
        search.setCode(code);
        search.setSchemeCode(schemeCode);
        return !listQuery(organizationId, projectId, search).isEmpty();
    }

    @Override
    public List<AgileIssueHeadVO> getIssueHeadForAgile(Long organizationId, Long projectId, String schemeCode) {
        if (!EnumUtil.contain(ObjectSchemeCode.class, schemeCode)) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        ObjectSchemeFieldSearchVO searchDTO = new ObjectSchemeFieldSearchVO();
        searchDTO.setSchemeCode(schemeCode);
        List<ObjectSchemeFieldDTO> objectSchemeFields = listQuery(organizationId, projectId, searchDTO)
                .stream().filter(objectSchemeField -> !objectSchemeField.getSystem()).collect(Collectors.toList());
        List<AgileIssueHeadVO> agileIssueHeadDTOS = new ArrayList<>();
        objectSchemeFields.forEach(objectSchemeField -> {
            AgileIssueHeadVO agileIssueHeadDTO = new AgileIssueHeadVO();
            agileIssueHeadDTO.setTitle(objectSchemeField.getName());
            agileIssueHeadDTO.setCode(objectSchemeField.getCode());
            agileIssueHeadDTO.setSortId(objectSchemeField.getCode());
            agileIssueHeadDTO.setFieldType(objectSchemeField.getFieldType());
            agileIssueHeadDTOS.add(agileIssueHeadDTO);
        });
        return agileIssueHeadDTOS;
    }
}
