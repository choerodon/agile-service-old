//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.MessageVO;
//import io.choerodon.agile.infra.dataobject.MessageDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/10/8.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class MessageConverter implements ConvertorI<Object, MessageDTO, MessageVO> {
//
//    @Override
//    public MessageVO doToDto(MessageDTO messageDTO) {
//        MessageVO messageVO = new MessageVO();
//        BeanUtils.copyProperties(messageDTO, messageVO);
//        return messageVO;
//    }
//
//    @Override
//    public MessageDTO dtoToDo(MessageVO messageVO) {
//        MessageDTO messageDTO = new MessageDTO();
//        BeanUtils.copyProperties(messageVO, messageDTO);
//        return messageDTO;
//    }
//}
