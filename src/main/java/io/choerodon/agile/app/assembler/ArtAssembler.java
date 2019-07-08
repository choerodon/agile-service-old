package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.ArtVO;
import io.choerodon.agile.api.vo.PiCalendarVO;
import io.choerodon.agile.api.vo.SprintCalendarVO;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.ArtDTO;
import io.choerodon.agile.infra.dataobject.PiCalendarDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ArtAssembler {

    @Autowired
    private UserRepository userRepository;

    public ArtVO artDOTODTO(ArtDTO artDTO) {
        ArtVO artVO = ConvertHelper.convert(artDTO, ArtVO.class);
        List<Long> rteIds = new ArrayList<>();
        if (artDTO.getRteId() != null) {
            rteIds.add(artDTO.getRteId());
        }
        if (rteIds.isEmpty()) {
            return artVO;
        }
        Map<Long, UserMessageDO> userMessageDOMap = userRepository.queryUsersMap(
                rteIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        UserMessageDO userMessageDO = userMessageDOMap.get(artDTO.getRteId());
        String rteName = userMessageDO != null ? userMessageDO.getLoginName() + userMessageDO.getName() : null;
        artVO.setRteName(rteName);
        return artVO;
    }

    public List<PiCalendarVO> piCalendarDOToDTO(List<PiCalendarDTO> piCalendarDTOList) {
        List<PiCalendarVO> result = new ArrayList<>();
        piCalendarDTOList.forEach(piCalendarDTO -> {
            PiCalendarVO piCalendarVO = new PiCalendarVO();
            BeanUtils.copyProperties(piCalendarDTO, piCalendarVO);
            if (piCalendarDTO.getSprintCalendarDTOList() != null && !piCalendarDTO.getSprintCalendarDTOList().isEmpty()) {
                piCalendarVO.setSprintCalendarVOList(ConvertHelper.convertList(piCalendarDTO.getSprintCalendarDTOList(), SprintCalendarVO.class));
            }
            result.add(piCalendarVO);
        });
        return result;
    }
}
