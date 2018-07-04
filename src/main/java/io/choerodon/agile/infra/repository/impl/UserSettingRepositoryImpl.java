package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.UserSettingE;
import io.choerodon.agile.domain.agile.repository.UserSettingRepository;
import io.choerodon.agile.infra.dataobject.UserSettingDO;
import io.choerodon.agile.infra.mapper.UserSettingMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
@Component
@Transactional(rollbackFor = CommonException.class)
public class UserSettingRepositoryImpl implements UserSettingRepository {

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Override
    public UserSettingE create(UserSettingE userSettingE) {
        UserSettingDO userSettingDO = ConvertHelper.convert(userSettingE, UserSettingDO.class);
        int insert = userSettingMapper.insert(userSettingDO);
        if (insert != 1) {
            throw new CommonException("error.userSetting.create");
        }
        return ConvertHelper.convert(userSettingMapper.selectByPrimaryKey(userSettingDO.getSettingId()), UserSettingE.class);
    }

    @Override
    public UserSettingE update(UserSettingE userSettingE) {
        UserSettingDO userSettingDO = ConvertHelper.convert(userSettingE, UserSettingDO.class);
        if (userSettingMapper.selectByPrimaryKey(userSettingDO) == null) {
            throw new CommonException("error.userSetting.notFound");
        }
        int update = userSettingMapper.updateByPrimaryKey(userSettingDO);
        if (update != 1) {
            throw new CommonException("error.userSetting.update");
        }
        return ConvertHelper.convert(userSettingMapper.selectByPrimaryKey(userSettingDO.getSettingId()), UserSettingE.class);
    }
}
