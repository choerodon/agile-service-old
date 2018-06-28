package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.IssueLinkTypeDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
public interface IssueLinkTypeMapper extends BaseMapper<IssueLinkTypeDO> {

    /**
     * 根据项目id查询issueLinkType
     *
     * @param projectId       projectId
     * @param issueLinkTypeId 不包含的，为空则查全部
     * @return IssueLinkTypeDO
     */
    List<IssueLinkTypeDO> queryIssueLinkTypeByProjectId(@Param("projectId") Long projectId, @Param("issueLinkTypeId") Long issueLinkTypeId);
}
