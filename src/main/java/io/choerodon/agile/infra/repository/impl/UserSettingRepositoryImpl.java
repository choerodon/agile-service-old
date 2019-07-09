package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.UserSettingE;
import io.choerodon.agile.infra.repository.UserSettingRepository;
import io.choerodon.agile.infra.dataobject.UserSettingDTO;
import io.choerodon.agile.infra.mapper.UserSettingMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
@Component
public class UserSettingRepositoryImpl implements UserSettingRepository {

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Override
    public UserSettingE create(UserSettingE userSettingE) {
        UserSettingDTO userSettingDTO = ConvertHelper.convert(userSettingE, UserSettingDTO.class);
        int insert = userSettingMapper.insert(userSettingDTO);
        if (insert != 1) {
            throw new CommonException("error.userSetting.create");
        }
        return ConvertHelper.convert(userSettingMapper.selectByPrimaryKey(userSettingDTO.getSettingId()), UserSettingE.class);
    }

    @Override
    public UserSettingE update(UserSettingE userSettingE) {
        UserSettingDTO userSettingDTO = ConvertHelper.convert(userSettingE, UserSettingDTO.class);
        if (userSettingMapper.selectByPrimaryKey(userSettingDTO) == null) {
            throw new CommonException("error.userSetting.notFound");
        }
        int update = userSettingMapper.updateByPrimaryKey(userSettingDTO);
        if (update != 1) {
            throw new CommonException("error.userSetting.update");
        }
        return ConvertHelper.convert(userSettingMapper.selectByPrimaryKey(userSettingDTO.getSettingId()), UserSettingE.class);
    }

    @Override
    public Integer updateOtherBoardNoDefault(Long boardId, Long projectId, Long userId) {
        return userSettingMapper.updateOtherBoardNoDefault(boardId,projectId,userId);
    }
}
