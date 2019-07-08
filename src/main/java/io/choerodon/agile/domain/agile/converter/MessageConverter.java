package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.MessageDTO;
import io.choerodon.agile.infra.dataobject.MessageDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class MessageConverter implements ConvertorI<Object, MessageDO, MessageDTO> {

    @Override
    public MessageDTO doToDto(MessageDO messageDO) {
        MessageDTO messageDTO = new MessageDTO();
        BeanUtils.copyProperties(messageDO, messageDTO);
        return messageDTO;
    }

    @Override
    public MessageDO dtoToDo(MessageDTO messageDTO) {
        MessageDO messageDO = new MessageDO();
        BeanUtils.copyProperties(messageDTO, messageDO);
        return messageDO;
    }
}
