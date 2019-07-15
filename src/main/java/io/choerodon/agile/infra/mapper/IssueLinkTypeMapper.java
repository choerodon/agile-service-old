package io.choerodon.agile.infra.mapper;

import java.util.List;

import io.choerodon.agile.infra.dataobject.IssueLinkTypeDTO;
import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
public interface IssueLinkTypeMapper extends Mapper<IssueLinkTypeDTO> {

    /**
     * 根据项目id查询issueLinkType
     *
     * @param projectId       projectId
     * @param issueLinkTypeId 不包含的，为空则查全部
     * @param linkName        linkName
     * @param contents        contents
     * @return IssueLinkTypeDTO
     */
    List<IssueLinkTypeDTO> queryIssueLinkTypeByProjectId(@Param("projectId") Long projectId, @Param("issueLinkTypeId") Long issueLinkTypeId,
                                                         @Param("linkName") String linkName, @Param("contents") List<String> contents);

    Integer queryIssueLinkTypeName(@Param("projectId") Long projectId,
                                   @Param("issueLinkTypeName") String issueLinkTypeName,
                                   @Param("issueLinkTypeId") Long issueLinkTypeId);
}
