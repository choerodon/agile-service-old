package io.choerodon.agile.domain.agile.repository;

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
}
