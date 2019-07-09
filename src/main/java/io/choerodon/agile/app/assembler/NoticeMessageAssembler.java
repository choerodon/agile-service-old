package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.IdWithNameDTO;
import io.choerodon.agile.api.vo.MessageVO;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.dataobject.MessageDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/20.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class NoticeMessageAssembler {

    @Autowired
    private UserService userService;


    public List<MessageVO> messageDOToIDTO(List<MessageDTO> messageDTOList, List<Long> ids) {
        List<MessageVO> messageVOList = new ArrayList<>(messageDTOList.size());
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(ids, true);
        messageDTOList.forEach(messageDTO -> {
            List<IdWithNameDTO> idWithNameDTOList = new ArrayList<>();
            if (messageDTO.getEnable() && messageDTO.getUser() != null && messageDTO.getUser().length() != 0 && !"null".equals(messageDTO.getUser())) {
                String[] strs = messageDTO.getUser().split(",");
                for (String str : strs) {
                    Long id = Long.parseLong(str);
                    if (usersMap.get(id) != null) {
                        idWithNameDTOList.add(new IdWithNameDTO(id, usersMap.get(id).getName()));
                    }
                }
            }
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(messageDTO, messageVO);
            messageVO.setIdWithNameDTOList(idWithNameDTOList);
            messageVOList.add(messageVO);
        });
        return messageVOList;
    }

}
