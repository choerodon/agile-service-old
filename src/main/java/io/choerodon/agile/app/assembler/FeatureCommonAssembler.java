package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.FeatureCommonVO;
import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.PiNameVO;
import io.choerodon.agile.api.vo.StatusMapVO;
import io.choerodon.agile.infra.dataobject.FeatureCommonDTO;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private UserRepository userRepository;

    public List<FeatureCommonVO> featureCommonDOToDTO(List<FeatureCommonDTO> featureCommonDTOList, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<FeatureCommonVO> result = new ArrayList<>();
        List<Long> reporterIds = new ArrayList<>();
        reporterIds.addAll(featureCommonDTOList.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(FeatureCommonDTO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDO> userMessageDOMap = userRepository.queryUsersMap(
                reporterIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        featureCommonDTOList.forEach(featureCommonDO -> {
            FeatureCommonVO featureCommonVO = ConvertHelper.convert(featureCommonDO, FeatureCommonVO.class);
            featureCommonVO.setPiNameVOList(ConvertHelper.convertList(featureCommonDO.getPiNameDTOList(), PiNameVO.class));
            featureCommonVO.setStatusMapVO(statusMapDTOMap.get(featureCommonDO.getStatusId()));
            featureCommonVO.setIssueTypeVO(issueTypeDTOMap.get(featureCommonDO.getIssueTypeId()));
            UserMessageDO userMessageDO = userMessageDOMap.get(featureCommonDO.getReporterId());
            if (userMessageDO != null) {
                featureCommonVO.setReporterName(userMessageDO.getName());
                featureCommonVO.setReporterImageUrl(userMessageDO.getImageUrl());
            }
            result.add(featureCommonVO);
        });
        return result;
    }
}
