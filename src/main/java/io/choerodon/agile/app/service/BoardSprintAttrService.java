package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.BoardSprintAttrDTO;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public interface BoardSprintAttrService {

    BoardSprintAttrDTO addColumnWidth(Long projectId, Long sprintId);

    BoardSprintAttrDTO reduceColumnWidth(Long projectId, Long sprintId);
}
