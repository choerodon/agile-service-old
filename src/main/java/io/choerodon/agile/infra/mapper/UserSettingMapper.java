package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.UserSettingDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
public interface UserSettingMapper extends Mapper<UserSettingDO> {

    /**
     * 更新用户其他面板为非默认
     *
     * @param boardId   boardId
     * @param projectId projectId
     * @param userId    userId
     * @return Integer
     */
    Integer updateOtherBoardNoDefault(@Param("boardId") Long boardId, @Param("projectId")Long projectId,@Param("userId") Long userId);
}
