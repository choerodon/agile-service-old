package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.BurnDownChangeDTO;
import io.choerodon.agile.api.dto.CoordinateDTO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
public interface ReportService {

    /**
     * 燃尽图坐标信息
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @param type      storyPoints、remainingEstimatedTime、originalEstimatedTime、issueCount
     * @return CoordinateDTO
     */
    CoordinateDTO queryBurnDownCoordinate(Long projectId, Long sprintId, String type);

    /**
     * 查询燃尽图报告信息
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @param type      storyPoints、remainingEstimatedTime、originalEstimatedTime、issueCount
     * @return BurnDownChangeDTO
     */
    BurnDownChangeDTO queryBurnDown(Long projectId, Long sprintId, String type);
}
