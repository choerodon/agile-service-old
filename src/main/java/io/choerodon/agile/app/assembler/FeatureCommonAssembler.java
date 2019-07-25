package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.FeatureCommonVO;
import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.PiNameVO;
import io.choerodon.agile.api.vo.StatusVO;
import io.choerodon.agile.infra.dataobject.FeatureCommonDTO;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeatureCommonAssembler {

    @Autowired
    private UserService userService;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public List<FeatureCommonVO> featureCommonDTOToVO(List<FeatureCommonDTO> featureCommonDTOList, Map<Long, StatusVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<FeatureCommonVO> result = new ArrayList<>();
        List<Long> reporterIds = new ArrayList<>();
        reporterIds.addAll(featureCommonDTOList.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(FeatureCommonDTO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDTO> userMessageDOMap = userService.queryUsersMap(
                reporterIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        featureCommonDTOList.forEach(featureCommonDO -> {
            FeatureCommonVO featureCommonVO = modelMapper.map(featureCommonDO, FeatureCommonVO.class);
            featureCommonVO.setPiNameVOList(modelMapper.map(featureCommonDO.getPiNameDTOList(), new TypeToken<List<PiNameVO>>(){}.getType()));
            featureCommonVO.setStatusVO(statusMapDTOMap.get(featureCommonDO.getStatusId()));
            featureCommonVO.setIssueTypeVO(issueTypeDTOMap.get(featureCommonDO.getIssueTypeId()));
            UserMessageDTO userMessageDTO = userMessageDOMap.get(featureCommonDO.getReporterId());
            if (userMessageDTO != null) {
                featureCommonVO.setReporterName(userMessageDTO.getName());
                featureCommonVO.setReporterImageUrl(userMessageDTO.getImageUrl());
            }
            result.add(featureCommonVO);
        });
        return result;
    }
}
