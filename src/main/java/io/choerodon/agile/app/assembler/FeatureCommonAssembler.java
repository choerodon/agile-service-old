package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.FeatureCommonDTO;
import io.choerodon.agile.api.vo.IssueTypeDTO;
import io.choerodon.agile.api.vo.PiNameVO;
import io.choerodon.agile.api.vo.StatusMapVO;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.FeatureCommonDO;
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

    public List<FeatureCommonDTO> featureCommonDOToDTO(List<FeatureCommonDO> featureCommonDOList, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<FeatureCommonDTO> result = new ArrayList<>();
        List<Long> reporterIds = new ArrayList<>();
        reporterIds.addAll(featureCommonDOList.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(FeatureCommonDO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDO> userMessageDOMap = userRepository.queryUsersMap(
                reporterIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        featureCommonDOList.forEach(featureCommonDO -> {
            FeatureCommonDTO featureCommonDTO = ConvertHelper.convert(featureCommonDO, FeatureCommonDTO.class);
            featureCommonDTO.setPiNameVOList(ConvertHelper.convertList(featureCommonDO.getPiNameDTOList(), PiNameVO.class));
            featureCommonDTO.setStatusMapVO(statusMapDTOMap.get(featureCommonDO.getStatusId()));
            featureCommonDTO.setIssueTypeDTO(issueTypeDTOMap.get(featureCommonDO.getIssueTypeId()));
            UserMessageDO userMessageDO = userMessageDOMap.get(featureCommonDO.getReporterId());
            if (userMessageDO != null) {
                featureCommonDTO.setReporterName(userMessageDO.getName());
                featureCommonDTO.setReporterImageUrl(userMessageDO.getImageUrl());
            }
            result.add(featureCommonDTO);
        });
        return result;
    }
}
