//package io.choerodon.agile.app.assembler;
//
//import io.choerodon.agile.api.vo.IssueTypeVO;
//import io.choerodon.agile.api.vo.PriorityDTO;
//import io.choerodon.agile.api.vo.StatusMapVO;
//import io.choerodon.agile.api.vo.StoryMapIssueDTO;
//import io.choerodon.agile.infra.dataobject.LookupValueDTO;
//import io.choerodon.agile.infra.repository.UserRepository;
//import io.choerodon.agile.infra.common.utils.ColorUtil;
//import io.choerodon.agile.infra.dataobject.StoryMapIssueDO;
//import io.choerodon.agile.infra.dataobject.UserMessageDO;
//import io.choerodon.agile.infra.mapper.LookupValueMapper;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/8/8.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class StoryMapIssueAssembler {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private LookupValueMapper lookupValueMapper;
//
//    private static final String ISSUE_STATUS_COLOR = "issue_status_color";
//
//    public List<StoryMapIssueDTO> storyMapIssueDOToDTO(List<StoryMapIssueDO> storyMapIssueDOList, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
//        LookupValueDTO lookupValueDTO = new LookupValueDTO();
//        lookupValueDTO.setTypeCode(ISSUE_STATUS_COLOR);
//        List<StoryMapIssueDTO> storyMapIssueDTOList = new ArrayList<>(storyMapIssueDOList.size());
//        Map<String, String> lookupValueMap = lookupValueMapper.select(lookupValueDTO).stream().collect(Collectors.toMap(LookupValueDTO::getValueCode, LookupValueDTO::getName));
//        List<Long> assigneeIds = storyMapIssueDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(StoryMapIssueDO::getAssigneeId).distinct().collect(Collectors.toList());
//        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
//        storyMapIssueDOList.forEach(storyMapIssueDO -> {
//            String assigneeName = usersMap.get(storyMapIssueDO.getAssigneeId()) != null ? usersMap.get(storyMapIssueDO.getAssigneeId()).getName() : null;
//            String imageUrl = assigneeName != null ? usersMap.get(storyMapIssueDO.getAssigneeId()).getImageUrl() : null;
//            StoryMapIssueDTO storyMapIssueDTO = new StoryMapIssueDTO();
//            BeanUtils.copyProperties(storyMapIssueDO, storyMapIssueDTO);
//            storyMapIssueDTO.setStatusColor(ColorUtil.initializationStatusColor(storyMapIssueDTO.getStatusCode(), lookupValueMap));
//            storyMapIssueDTO.setAssigneeName(assigneeName);
//            storyMapIssueDTO.setImageUrl(imageUrl);
//            storyMapIssueDTO.setPriorityDTO(priorityMap.get(storyMapIssueDO.getPriorityId()));
//            storyMapIssueDTO.setIssueTypeVO(issueTypeDTOMap.get(storyMapIssueDO.getIssueTypeId()));
//            storyMapIssueDTO.setStatusMapVO(statusMapDTOMap.get(storyMapIssueDTO.getStatusId()));
//            storyMapIssueDTOList.add(storyMapIssueDTO);
//        });
//        return storyMapIssueDTOList;
//    }
//}
