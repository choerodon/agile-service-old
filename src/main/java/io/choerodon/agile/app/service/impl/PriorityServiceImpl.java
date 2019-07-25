package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.PriorityVO;
import io.choerodon.agile.api.vo.ProjectVO;
import io.choerodon.agile.app.service.IssueAccessDataService;
import io.choerodon.agile.app.service.PriorityService;
import io.choerodon.agile.infra.dataobject.PriorityDTO;
import io.choerodon.agile.infra.feign.IamFeignClient;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.PriorityMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.entity.Criteria;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @since 2019/3/4
 */
@Service
public class PriorityServiceImpl implements PriorityService {
    private static final String NOT_FOUND = "error.priority.notFound";
    private static final String DELETE_ILLEGAL = "error.priority.deleteIllegal";
    private static final String LAST_ILLEGAL = "error.priority.lastIllegal";
    @Autowired
    private PriorityMapper priorityMapper;
    @Autowired
    private IamFeignClient iamFeignClient;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueAccessDataService issueAccessDataService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PriorityService priorityService;

    @Override
    public List<PriorityVO> selectAll(PriorityVO priorityVO, String param) {
        PriorityDTO priority = modelMapper.map(priorityVO, PriorityDTO.class);
        List<PriorityDTO> priorities = priorityMapper.fulltextSearch(priority, param);
        return modelMapper.map(priorities, new TypeToken<List<PriorityVO>>() {
        }.getType());
    }

    @Override
    public PriorityVO create(Long organizationId, PriorityVO priorityVO) {
        if (checkName(organizationId, priorityVO.getName())) {
            throw new CommonException("error.priority.create.name.same");
        }
        priorityVO.setSequence((priorityMapper.getNextSequence(organizationId)).add(new BigDecimal(1)));
        priorityVO.setOrganizationId(organizationId);
        //若设置为默认值，则清空其他默认值
        if (priorityVO.getDefault() != null && priorityVO.getDefault()) {
            priorityMapper.cancelDefaultPriority(organizationId);
        } else {
            priorityVO.setDefault(false);
        }
        PriorityDTO priority = modelMapper.map(priorityVO, PriorityDTO.class);
        priority.setEnable(true);
        int isInsert = priorityMapper.insert(priority);
        if (isInsert != 1) {
            throw new CommonException("error.priority.create");
        }
        priority = priorityMapper.selectByPrimaryKey(priority);
        return modelMapper.map(priority, PriorityVO.class);
    }

    private Boolean checkNameUpdate(Long organizationId, Long priorityId, String name) {
        PriorityDTO priority = new PriorityDTO();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        PriorityDTO res = priorityMapper.selectOne(priority);
        return res != null && !priorityId.equals(res.getId());
    }

    @Override
    public PriorityVO update(PriorityVO priorityVO) {
        if (checkNameUpdate(priorityVO.getOrganizationId(), priorityVO.getId(), priorityVO.getName())) {
            throw new CommonException("error.priority.update.name.same");
        }
        PriorityDTO priority = modelMapper.map(priorityVO, PriorityDTO.class);
        //若设置为默认值，则清空其他默认值
        if (priorityVO.getDefault() != null && priorityVO.getDefault()) {
            priorityMapper.cancelDefaultPriority(priorityVO.getOrganizationId());
        } else {
            //如果只有一个默认优先级时，无法取消当前默认优先级
            PriorityDTO select = new PriorityDTO();
            select.setDefault(true);
            select.setOrganizationId(priorityVO.getOrganizationId());
            PriorityDTO result = priorityMapper.selectOne(select);
            if (result.getId().equals(priority.getId())) {
                throw new CommonException("error.priority.illegal");
            }
        }
        int isUpdate = priorityMapper.updateByPrimaryKeySelective(priority);
        if (isUpdate != 1) {
            throw new CommonException("error.priority.update");
        }
        priority = priorityMapper.selectByPrimaryKey(priority);
        return modelMapper.map(priority, PriorityVO.class);
    }

    @Override
    public Boolean checkName(Long organizationId, String name) {
        PriorityDTO priority = new PriorityDTO();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        PriorityDTO res = priorityMapper.selectOne(priority);
        return res != null;
    }

