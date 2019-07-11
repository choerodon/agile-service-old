package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.ArtVO;
import io.choerodon.agile.api.vo.PiCalendarVO;
import io.choerodon.agile.api.vo.SprintCalendarVO;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.dataobject.ArtDTO;
import io.choerodon.agile.infra.dataobject.PiCalendarDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;
import io.choerodon.core.convertor.ConvertHelper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private UserService userService;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public ArtVO artDOTODTO(ArtDTO artDTO) {
        ArtVO artVO = modelMapper.map(artDTO, ArtVO.class);
        List<Long> rteIds = new ArrayList<>();
        if (artDTO.getRteId() != null) {
            rteIds.add(artDTO.getRteId());
        }
        if (rteIds.isEmpty()) {
            return artVO;
        }
        Map<Long, UserMessageDTO> userMessageDOMap = userService.queryUsersMap(
                rteIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        UserMessageDTO userMessageDTO = userMessageDOMap.get(artDTO.getRteId());
        String rteName = userMessageDTO != null ? userMessageDTO.getLoginName() + userMessageDTO.getName() : null;
        artVO.setRteName(rteName);
        return artVO;
    }

    public List<PiCalendarVO> piCalendarDOToDTO(List<PiCalendarDTO> piCalendarDTOList) {
        List<PiCalendarVO> result = new ArrayList<>();
        piCalendarDTOList.forEach(piCalendarDTO -> {
            PiCalendarVO piCalendarVO = new PiCalendarVO();
            BeanUtils.copyProperties(piCalendarDTO, piCalendarVO);
            if (piCalendarDTO.getSprintCalendarDTOList() != null && !piCalendarDTO.getSprintCalendarDTOList().isEmpty()) {
                piCalendarVO.setSprintCalendarVOList(modelMapper.map(piCalendarDTO.getSprintCalendarDTOList(), new TypeToken<List<SprintCalendarVO>>(){}.getType()));
            }
            result.add(piCalendarVO);
        });
        return result;
    }
}
