package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.UserSettingService;
import io.choerodon.agile.infra.dataobject.UserSettingDTO;
import io.choerodon.agile.infra.mapper.UserSettingMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
@Component
public class UserSettingServiceImpl implements UserSettingService {

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Override
    public UserSettingDTO create(UserSettingDTO userSettingDTO) {
        int insert = userSettingMapper.insert(userSettingDTO);
        if (insert != 1) {
            throw new CommonException("error.userSetting.create");
        }
        return userSettingMapper.selectByPrimaryKey(userSettingDTO.getSettingId());
    }

    @Override
    public UserSettingDTO update(UserSettingDTO userSettingDTO) {
        if (userSettingMapper.selectByPrimaryKey(userSettingDTO) == null) {
            throw new CommonException("error.userSetting.notFound");
        }
        int update = userSettingMapper.updateByPrimaryKey(userSettingDTO);
        if (update != 1) {
            throw new CommonException("error.userSetting.update");
        }
        return userSettingMapper.selectByPrimaryKey(userSettingDTO.getSettingId());
    }

    @Override
    public Integer updateOtherBoardNoDefault(Long boardId, Long projectId, Long userId) {
        return userSettingMapper.updateOtherBoardNoDefault(boardId,projectId,userId);
    }
}
