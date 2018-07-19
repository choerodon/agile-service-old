package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.infra.dataobject.UserDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;

import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/28
 */
public interface UserRepository {

    /**
     * 查询
     *
     * @param userId userId
     * @param withId withId
     * @return userDO
     */
    UserDO queryUserNameByOption(Long userId, Boolean withId);

    Map<Long, UserMessageDO> queryUsersMap(List<Long> assigneeIds, Boolean withLoginName);
}
