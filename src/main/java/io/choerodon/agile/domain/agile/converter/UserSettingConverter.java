package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.UserSettingDTO;
import io.choerodon.agile.domain.agile.entity.UserSettingE;
import io.choerodon.agile.infra.dataobject.UserSettingDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
@Component
public class UserSettingConverter implements ConvertorI<UserSettingE, UserSettingDO, UserSettingDTO> {

    @Override
    public UserSettingE dtoToEntity(UserSettingDTO userSettingDTO) {
        UserSettingE userSettingE = new UserSettingE();
        BeanUtils.copyProperties(userSettingDTO, userSettingE);
        return userSettingE;
    }

    @Override
    public UserSettingE doToEntity(UserSettingDO userSettingDO) {
        UserSettingE userSettingE = new UserSettingE();
        BeanUtils.copyProperties(userSettingDO, userSettingE);
        return userSettingE;
    }

    @Override
    public UserSettingDTO entityToDto(UserSettingE userSettingE) {
        UserSettingDTO userSettingDTO = new UserSettingDTO();
        BeanUtils.copyProperties(userSettingE, userSettingDTO);
        return userSettingDTO;
    }

    @Override
    public UserSettingDO entityToDo(UserSettingE userSettingE) {
        UserSettingDO userSettingDO = new UserSettingDO();
        BeanUtils.copyProperties(userSettingE, userSettingDO);
        return userSettingDO;
    }

    @Override
    public UserSettingDTO doToDto(UserSettingDO userSettingDO) {
        UserSettingDTO userSettingDTO = new UserSettingDTO();
        BeanUtils.copyProperties(userSettingDO, userSettingDTO);
        return userSettingDTO;
    }

    @Override
    public UserSettingDO dtoToDo(UserSettingDTO userSettingDTO) {
        UserSettingDO userSettingDO = new UserSettingDO();
        BeanUtils.copyProperties(userSettingDTO, userSettingDO);
        return userSettingDO;
    }
}