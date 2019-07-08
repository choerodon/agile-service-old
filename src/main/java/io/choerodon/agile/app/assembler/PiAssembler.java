package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.IssueTypeDTO;
import io.choerodon.agile.api.vo.PiWithFeatureDTO;
import io.choerodon.agile.api.vo.StatusMapDTO;
import io.choerodon.agile.api.vo.SubFeatureDTO;
import io.choerodon.agile.infra.dataobject.PiWithFeatureDO;
import io.choerodon.agile.infra.dataobject.SubFeatureDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/18.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiAssembler {

    public List<SubFeatureDTO> subFeatureDOTODTO(List<SubFeatureDO> subFeatureDOList,
                                                 Map<Long, StatusMapDTO> statusMapDTOMap,
                                                 Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<SubFeatureDTO> subFeatureDTOList = new ArrayList<>();
        subFeatureDOList.forEach(subFeatureDO -> {
            SubFeatureDTO subFeatureDTO = new SubFeatureDTO();
            BeanUtils.copyProperties(subFeatureDO, subFeatureDTO);
            subFeatureDTO.setStatusMapDTO(statusMapDTOMap.get(subFeatureDO.getStatusId()));
            subFeatureDTO.setIssueTypeDTO(issueTypeDTOMap.get(subFeatureDO.getIssueTypeId()));
            subFeatureDTOList.add(subFeatureDTO);
        });
        return subFeatureDTOList;
    }

    public List<PiWithFeatureDTO> piWithFeatureDOTODTO(List<PiWithFeatureDO> piWithFeatureDOList,
                                                       Map<Long, StatusMapDTO> statusMapDTOMap,
                                                       Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<PiWithFeatureDTO> piWithFeatureDTOList = new ArrayList<>();
        piWithFeatureDOList.forEach(piWithFeatureDO -> {
            PiWithFeatureDTO piWithFeatureDTO = new PiWithFeatureDTO();
            BeanUtils.copyProperties(piWithFeatureDO, piWithFeatureDTO);
            if (piWithFeatureDO.getSubFeatureDOList() != null && !piWithFeatureDO.getSubFeatureDOList().isEmpty()) {
                List<SubFeatureDO> subFeatureDOList = piWithFeatureDO.getSubFeatureDOList();
                piWithFeatureDTO.setSubFeatureDTOList(subFeatureDOTODTO(subFeatureDOList, statusMapDTOMap, issueTypeDTOMap));
            } else {
                piWithFeatureDTO.setSubFeatureDTOList(new ArrayList<>());
            }
            piWithFeatureDTOList.add(piWithFeatureDTO);
        });
        return piWithFeatureDTOList;
    }

}
