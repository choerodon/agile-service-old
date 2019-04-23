package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.DataLogDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.DataLogDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/26.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DataLogAssembler {

    @Autowired
    private UserRepository userRepository;

    public List<DataLogDTO> dataLogDOToDTO(List<DataLogDO> dataLogDOList, Map<Long, StatusMapDTO> statusMapDTOMap) {
        List<DataLogDTO> dataLogDTOList = new ArrayList<>(dataLogDOList.size());
        List<Long> createByIds = dataLogDOList.stream().filter(dataLogDO -> dataLogDO.getCreatedBy() != null && !Objects.equals(dataLogDO.getCreatedBy(), 0L)).map(DataLogDO::getCreatedBy).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(createByIds, true);
        for (DataLogDO dataLogDO : dataLogDOList) {
            DataLogDTO dataLogDTO = new DataLogDTO();
            BeanUtils.copyProperties(dataLogDO, dataLogDTO);
            UserMessageDO userMessageDO = usersMap.get(dataLogDO.getCreatedBy());
            String name = userMessageDO != null ? userMessageDO.getName() : null;
            String loginName = userMessageDO != null ? userMessageDO.getLoginName() : null;
            String realName = userMessageDO != null ? userMessageDO.getRealName() : null;
            String imageUrl = userMessageDO != null ? userMessageDO.getImageUrl() : null;
            String email = userMessageDO != null ? userMessageDO.getEmail() : null;
            dataLogDTO.setName(name);
            dataLogDTO.setLoginName(loginName);
            dataLogDTO.setRealName(realName);
            dataLogDTO.setImageUrl(imageUrl);
            dataLogDTO.setEmail(email);
            if ("status".equals(dataLogDO.getField())) {
                StatusMapDTO statusMapDTO = statusMapDTOMap.get(Long.parseLong(dataLogDO.getNewValue()));
                dataLogDTO.setCategoryCode(statusMapDTO != null ? statusMapDTO.getType() : null);
            }
            dataLogDTOList.add(dataLogDTO);
        }
        return dataLogDTOList;
    }

}
