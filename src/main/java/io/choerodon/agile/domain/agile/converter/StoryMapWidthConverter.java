//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.StoryMapWidthVO;
//import io.choerodon.agile.domain.agile.entity.StoryMapWidthE;
//import io.choerodon.agile.infra.dataobject.StoryMapWidthDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2019/6/3.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class StoryMapWidthConverter implements ConvertorI<StoryMapWidthE, StoryMapWidthDTO, StoryMapWidthVO> {
//
//    @Override
//    public StoryMapWidthE dtoToEntity(StoryMapWidthVO storyMapWidthVO) {
//        StoryMapWidthE storyMapWidthE = new StoryMapWidthE();
//        BeanUtils.copyProperties(storyMapWidthVO, storyMapWidthE);
//        return storyMapWidthE;
//    }
//
//    @Override
//    public StoryMapWidthVO entityToDto(StoryMapWidthE storyMapWidthE) {
//        StoryMapWidthVO storyMapWidthVO = new StoryMapWidthVO();
//        BeanUtils.copyProperties(storyMapWidthE, storyMapWidthVO);
//        return storyMapWidthVO;
//    }
//
//    @Override
//    public StoryMapWidthE doToEntity(StoryMapWidthDTO storyMapWidthDTO) {
//        StoryMapWidthE storyMapWidthE = new StoryMapWidthE();
//        BeanUtils.copyProperties(storyMapWidthDTO, storyMapWidthE);
//        return storyMapWidthE;
//    }
//
//    @Override
//    public StoryMapWidthDTO entityToDo(StoryMapWidthE storyMapWidthE) {
//        StoryMapWidthDTO storyMapWidthDTO = new StoryMapWidthDTO();
//        BeanUtils.copyProperties(storyMapWidthE, storyMapWidthDTO);
//        return storyMapWidthDTO;
//    }
//
//    @Override
//    public StoryMapWidthVO doToDto(StoryMapWidthDTO storyMapWidthDTO) {
//        StoryMapWidthVO storyMapWidthVO = new StoryMapWidthVO();
//        BeanUtils.copyProperties(storyMapWidthDTO, storyMapWidthVO);
//        return storyMapWidthVO;
//    }
//
//    @Override
//    public StoryMapWidthDTO dtoToDo(StoryMapWidthVO storyMapWidthVO) {
//        StoryMapWidthDTO storyMapWidthDTO = new StoryMapWidthDTO();
//        BeanUtils.copyProperties(storyMapWidthVO, storyMapWidthDTO);
//        return storyMapWidthDTO;
//    }
//}
