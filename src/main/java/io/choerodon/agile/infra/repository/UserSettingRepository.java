package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.UserSettingE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
public interface UserSettingRepository {

    /**
     * 创建
     *
     * @param userSettingE userSettingE
     * @return UserSettingE
     */
    UserSettingE create(UserSettingE userSettingE);

    /**
     * 更新
     *
     * @param userSettingE userSettingE
     * @return UserSettingE
     */
    UserSettingE update(UserSettingE userSettingE);

    /**
     * 更新用户其他板默认的为非默认
     *
     * @param boardId   boardId
     * @param projectId projectId
     * @param userId    userId
     * @return
     */
    Integer updateOtherBoardNoDefault(Long boardId, Long projectId, Long userId);
}
