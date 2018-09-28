package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.DataLogDTO;
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

    public List<DataLogDTO> dataLogDOToDTO(List<DataLogDO> dataLogDOList) {
        List<DataLogDTO> dataLogDTOList = new ArrayList<>(dataLogDOList.size());
        List<Long> lastUpdatedByIds = dataLogDOList.stream().filter(dataLogDO -> dataLogDO.getLastUpdatedBy() != null && !Objects.equals(dataLogDO.getLastUpdatedBy(), 0L)).map(DataLogDO::getLastUpdatedBy).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(lastUpdatedByIds, true);
        for (DataLogDO dataLogDO : dataLogDOList) {
            DataLogDTO dataLogDTO = new DataLogDTO();
            BeanUtils.copyProperties(dataLogDO, dataLogDTO);
            UserMessageDO userMessageDO = usersMap.get(dataLogDO.getLastUpdatedBy());
            String name = userMessageDO != null ? userMessageDO.getName() : null;
            String imageUrl = userMessageDO != null ? userMessageDO.getImageUrl() : null;
            String email = userMessageDO != null ? userMessageDO.getEmail() : null;
            dataLogDTO.setName(name);
            dataLogDTO.setImageUrl(imageUrl);
            dataLogDTO.setEmail(email);
            dataLogDTOList.add(dataLogDTO);
        }
        return dataLogDTOList;
    }

}
