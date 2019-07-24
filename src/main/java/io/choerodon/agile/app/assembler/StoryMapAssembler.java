package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.StatusVO;
import io.choerodon.agile.api.vo.StoryMapStoryVO;
import io.choerodon.agile.api.vo.StoryMapVersionVO;
import io.choerodon.agile.infra.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.StoryMapStoryDTO;
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

    public List<StoryMapStoryVO> storyMapStoryDTOToVO(Long projectId, List<StoryMapStoryDTO> storyMapStoryDTOList) {
        if (storyMapStoryDTOList == null || storyMapStoryDTOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<StoryMapStoryVO> result = new ArrayList<>();
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, APPLYTYPE_AGILE);
        Map<Long, StatusVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        storyMapStoryDTOList.forEach(storyMapStoryDTO -> {
            StoryMapStoryVO storyMapStoryVO = toTarget(storyMapStoryDTO, StoryMapStoryVO.class);
            storyMapStoryVO.setIssueTypeVO(issueTypeDTOMap.get(storyMapStoryDTO.getIssueTypeId()));
            storyMapStoryVO.setStatusVO(statusMapDTOMap.get(storyMapStoryDTO.getStatusId()));
            storyMapStoryVO.setStoryMapVersionVOList(toTargetList(storyMapStoryDTO.getStoryMapVersionDTOList(), StoryMapVersionVO.class));
            result.add(storyMapStoryVO);
        });
        return result;
    }

}
