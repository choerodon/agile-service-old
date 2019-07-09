package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.StatusMapVO;
import io.choerodon.agile.api.vo.StoryMapStoryDTO;
import io.choerodon.agile.api.vo.StoryMapVersionDTO;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.StoryMapStoryDO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/6.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StoryMapAssembler extends AbstractAssembler {

    private static final String APPLYTYPE_AGILE = "agile";

    public List<StoryMapStoryDTO> storyMapStoryDOToDTO(Long projectId, List<StoryMapStoryDO> storyMapStoryDOList) {
        if (storyMapStoryDOList == null || storyMapStoryDOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<StoryMapStoryDTO> result = new ArrayList<>();
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, APPLYTYPE_AGILE);
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        storyMapStoryDOList.forEach(storyMapStoryDO -> {
            StoryMapStoryDTO storyMapStoryDTO = toTarget(storyMapStoryDO, StoryMapStoryDTO.class);
            storyMapStoryDTO.setIssueTypeVO(issueTypeDTOMap.get(storyMapStoryDO.getIssueTypeId()));
            storyMapStoryDTO.setStatusMapVO(statusMapDTOMap.get(storyMapStoryDO.getStatusId()));
            storyMapStoryDTO.setStoryMapVersionDTOList(toTargetList(storyMapStoryDO.getStoryMapVersionDOList(), StoryMapVersionDTO.class));
            result.add(storyMapStoryDTO);
        });
        return result;
    }

}
