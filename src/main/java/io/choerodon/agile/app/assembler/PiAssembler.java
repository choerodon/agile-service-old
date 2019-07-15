package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.PiWithFeatureVO;
import io.choerodon.agile.api.vo.StatusMapVO;
import io.choerodon.agile.api.vo.SubFeatureVO;
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

    public List<SubFeatureVO> subFeatureDOTODTO(List<SubFeatureDTO> subFeatureDOList,
                                                Map<Long, StatusMapVO> statusMapDTOMap,
                                                Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<SubFeatureVO> subFeatureVOList = new ArrayList<>();
        subFeatureDOList.forEach(subFeatureDO -> {
            SubFeatureVO subFeatureVO = new SubFeatureVO();
            BeanUtils.copyProperties(subFeatureDO, subFeatureVO);
            subFeatureVO.setStatusMapVO(statusMapDTOMap.get(subFeatureDO.getStatusId()));
            subFeatureVO.setIssueTypeVO(issueTypeDTOMap.get(subFeatureDO.getIssueTypeId()));
            subFeatureVOList.add(subFeatureVO);
        });
        return subFeatureVOList;
    }

    public List<PiWithFeatureVO> piWithFeatureDOTODTO(List<PiWithFeatureDTO> piWithFeatureDTOList,
                                                      Map<Long, StatusMapVO> statusMapDTOMap,
                                                      Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<PiWithFeatureVO> piWithFeatureVOList = new ArrayList<>();
        piWithFeatureDTOList.forEach(piWithFeatureDTO -> {
            PiWithFeatureVO piWithFeatureVO = new PiWithFeatureVO();
            BeanUtils.copyProperties(piWithFeatureDTO, piWithFeatureVO);
            if (piWithFeatureDTO.getSubFeatureDTOList() != null && !piWithFeatureDTO.getSubFeatureDTOList().isEmpty()) {
                List<SubFeatureDTO> subFeatureDTOList = piWithFeatureDTO.getSubFeatureDTOList();
                piWithFeatureVO.setSubFeatureVOList(subFeatureDOTODTO(subFeatureDTOList, statusMapDTOMap, issueTypeDTOMap));
            } else {
                piWithFeatureVO.setSubFeatureVOList(new ArrayList<>());
            }
            piWithFeatureVOList.add(piWithFeatureVO);
        });
        return piWithFeatureVOList;
    }

}
