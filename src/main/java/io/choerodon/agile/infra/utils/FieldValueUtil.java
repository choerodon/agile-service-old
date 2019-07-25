package io.choerodon.agile.infra.utils;

import io.choerodon.agile.api.vo.FieldDataLogCreateVO;
import io.choerodon.agile.api.vo.FieldValueVO;
import io.choerodon.agile.api.vo.ObjectSchemeFieldDetailVO;
import io.choerodon.agile.api.vo.PageFieldViewVO;
import io.choerodon.agile.app.service.FieldDataLogService;
import io.choerodon.agile.infra.dataobject.FieldOptionDTO;
import io.choerodon.agile.infra.dataobject.FieldValueDTO;
import io.choerodon.agile.infra.dataobject.PageFieldDTO;
import io.choerodon.agile.infra.dataobject.UserDTO;
import io.choerodon.agile.infra.enums.FieldType;
import io.choerodon.agile.infra.enums.ObjectSchemeCode;
import io.choerodon.agile.infra.feign.IamFeignClient;
import io.choerodon.agile.infra.mapper.FieldOptionMapper;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @since 2019/6/11
 */
public class FieldValueUtil {

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATE_VALUE = "date_value";

    /**
     * 获取人员信息
     *
     * @param userIds
     * @return
     */
    public static Map<Long, UserDTO> handleUserMap(List<Long> userIds) {
        Map<Long, UserDTO> map = new HashMap<>(userIds.size());
        IamFeignClient iamFeignClient = SpringBeanUtil.getBean(IamFeignClient.class);
        if (!userIds.isEmpty()) {
            map = iamFeignClient.listUsersByIds(userIds.toArray(new Long[userIds.size()]), false).getBody().stream().collect(Collectors.toMap(UserDTO::getId, x -> x));
        }
        return map;
    }

    /**
     * 处理FieldValueDTO为value
     *
     * @param view
     * @param fieldType
     * @param values
     */
    public static void handleDTO2Value(PageFieldViewVO view, String fieldType, List<FieldValueVO> values, Map<Long, UserDTO> userMap, Boolean isJustStr) {
        Object valueStr = null;
        Object value = null;
        if (values != null && !values.isEmpty()) {
            Long[] longValues = new Long[values.size()];
            switch (fieldType) {
                case FieldType.CHECKBOX:
                case FieldType.MULTIPLE:
                    values.stream().map(FieldValueVO::getOptionId).collect(Collectors.toList()).toArray(longValues);
                    value = longValues;
                    valueStr = values.stream().map(FieldValueVO::getOptionValue).collect(Collectors.joining(", "));
                    break;
                case FieldType.RADIO:
                case FieldType.SINGLE:
                    //单选款/选择器（单选）获取为Long
                    value = values.get(0).getOptionId();
                    valueStr = values.get(0).getOptionValue();
                    break;
                case FieldType.DATETIME:
                    value = values.get(0).getDateValue();
                    DateFormat dff = new SimpleDateFormat(DATETIME_FORMAT);
                    if (value != null) {
                        valueStr = dff.format(value);
                    }
                    break;
                case FieldType.DATE:
                    value = values.get(0).getDateValue();
                    DateFormat dfff = new SimpleDateFormat(DATE_FORMAT);
                    if (value != null) {
                        valueStr = dfff.format(value);
                    }
                    break;
                case FieldType.TIME:
                    value = values.get(0).getDateValue();
                    DateFormat df = new SimpleDateFormat(TIME_FORMAT);
                    if (value != null) {
                        valueStr = df.format(value);
                    }
                    break;
                case FieldType.INPUT:
                    value = values.get(0).getStringValue();
                    valueStr = value.toString();
                    break;
                case FieldType.NUMBER:
                    value = values.get(0).getNumberValue();
                    //是否包括小数
                    if (view.getExtraConfig() != null && view.getExtraConfig()) {
                        valueStr = value.toString();
                    } else {
                        valueStr = value.toString().split("\\.")[0];
                        value = valueStr;
                    }
                    break;
                case FieldType.TEXT:
                    value = values.get(0).getTextValue();
                    valueStr = value.toString();
                    break;
                case FieldType.MEMBER:
                    //人员获取为Long
                    value = values.get(0).getOptionId();
                    //是否仅需要字符串，用于导出
                    if (isJustStr) {
                        UserDTO userVO = userMap.get(value);
                        if (userVO != null) {
                            valueStr = userVO.getLoginName() + userVO.getRealName();
                        }
                    } else {
                        valueStr = userMap.getOrDefault(value, new UserDTO());
                    }
                    break;
                default:
                    break;
            }
        }
        view.setValueStr(valueStr);
        view.setValue(value);
    }

