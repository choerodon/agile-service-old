package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.UserSettingDTO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
public interface UserSettingService {

    /**
     * 创建
     *
     * @param userSettingDTO userSettingDTO
     * @return UserSettingDTO
     */
    UserSettingDTO create(UserSettingDTO userSettingDTO);

    /**
     * 更新
     *
     * @param userSettingDTO userSettingDTO
     * @return UserSettingDTO
     */
    UserSettingDTO update(UserSettingDTO userSettingDTO);

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
