package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.IssueTypeDTO;
import io.choerodon.agile.api.vo.PiWithFeatureVO;
import io.choerodon.agile.api.vo.StatusMapDTO;
import io.choerodon.agile.infra.dataobject.PiWithFeatureDTO;
import io.choerodon.agile.infra.dataobject.SubFeatureDTO;
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

    public List<io.choerodon.agile.api.vo.SubFeatureDTO> subFeatureDOTODTO(List<SubFeatureDTO> subFeatureDOList,
                                                                           Map<Long, StatusMapDTO> statusMapDTOMap,
                                                                           Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<io.choerodon.agile.api.vo.SubFeatureDTO> subFeatureDTOList = new ArrayList<>();
        subFeatureDOList.forEach(subFeatureDO -> {
            io.choerodon.agile.api.vo.SubFeatureDTO subFeatureDTO = new io.choerodon.agile.api.vo.SubFeatureDTO();
            BeanUtils.copyProperties(subFeatureDO, subFeatureDTO);
            subFeatureDTO.setStatusMapDTO(statusMapDTOMap.get(subFeatureDO.getStatusId()));
            subFeatureDTO.setIssueTypeDTO(issueTypeDTOMap.get(subFeatureDO.getIssueTypeId()));
            subFeatureDTOList.add(subFeatureDTO);
        });
        return subFeatureDTOList;
    }

    public List<PiWithFeatureVO> piWithFeatureDOTODTO(List<PiWithFeatureDTO> piWithFeatureDTOList,
                                                      Map<Long, StatusMapDTO> statusMapDTOMap,
                                                      Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<PiWithFeatureVO> piWithFeatureVOList = new ArrayList<>();
        piWithFeatureDTOList.forEach(piWithFeatureDTO -> {
            PiWithFeatureVO piWithFeatureVO = new PiWithFeatureVO();
            BeanUtils.copyProperties(piWithFeatureDTO, piWithFeatureVO);
            if (piWithFeatureDTO.getSubFeatureDTOList() != null && !piWithFeatureDTO.getSubFeatureDTOList().isEmpty()) {
                List<SubFeatureDTO> subFeatureDTOList = piWithFeatureDTO.getSubFeatureDTOList();
                piWithFeatureVO.setSubFeatureDTOList(subFeatureDOTODTO(subFeatureDTOList, statusMapDTOMap, issueTypeDTOMap));
            } else {
                piWithFeatureVO.setSubFeatureDTOList(new ArrayList<>());
            }
            piWithFeatureVOList.add(piWithFeatureVO);
        });
        return piWithFeatureVOList;
    }

}