    /**
     * 处理默认值
     *
     * @param pageFieldViews
     */
    public static void handleDefaultValue(List<PageFieldViewVO> pageFieldViews) {
        Map<Long, UserDTO> userMap = handleUserMap(pageFieldViews.stream().filter(x -> x.getFieldType().equals(FieldType.MEMBER) && x.getDefaultValue() != null && !"".equals(String.valueOf(x.getDefaultValue()).trim()))
                .map(x -> Long.parseLong(String.valueOf(x.getDefaultValue()))).collect(Collectors.toList()));
        for (PageFieldViewVO view : pageFieldViews) {
            switch (view.getFieldType()) {
                case FieldType.CHECKBOX:
                case FieldType.MULTIPLE:
                    handleDefaultValueIds(view);
                    break;
                case FieldType.RADIO:
                case FieldType.SINGLE:
                    if (view.getDefaultValue() != null && !"".equals(view.getDefaultValue())) {
                        view.setDefaultValue(Long.valueOf(String.valueOf(view.getDefaultValue())));
                    }
                    break;
                case FieldType.DATETIME:
                case FieldType.DATE:
                case FieldType.TIME:
                    //如果勾选了默认当前
                    if (view.getExtraConfig() != null && view.getExtraConfig()) {
                        view.setDefaultValue(new Date());
                    }
                    break;
                case FieldType.NUMBER:
                    //如果勾选了是否小数
                    if (view.getExtraConfig() != null && !view.getExtraConfig()) {
                        view.setDefaultValue(view.getDefaultValue().toString().split("\\.")[0]);
                    }
                    break;
                case FieldType.MEMBER:
                    if (view.getDefaultValue() != null && !"".equals(view.getDefaultValue())) {
                        Long defaultValue = Long.valueOf(String.valueOf(view.getDefaultValue()));
                        view.setDefaultValue(defaultValue);
                        view.setDefaultValueObj(userMap.getOrDefault(defaultValue, new UserDTO()));
                    }
                    break;
                case FieldType.INPUT:
                case FieldType.TEXT:
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理默认值多选id
     *
     * @param view
     */
    public static void handleDefaultValueIds(PageFieldViewVO view) {
        Object defaultValue = view.getDefaultValue();
        if (defaultValue != null && !"".equals(defaultValue)) {
            String[] defaultIdStrs = String.valueOf(defaultValue).split(",");
            if (defaultIdStrs != null) {
                Long[] defaultIds = new Long[defaultIdStrs.length];
                for (int i = 0; i < defaultIdStrs.length; i++) {
                    defaultIds[i] = Long.valueOf(defaultIdStrs[i]);
                }
                view.setDefaultValue(defaultIds);
            }
        }
    }

    /**
     * 处理默认值
     *
     * @param fieldDetail
     */
    public static void handleDefaultValue(ObjectSchemeFieldDetailVO fieldDetail) {
        switch (fieldDetail.getFieldType()) {
            case FieldType.CHECKBOX:
            case FieldType.MULTIPLE:
            case FieldType.RADIO:
            case FieldType.SINGLE:
            case FieldType.DATETIME:
            case FieldType.DATE:
            case FieldType.TIME:
            case FieldType.NUMBER:
            case FieldType.INPUT:
            case FieldType.TEXT:
                break;
            case FieldType.MEMBER:
                IamFeignClient iamFeignClient = SpringBeanUtil.getBean(IamFeignClient.class);
                if (fieldDetail.getDefaultValue() != null && !"".equals(fieldDetail.getDefaultValue())) {
                    Long defaultValue = Long.valueOf(String.valueOf(fieldDetail.getDefaultValue()));
                    List<UserDTO> list = iamFeignClient.listUsersByIds(Arrays.asList(defaultValue).toArray(new Long[1]), false).getBody();
                    if (!list.isEmpty()) {
                        fieldDetail.setDefaultValueObj(list.get(0));
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理valueStr为FieldValue
     *
     * @param fieldValues
     * @param create
     */
    public static void handleDefaultValue2DTO(List<FieldValueDTO> fieldValues, PageFieldDTO create) {
        String defaultValue = create.getDefaultValue();
        String fieldType = create.getFieldType();
        FieldValueDTO fieldValue = new FieldValueDTO();
        //处理默认当前时间
        if (fieldType.equals(FieldType.DATETIME) || fieldType.equals(FieldType.TIME)) {
            if (create.getExtraConfig() != null && create.getExtraConfig()) {
                DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
                defaultValue = df.format(new Date());
            }
        }
        if (defaultValue != null && !defaultValue.equals("")) {
            try {
                switch (fieldType) {
                    case FieldType.CHECKBOX:
                    case FieldType.MULTIPLE:
                        String[] optionIds = defaultValue.split(",");
                        for (String optionId : optionIds) {
                            FieldValueDTO oValue = new FieldValueDTO();
                            oValue.setOptionId(Long.parseLong(optionId));
                            fieldValues.add(oValue);
                        }
                        break;
                    case FieldType.RADIO:
                    case FieldType.SINGLE:
                    case FieldType.MEMBER:
                        Long optionId = Long.parseLong(defaultValue);
                        fieldValue.setOptionId(optionId);
                        fieldValues.add(fieldValue);
                        break;
                    case FieldType.DATETIME:
                    case FieldType.DATE:
                    case FieldType.TIME:
                        DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
                        Date dateValue = df.parse(defaultValue);
                        fieldValue.setDateValue(dateValue);
                        fieldValues.add(fieldValue);
                        break;
                    case FieldType.INPUT:
                        fieldValue.setStringValue(defaultValue);
                        fieldValues.add(fieldValue);
                        break;
                    case FieldType.NUMBER:
                        fieldValue.setNumberValue(defaultValue);
                        fieldValues.add(fieldValue);
                        break;
                    case FieldType.TEXT:
                        fieldValue.setTextValue(defaultValue);
                        fieldValues.add(fieldValue);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                throw new CommonException(e.getMessage());
            }
        }
    }

    /**
     * 处理value为FieldValue
     *
     * @param fieldValues
     * @param fieldType
     * @param value
     */
    public static void handleValue2DTO(List<FieldValueDTO> fieldValues, String fieldType, Object value) {
        FieldValueDTO fieldValue = new FieldValueDTO();
        if (value != null) {
            try {
                switch (fieldType) {
                    case FieldType.CHECKBOX:
                    case FieldType.MULTIPLE:
                        List<Integer> optionIds = (List<Integer>) value;
                        for (Integer optionId : optionIds) {
                            FieldValueDTO oValue = new FieldValueDTO();
                            oValue.setOptionId(Long.parseLong(String.valueOf(optionId)));
                            fieldValues.add(oValue);
                        }
                        break;
                    case FieldType.RADIO:
                    case FieldType.SINGLE:
                    case FieldType.MEMBER:
                        //人员/单选款/选择器（单选）处理为Long
                        Long optionId = Long.parseLong(value.toString());
                        fieldValue.setOptionId(optionId);
                        fieldValues.add(fieldValue);
                        break;
                    case FieldType.DATETIME:
                    case FieldType.DATE:
                    case FieldType.TIME:
                        DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
                        Date dateValue = df.parse(value.toString());
                        fieldValue.setDateValue(dateValue);
                        fieldValues.add(fieldValue);
                        break;
                    case FieldType.INPUT:
                        String stringValue = (String) value;
                        fieldValue.setStringValue(stringValue);
                        fieldValues.add(fieldValue);
                        break;
                    case FieldType.NUMBER:
                        String numberValue = value.toString();
                        fieldValue.setNumberValue(numberValue);
                        fieldValues.add(fieldValue);
                        break;
                    case FieldType.TEXT:
                        String textValue = (String) value;
                        fieldValue.setTextValue(textValue);
                        fieldValues.add(fieldValue);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                throw new CommonException(e.getMessage());
            }
        }
    }

    /**
     * 处理自定义字段日志
     *
     * @param organizationId
     * @param projectId
     * @param instanceId
     * @param fieldId
     * @param fieldType
     * @param schemeCode
     * @param oldFieldValues
     * @param newFieldValues
     */
    public static void handleDataLog(Long organizationId, Long projectId, Long instanceId, Long fieldId, String fieldType, String schemeCode, List<FieldValueDTO> oldFieldValues, List<FieldValueDTO> newFieldValues) {
        switch (schemeCode) {
            case ObjectSchemeCode.AGILE_ISSUE:
                handleAgileDataLog(organizationId, projectId, instanceId, fieldId, fieldType, oldFieldValues, newFieldValues);
                break;
            default:
                break;
        }
    }

    /**
     * 处理敏捷问题自定义字段日志
     *
     * @param organizationId
     * @param projectId
     * @param instanceId
     * @param fieldId
     * @param fieldType
     * @param oldFieldValues
     * @param newFieldValues
     */
    private static void handleAgileDataLog(Long organizationId, Long projectId, Long instanceId, Long fieldId, String fieldType, List<FieldValueDTO> oldFieldValues, List<FieldValueDTO> newFieldValues) {
        FieldOptionMapper fieldOptionMapper = SpringBeanUtil.getBean(FieldOptionMapper.class);
        FieldDataLogService fieldDataLogService = SpringBeanUtil.getBean(FieldDataLogService.class);
        FieldDataLogCreateVO create = new FieldDataLogCreateVO();
        create.setFieldId(fieldId);
        create.setInstanceId(instanceId);
        try {
            switch (fieldType) {
                case FieldType.CHECKBOX:
                case FieldType.MULTIPLE:
                    if (!oldFieldValues.isEmpty()) {
                        create.setOldValue(oldFieldValues.stream().map(x -> String.valueOf(x.getOptionId())).collect(Collectors.joining(",")));
                        create.setOldString(oldFieldValues.stream().map(FieldValueDTO::getOptionValue).collect(Collectors.joining(",")));
                    }
                    if (!newFieldValues.isEmpty()) {
                        List<Long> newOptionIds = newFieldValues.stream().map(FieldValueDTO::getOptionId).collect(Collectors.toList());
                        create.setNewValue(newOptionIds.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(",")));
                        create.setNewString(fieldOptionMapper.selectByOptionIds(organizationId, newOptionIds).stream().map(FieldOptionDTO::getValue).collect(Collectors.joining(",")));
                    }
                    break;
                case FieldType.RADIO:
                case FieldType.SINGLE:
                    if (!oldFieldValues.isEmpty()) {
                        create.setOldValue(String.valueOf(oldFieldValues.get(0).getOptionId()));
                        create.setOldString(String.valueOf(oldFieldValues.get(0).getOptionValue()));
                    }
                    if (!newFieldValues.isEmpty()) {
                        List<Long> newOptionIds = Arrays.asList(newFieldValues.get(0).getOptionId());
                        List<FieldOptionDTO> fieldOptions = fieldOptionMapper.selectByOptionIds(organizationId, newOptionIds);
                        create.setNewValue(String.valueOf(newFieldValues.get(0).getOptionId()));
                        if (!fieldOptions.isEmpty()) {
                            create.setNewString(fieldOptions.get(0).getValue());
                        }
                    }
                    break;
                case FieldType.DATETIME:
                    DateFormat df1 = new SimpleDateFormat(DATETIME_FORMAT);
                    if (!oldFieldValues.isEmpty()) {
                        create.setOldString(df1.format(oldFieldValues.get(0).getDateValue()));
                    }
                    if (!newFieldValues.isEmpty()) {
                        create.setNewString(df1.format(newFieldValues.get(0).getDateValue()));
                    }
                    break;
                case FieldType.DATE:
                    DateFormat df3 = new SimpleDateFormat(DATE_FORMAT);
                    if (!oldFieldValues.isEmpty()) {
                        create.setOldString(df3.format(oldFieldValues.get(0).getDateValue()));
                    }
                    if (!newFieldValues.isEmpty()) {
                        create.setNewString(df3.format(newFieldValues.get(0).getDateValue()));
                    }
                    break;
                case FieldType.TIME:
                    DateFormat df2 = new SimpleDateFormat(DATETIME_FORMAT);
                    if (!oldFieldValues.isEmpty()) {
                        create.setOldString(df2.format(oldFieldValues.get(0).getDateValue()));
                    }
                    if (!newFieldValues.isEmpty()) {
                        create.setNewString(df2.format(newFieldValues.get(0).getDateValue()));
                    }
                    break;
                case FieldType.INPUT:
                    if (!oldFieldValues.isEmpty()) {
                        create.setOldString(oldFieldValues.get(0).getStringValue());
                    }
                    if (!newFieldValues.isEmpty()) {
                        create.setNewString(newFieldValues.get(0).getStringValue());
                    }
                    break;
                case FieldType.NUMBER:
                    if (!oldFieldValues.isEmpty()) {
                        create.setOldString(oldFieldValues.get(0).getNumberValue());
                    }
                    if (!newFieldValues.isEmpty()) {
                        create.setNewString(newFieldValues.get(0).getNumberValue());
                    }
                    break;
                case FieldType.TEXT:
                    if (!oldFieldValues.isEmpty()) {
                        create.setOldString(oldFieldValues.get(0).getTextValue());
                    }
                    if (!newFieldValues.isEmpty()) {
                        create.setNewString(newFieldValues.get(0).getTextValue());
                    }
                    break;
                case FieldType.MEMBER:
                    //查询用户
                    List<Long> userIds = oldFieldValues.stream().map(FieldValueDTO::getOptionId).collect(Collectors.toList());
                    userIds.addAll(newFieldValues.stream().map(FieldValueDTO::getOptionId).collect(Collectors.toList()));
                    Map<Long, UserDTO> userMap = handleUserMap(userIds);
                    if (!oldFieldValues.isEmpty()) {
                        create.setOldValue(String.valueOf(oldFieldValues.get(0).getOptionId()));
                        create.setOldString(userMap.getOrDefault(oldFieldValues.get(0).getOptionId(), new UserDTO()).getRealName());
                    }
                    if (!newFieldValues.isEmpty()) {
                        create.setNewValue(String.valueOf(newFieldValues.get(0).getOptionId()));
                        create.setNewString(userMap.getOrDefault(newFieldValues.get(0).getOptionId(), new UserDTO()).getRealName());
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        fieldDataLogService.createDataLog(projectId, ObjectSchemeCode.AGILE_ISSUE, create);
    }

    public static void handleAgileSortPageRequest(String fieldCode, String fieldType, PageRequest pageRequest) {
        try {
            switch (fieldType) {
                case FieldType.DATETIME:
                case FieldType.DATE:
                case FieldType.TIME:
                    Map<String, String> order = new HashMap<>(1);
                    order.put(fieldCode, DATE_VALUE);
                    PageUtil.sortResetOrder(pageRequest.getSort(), "fv", order);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}