    @Override
    public List<PriorityVO> updateByList(List<PriorityVO> list, Long organizationId) {
        int seq = 1;
        for (PriorityVO priorityVO : list) {
            PriorityDTO p = modelMapper.map(priorityVO, PriorityDTO.class);
            p.setSequence(new BigDecimal(seq));
            seq++;
            int isUpdate = priorityMapper.updateSequenceById(p);
            if (isUpdate != 1) {
                throw new CommonException("error.priority.update");
            }
        }
        List<PriorityDTO> priorities = priorityMapper.fulltextSearch(new PriorityDTO(), null);
        return modelMapper.map(priorities, new TypeToken<List<PriorityVO>>() {
        }.getType());
    }

    @Override
    public Map<Long, PriorityVO> queryByOrganizationId(Long organizationId) {
        PriorityDTO priority = new PriorityDTO();
        priority.setOrganizationId(organizationId);
        List<PriorityDTO> priorities = priorityMapper.select(priority);
        Map<Long, PriorityVO> result = new HashMap<>();
        for (PriorityDTO pri : priorities) {
            PriorityVO priorityVO = modelMapper.map(pri, new TypeToken<PriorityVO>() {
            }.getType());
            result.put(priorityVO.getId(), priorityVO);
        }
        return result;
    }

    @Override
    public PriorityVO queryDefaultByOrganizationId(Long organizationId) {
        PriorityDTO priority = new PriorityDTO();
        priority.setOrganizationId(organizationId);
        priority.setDefault(true);
        PriorityDTO result = priorityMapper.selectOne(priority);
        if (result == null) {
            throw new CommonException(NOT_FOUND);
        }
        return modelMapper.map(result, new TypeToken<PriorityVO>() {
        }.getType());
    }

    @Override
    public List<PriorityVO> queryByOrganizationIdList(Long organizationId) {
        PriorityDTO priority = new PriorityDTO();
        priority.setOrganizationId(organizationId);
        List<PriorityDTO> priorities = priorityMapper.select(priority);
        Collections.sort(priorities, Comparator.comparing(PriorityDTO::getSequence));
        return modelMapper.map(priorities, new TypeToken<List<PriorityVO>>() {
        }.getType());
    }


    @Override
    public PriorityVO queryById(Long organizationId, Long id) {
        PriorityDTO result = priorityMapper.selectByPrimaryKey(id);
        if (result == null) {
            throw new CommonException("error.priority.get");
        }
        return modelMapper.map(result, new TypeToken<PriorityVO>() {
        }.getType());
    }

    private PriorityDTO savePrority(Long organizationId, String name, BigDecimal sequence, String colour, Boolean isDefault) {
        PriorityDTO priority = new PriorityDTO();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        priority.setSequence(sequence);
        priority.setColour(colour);
        priority.setDescription(name);
        priority.setDefault(isDefault);
        priority.setEnable(true);
        //保证幂等性
        List<PriorityDTO> list = priorityMapper.select(priority);
        if (list.isEmpty()) {
            if (priorityMapper.insert(priority) != 1) {
                throw new CommonException("error.prority.insert");
            }
        } else {
            priority = list.get(0);
        }

        return priority;
    }

    private Map<String, Long> initPrority(Long organizationId) {
        Map<String, Long> map = new HashMap<>();
        PriorityDTO high = savePrority(organizationId, "高", new BigDecimal(0), "#FFB100", false);
        PriorityDTO medium = savePrority(organizationId, "中", new BigDecimal(1), "#3575DF", true);
        PriorityDTO low = savePrority(organizationId, "低", new BigDecimal(2), "#979797", false);
        map.put("high", high.getId());
        map.put("medium", medium.getId());
        map.put("low", low.getId());
        return map;
    }

    @Override
    public Map<Long, Map<String, Long>> initProrityByOrganization(List<Long> organizationIds) {
        Map<Long, Map<String, Long>> result = new HashMap<>(organizationIds.size());
        for (Long organizationId : organizationIds) {
            result.put(organizationId, initPrority(organizationId));
        }
        return result;
    }

