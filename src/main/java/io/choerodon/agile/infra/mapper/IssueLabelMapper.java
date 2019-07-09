package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.agile.infra.dataobject.*;
import org.apache.ibatis.annotations.Param;


/**
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
public interface IssueLabelMapper extends Mapper<IssueLabelDTO> {

    /**
     * 回收没有再用的issue标签
     *
     * @param projectId projectId
     * @return int
     */
    int labelGarbageCollection(@Param("projectId") Long projectId);

    /**
     * 重名校验
     *
     * @param labelName labelName
     * @param projectId projectId
     * @return 重名true 否 false
     */
    Boolean checkNameExist(@Param("labelName") String labelName, @Param("projectId") Long projectId);

    /**
     * 根据label名称和项目id查询labelId
     *
     * @param labelName labelName
     * @param projectId projectId
     * @return Long
     */
    Long queryLabelIdByLabelNameAndProjectId(@Param("labelName") String labelName, @Param("projectId") Long projectId);
}