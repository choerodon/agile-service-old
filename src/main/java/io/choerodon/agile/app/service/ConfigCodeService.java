package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.ConfigCodeVO;
import io.choerodon.agile.infra.statemachineclient.dto.PropertyData;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/10/10
 */
public interface ConfigCodeService {

    /**
     * 根据类型获取ConfigCode
     *
     * @param type
     * @return
     */
    List<ConfigCodeVO> queryByType(String type);

    /**
     * 根据转换id获取未设置的ConfigCode
     *
     * @param organizationId
     * @param transformId
     * @param type
     * @return
     */
    List<ConfigCodeVO> queryByTransformId(Long organizationId, Long transformId, String type);

    /**
     * 处理扫描到的ConfigCode
     *
     * @param propertyData
     */
    void handlePropertyData(PropertyData propertyData);
}