    @Override
    public PriorityVO enablePriority(Long organizationId, Long id, Boolean enable) {
        if (!enable) {
            checkLastPriority(organizationId, id);
        }
        PriorityDTO priority = priorityMapper.selectByPrimaryKey(id);
        if (priority == null) {
            throw new CommonException(NOT_FOUND);
        }
        priority.setEnable(enable);
        Criteria criteria = new Criteria();
        criteria.update("enable");
        priorityMapper.updateByPrimaryKeyOptions(priority, criteria);
        //失效之后再进行默认优先级的重置
        if (!enable && priority.getDefault()) {
            updateOtherDefault(organizationId);
        }
        return queryById(organizationId, id);
    }

    @Override
    public Long checkDelete(Long organizationId, Long id) {
        //查询出组织下的所有项目
        List<ProjectVO> projectVOS = iamFeignClient.queryProjectsByOrgId(organizationId, 1, 0).getBody().getList();
        List<Long> projectIds = projectVOS.stream().map(ProjectVO::getId).collect(Collectors.toList());
        Long count;
        if (projectIds == null || projectIds.isEmpty()) {
            count = 0L;
        } else {
            count = priorityService.checkPriorityDelete(organizationId, id, projectIds);
        }
        return count;
    }

    @Override
    public Boolean delete(Long organizationId, Long priorityId, Long changePriorityId) {
        if (priorityId.equals(changePriorityId)) {
            throw new CommonException(DELETE_ILLEGAL);
        }
        checkLastPriority(organizationId, priorityId);
        PriorityDTO priority = priorityMapper.selectByPrimaryKey(priorityId);
        List<ProjectVO> projectVOS = iamFeignClient.queryProjectsByOrgId(organizationId, 1, 0).getBody().getList();
        List<Long> projectIds = projectVOS.stream().map(ProjectVO::getId).collect(Collectors.toList());
        Long count;
        if (projectIds == null || projectIds.isEmpty()) {
            count = 0L;
        } else {
            count = priorityService.checkPriorityDelete(organizationId, priorityId, projectIds);
        }
        //执行优先级转换
        if (!count.equals(0L)) {
            if (changePriorityId == null) {
                throw new CommonException(DELETE_ILLEGAL);
            }
            CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
            priorityService.batchChangeIssuePriority(organizationId, priorityId, changePriorityId, customUserDetails.getUserId(), projectIds);
        }
        int isDelete = priorityMapper.deleteByPrimaryKey(priorityId);
        if (isDelete != 1) {
            throw new CommonException("error.priority.delete");
        }
        if (priority.getDefault()) {
            updateOtherDefault(organizationId);
        }
        return true;
    }

    /**
     * 操作的是最后一个有效优先级则无法删除/失效
     *
     * @param organizationId
     */
    private void checkLastPriority(Long organizationId, Long priorityId) {
        PriorityDTO priority = new PriorityDTO();
        priority.setEnable(true);
        priority.setOrganizationId(organizationId);
        List<PriorityDTO> priorities = priorityMapper.select(priority);
        if (priorities.size() == 1 && priorityId.equals(priorities.get(0).getId())) {
            throw new CommonException(LAST_ILLEGAL);
        }
    }

    /**
     * 当执行失效/删除时，若当前是默认优先级，则取消当前默认优先级，并设置第一个为默认优先级，要放在方法最后执行
     *
     * @param organizationId
     */
    private synchronized void updateOtherDefault(Long organizationId) {
        priorityMapper.cancelDefaultPriority(organizationId);
        priorityMapper.updateMinSeqAsDefault(organizationId);
    }

    @Override
    public Long checkPriorityDelete(Long organizationId, Long priorityId, List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return 0L;
        } else {
            return issueMapper.checkPriorityDelete(priorityId, projectIds);
        }
    }

    @Override
    public void batchChangeIssuePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds) {
        if (projectIds != null && !projectIds.isEmpty()) {
            issueAccessDataService.batchUpdateIssuePriority(organizationId, priorityId, changePriorityId, userId, projectIds);
        }
    }
}
