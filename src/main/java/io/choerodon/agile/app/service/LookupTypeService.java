package io.choerodon.agile.app.service;


import io.choerodon.agile.api.vo.LookupTypeDTO;

import java.util.List;

/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
public interface LookupTypeService {

    /**
     * 查询所有lookupType
     *
     * @param project project
     * @return LookupTypeDTO
     */
    List<LookupTypeDTO> listLookupType(Long project);
}