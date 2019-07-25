package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.FieldValueService;
import io.choerodon.agile.app.service.ObjectSchemeFieldService;
import io.choerodon.agile.app.service.PageFieldService;
import io.choerodon.agile.infra.dataobject.FieldValueDTO;
import io.choerodon.agile.infra.dataobject.ObjectSchemeFieldDTO;
import io.choerodon.agile.infra.dataobject.PageFieldDTO;
import io.choerodon.agile.infra.dataobject.UserDTO;
import io.choerodon.agile.infra.enums.FieldType;
import io.choerodon.agile.infra.enums.ObjectSchemeCode;
import io.choerodon.agile.infra.enums.ObjectSchemeFieldContext;
import io.choerodon.agile.infra.enums.PageCode;
import io.choerodon.agile.infra.mapper.FieldValueMapper;
import io.choerodon.agile.infra.utils.EnumUtil;
import io.choerodon.agile.infra.utils.FieldValueUtil;
import io.choerodon.agile.infra.utils.PageUtil;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @since 2019/4/8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FieldValueServiceImpl implements FieldValueService {
    private static final String ERROR_PAGECODE_ILLEGAL = "error.pageCode.illegal";
    private static final String ERROR_CONTEXT_ILLEGAL = "error.context.illegal";
    private static final String ERROR_SCHEMECODE_ILLEGAL = "error.schemeCode.illegal";
    private static final String ERROR_OPTION_ILLEGAL = "error.option.illegal";
    private static final String ERROR_FIELDTYPE_ILLEGAL = "error.fieldType.illegal";
    private static final String ERROR_SYSTEM_ILLEGAL = "error.system.illegal";

    @Autowired
    private FieldValueMapper fieldValueMapper;
    @Autowired
    private PageFieldService pageFieldService;
    @Autowired
    private ObjectSchemeFieldService objectSchemeFieldService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void fillValues(Long organizationId, Long projectId, Long instanceId, String schemeCode, List<PageFieldViewVO> pageFieldViews) {
        List<FieldValueVO> values = modelMapper.map(fieldValueMapper.queryList(projectId, instanceId, schemeCode, null), new TypeToken<List<FieldValueVO>>() {
        }.getType());
        Map<Long, UserDTO> userMap = FieldValueUtil.handleUserMap(values.stream().filter(x -> x.getFieldType().equals(FieldType.MEMBER)).map(FieldValueVO::getOptionId).collect(Collectors.toList()));
        Map<Long, List<FieldValueVO>> valueGroup = values.stream().collect(Collectors.groupingBy(FieldValueVO::getFieldId));
        pageFieldViews.forEach(view -> {
            List<FieldValueVO> fieldValues = valueGroup.get(view.getFieldId());
            FieldValueUtil.handleDTO2Value(view, view.getFieldType(), fieldValues, userMap, false);
        });
    }

    @Override
    public void createFieldValues(Long organizationId, Long projectId, Long instanceId, String schemeCode, List<PageFieldViewCreateVO> createDTOs) {
        if (!EnumUtil.contain(ObjectSchemeCode.class, schemeCode)) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        List<FieldValueDTO> fieldValues = new ArrayList<>();
        createDTOs.forEach(createDTO -> {
            List<FieldValueDTO> values = new ArrayList<>();
            FieldValueUtil.handleValue2DTO(values, createDTO.getFieldType(), createDTO.getValue());
            //校验
            ObjectSchemeFieldDTO field = objectSchemeFieldService.baseQueryById(organizationId, projectId, createDTO.getFieldId());
            if (field.getSystem()) {
                throw new CommonException(ERROR_SYSTEM_ILLEGAL);
            }
            values.forEach(value -> value.setFieldId(createDTO.getFieldId()));
            fieldValues.addAll(values);
        });
        if (!fieldValues.isEmpty()) {
            fieldValueMapper.batchInsert(projectId, instanceId, schemeCode, fieldValues);
        }
    }

    @Override
    public List<FieldValueVO> updateFieldValue(Long organizationId, Long projectId, Long instanceId, Long fieldId, String schemeCode, PageFieldViewUpdateVO updateDTO) {
        if (!EnumUtil.contain(ObjectSchemeCode.class, schemeCode)) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        if (!EnumUtil.contain(FieldType.class, updateDTO.getFieldType())) {
            throw new CommonException(ERROR_FIELDTYPE_ILLEGAL);
        }
        //校验
        ObjectSchemeFieldDTO field = objectSchemeFieldService.baseQueryById(organizationId, projectId, fieldId);
        //获取原fieldValue
        List<FieldValueDTO> oldFieldValues = fieldValueMapper.queryList(projectId, instanceId, schemeCode, fieldId);
        //删除原fieldValue
        if (!oldFieldValues.isEmpty()) {
            fieldValueMapper.deleteList(projectId, instanceId, schemeCode, fieldId);
        }
        //创建新fieldValue
        List<FieldValueDTO> newFieldValues = new ArrayList<>();
        FieldValueUtil.handleValue2DTO(newFieldValues, updateDTO.getFieldType(), updateDTO.getValue());
        newFieldValues.forEach(fieldValue -> fieldValue.setFieldId(fieldId));
        if (!newFieldValues.isEmpty()) {
            fieldValueMapper.batchInsert(projectId, instanceId, schemeCode, newFieldValues);
        }
        //处理字段日志
        FieldValueUtil.handleDataLog(organizationId, projectId, instanceId, fieldId, updateDTO.getFieldType(), schemeCode, oldFieldValues, newFieldValues);
        return modelMapper.map(fieldValueMapper.queryList(projectId, instanceId, schemeCode, fieldId), new TypeToken<List<FieldValueVO>>() {
        }.getType());
    }

    @Override
    public void deleteByOptionIds(Long fieldId, List<Long> optionIds) {
        if (!optionIds.isEmpty()) {
            for (Long optionId : optionIds) {
                if (optionId == null) {
                    throw new CommonException(ERROR_OPTION_ILLEGAL);
                }
            }
            fieldValueMapper.deleteByOptionIds(fieldId, optionIds);
        }
    }

    @Override
    public void deleteByFieldId(Long fieldId) {
        FieldValueDTO delete = new FieldValueDTO();
        delete.setFieldId(fieldId);
        fieldValueMapper.delete(delete);
    }

    @Override
    public void createFieldValuesWithQuickCreate(Long organizationId, Long projectId, Long instanceId, PageFieldViewParamVO paramDTO) {
        if (!EnumUtil.contain(PageCode.class, paramDTO.getPageCode())) {
            throw new CommonException(ERROR_PAGECODE_ILLEGAL);
        }
        if (!EnumUtil.contain(ObjectSchemeCode.class, paramDTO.getSchemeCode())) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        if (!EnumUtil.contain(ObjectSchemeFieldContext.class, paramDTO.getContext())) {
            throw new CommonException(ERROR_CONTEXT_ILLEGAL);
        }
        List<PageFieldDTO> pageFields = pageFieldService.queryPageField(organizationId, projectId, paramDTO.getPageCode(), paramDTO.getContext());
        //过滤掉不显示字段和系统字段
        pageFields = pageFields.stream().filter(PageFieldDTO::getDisplay).filter(x -> !x.getSystem()).collect(Collectors.toList());
        List<FieldValueDTO> fieldValues = new ArrayList<>();
        pageFields.forEach(create -> {
            List<FieldValueDTO> values = new ArrayList<>();
            //处理默认值
            FieldValueUtil.handleDefaultValue2DTO(values, create);
            values.forEach(value -> value.setFieldId(create.getFieldId()));
            fieldValues.addAll(values);
        });
        if (!fieldValues.isEmpty()) {
            fieldValueMapper.batchInsert(projectId, instanceId, paramDTO.getSchemeCode(), fieldValues);
        }
    }

    @Override
    public Map<String, String> queryFieldValueMapWithInstanceId(Long organizationId, Long projectId, Long instanceId) {
        Map<String, String> result = new HashMap<>();
        List<FieldValueVO> values = modelMapper.map(fieldValueMapper
                        .queryList(projectId, instanceId, null, null),
                new TypeToken<List<FieldValueVO>>() {
                }.getType());
        Map<Long, UserDTO> userMap = FieldValueUtil.handleUserMap(values.stream().filter(x -> x.getFieldType().equals(FieldType.MEMBER)).map(FieldValueVO::getOptionId).collect(Collectors.toList()));
        Map<Long, List<FieldValueVO>> valueGroup = values.stream().collect(Collectors.groupingBy(FieldValueVO::getFieldId));

        valueGroup.forEach((fieldId, fieldValueDTOList) -> {
            ObjectSchemeFieldDTO objectSchemeField = objectSchemeFieldService.baseQueryById(organizationId, projectId, fieldId);
            PageFieldViewVO view = new PageFieldViewVO();
            FieldValueUtil.handleDTO2Value(view, objectSchemeField.getFieldType(), fieldValueDTOList, userMap, true);
            result.put(objectSchemeField.getCode(), view.getValueStr().toString());
        });

        return result;
    }

    @Override
    public List<Long> sortIssueIdsByFieldValue(Long organizationId, Long projectId, PageRequest pageRequest) {
        if (pageRequest.getSort() != null) {
            Iterator<Sort.Order> iterator = pageRequest.getSort().iterator();
            String fieldCode = "";
            while (iterator.hasNext()) {
                Sort.Order order = iterator.next();
                fieldCode = order.getProperty();
            }
            ObjectSchemeFieldDTO objectSchemeField = objectSchemeFieldService.queryByFieldCode(organizationId, projectId, fieldCode);
            String fieldType = objectSchemeField.getFieldType();
            FieldValueUtil.handleAgileSortPageRequest(fieldCode, fieldType, pageRequest);
            return fieldValueMapper.sortIssueIdsByFieldValue(organizationId, projectId, objectSchemeField.getId(), PageUtil.sortToSql(pageRequest.getSort()));
        } else {
            return new ArrayList<>();
        }
    }
}
