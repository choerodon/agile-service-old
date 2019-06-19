package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.agile.infra.dataobject.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:47:27
 */
public interface ComponentIssueRelMapper extends Mapper<ComponentIssueRelDO> {

    List<ComponentIssueRelDO> selectByProjectIdAndIssueId(@Param("projectId") Long projectId, @Param("issueId") Long issueId);
}