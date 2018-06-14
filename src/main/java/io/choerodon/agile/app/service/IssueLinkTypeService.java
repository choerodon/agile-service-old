package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.IssueLinkTypeDTO;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
public interface IssueLinkTypeService {

    /**
     * 查询issueLink类型
     *
     * @return IssueLinkTypeDTO
     */
    List<IssueLinkTypeDTO> listIssueLinkType();
}
