package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.domain.agile.entity.BurnDownChangeE;
import io.choerodon.agile.domain.agile.entity.ReportIssueE;
import io.choerodon.agile.domain.agile.entity.SprintE;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
public interface ReportMapper {

    /**
     * 获取当前冲刺开启前的issue的故事点总和
     *
     * @param sprintE sprintE
     * @return Integer
     */
    Integer queryStoryPointsBeforeSprintStart(@Param("sprintE") SprintE sprintE);

    /**
     * 获取当前冲刺期间加入的issue(包含加入时间、加入时的故事点)
     *
     * @param sprintE sprintE
     * @return ReportIssueE
     */
    List<ReportIssueE> queryAddIssueDurationSprint(@Param("sprintE") SprintE sprintE);

    /**
     * 获取当前冲刺期间移除的issue(包含移除时间、移除时的故事点)
     *
     * @param sprintE sprintE
     * @return ReportIssueE
     */
    List<ReportIssueE> queryRemoveIssueDurationSprint(@Param("sprintE") SprintE sprintE);

    /**
     * 获取冲刺开启前的issue计数
     *
     * @param sprintE sprintE
     * @return issueCounts
     */
    Integer queryIssueTotalCountBeforeSprintStart(@Param("sprintE") SprintE sprintE);

    /**
     * 获取冲刺开启前的剩余预计时间总和
     *
     * @param sprintE sprintE
     * @return Integer
     */
    Integer queryRemainingTimesBeforeSprintStart(@Param("sprintE") SprintE sprintE);

}
