package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.BoardSprintAttrDTO;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public interface BoardSprintAttrService {

    BoardSprintAttrDTO updateColumnWidth(Long projectId, Long sprintId, Integer columnWidth);
}
