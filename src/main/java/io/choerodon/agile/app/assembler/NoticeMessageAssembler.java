package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.IdWithNameDTO;
import io.choerodon.agile.api.dto.MessageDTO;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.MessageDO;
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
    private UserRepository userRepository;


    public List<MessageDTO> messageDOToIDTO(List<MessageDO> messageDOList, List<Long> ids) {
        List<MessageDTO> messageDTOList = new ArrayList<>(messageDOList.size());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(ids, true);
        messageDOList.forEach(messageDO -> {
            List<IdWithNameDTO> idWithNameDTOList = new ArrayList<>();
            if (messageDO.getEnable() && messageDO.getUser() != null && messageDO.getUser().length() != 0 && !"null".equals(messageDO.getUser())) {
                String[] strs = messageDO.getUser().split(",");
                for (String str : strs) {
                    Long id = Long.parseLong(str);
                    if (usersMap.get(id) != null) {
                        idWithNameDTOList.add(new IdWithNameDTO(id, usersMap.get(id).getName()));
                    }
                }
            }
            MessageDTO messageDTO = new MessageDTO();
            BeanUtils.copyProperties(messageDO, messageDTO);
            messageDTO.setIdWithNameDTOList(idWithNameDTOList);
            messageDTOList.add(messageDTO);
        });
        return messageDTOList;
    }

}
