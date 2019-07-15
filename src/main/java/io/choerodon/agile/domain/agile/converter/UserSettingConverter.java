//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.UserSettingVO;
//import io.choerodon.agile.domain.agile.entity.UserSettingE;
//import io.choerodon.agile.infra.dataobject.UserSettingDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/7/4
// */
//@Component
//public class UserSettingConverter implements ConvertorI<UserSettingE, UserSettingDTO, UserSettingVO> {
//
//    @Override
//    public UserSettingE dtoToEntity(UserSettingVO userSettingVO) {
//        UserSettingE userSettingE = new UserSettingE();
//        BeanUtils.copyProperties(userSettingVO, userSettingE);
//        return userSettingE;
//    }
//
//    @Override
//    public UserSettingE doToEntity(UserSettingDTO userSettingDTO) {
//        UserSettingE userSettingE = new UserSettingE();
//        BeanUtils.copyProperties(userSettingDTO, userSettingE);
//        return userSettingE;
//    }
//
//    @Override
//    public UserSettingVO entityToDto(UserSettingE userSettingE) {
//        UserSettingVO userSettingVO = new UserSettingVO();
//        BeanUtils.copyProperties(userSettingE, userSettingVO);
//        return userSettingVO;
//    }
//
//    @Override
//    public UserSettingDTO entityToDo(UserSettingE userSettingE) {
//        UserSettingDTO userSettingDTO = new UserSettingDTO();
//        BeanUtils.copyProperties(userSettingE, userSettingDTO);
//        return userSettingDTO;
//    }
//
//    @Override
//    public UserSettingVO doToDto(UserSettingDTO userSettingDTO) {
//        UserSettingVO userSettingVO = new UserSettingVO();
//        BeanUtils.copyProperties(userSettingDTO, userSettingVO);
//        return userSettingVO;
//    }
//
//    @Override
//    public UserSettingDTO dtoToDo(UserSettingVO userSettingVO) {
//        UserSettingDTO userSettingDTO = new UserSettingDTO();
//        BeanUtils.copyProperties(userSettingVO, userSettingDTO);
//        return userSettingDTO;
//    }
//}