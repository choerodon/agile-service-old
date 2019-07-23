package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.StateMachineNodeService;
import io.choerodon.agile.app.service.StatusService;
import io.choerodon.agile.infra.cache.InstanceCache;
import io.choerodon.agile.infra.dataobject.StateMachineNodeDTO;
import io.choerodon.agile.infra.dataobject.StatusDTO;
import io.choerodon.agile.infra.dataobject.StatusWithInfoDTO;
import io.choerodon.agile.infra.enums.NodeType;
import io.choerodon.agile.infra.enums.StatusType;
import io.choerodon.agile.infra.exception.RemoveStatusException;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.utils.EnumUtil;
import io.choerodon.agile.infra.utils.PageUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Service
public class StatusServiceImpl implements StatusService {
    @Autowired
    private StatusMapper statusMapper;
    @Autowired
    private StateMachineNodeDraftMapper nodeDraftMapper;
    @Autowired
    private StateMachineNodeMapper nodeDeployMapper;
    @Autowired
    private StateMachineNodeService nodeService;
    @Autowired
    private StateMachineTransformDraftMapper transformDraftMapper;
    @Autowired
    private StateMachineTransformMapper transformDeployMapper;
    @Autowired
    private InstanceCache instanceCache;
    @Autowired
    private StateMachineMapper stateMachineMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PageInfo<StatusWithInfoVO> queryStatusList(PageRequest pageRequest, Long organizationId, StatusSearchVO statusSearchVO) {
        PageInfo<Long> statusIdsPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort()))
                .doSelectPageInfo(() -> statusMapper.selectStatusIds(organizationId, statusSearchVO));
        List<StatusWithInfoVO> statusWithInfoVOList = new ArrayList<>();
        if (!statusIdsPage.getList().isEmpty()) {
            List<StatusWithInfoDTO> statuses = statusMapper.queryStatusList(organizationId, statusIdsPage.getList());
            statusWithInfoVOList = modelMapper.map(statuses, new TypeToken<List<StatusWithInfoVO>>() {
            }.getType());
        }
        return PageUtil.buildPageInfoWithPageInfoList(statusIdsPage, statusWithInfoVOList);
    }

    @Override
    public StatusVO create(Long organizationId, StatusVO statusVO) {
        if (checkName(organizationId, statusVO.getName()).getStatusExist()) {
            throw new CommonException("error.statusName.exist");
        }
        if (!EnumUtil.contain(StatusType.class, statusVO.getType())) {
            throw new CommonException("error.status.type.illegal");
        }
        statusVO.setOrganizationId(organizationId);
        StatusDTO status = modelMapper.map(statusVO, StatusDTO.class);
        List<StatusDTO> select = statusMapper.select(status);
        if (select.isEmpty()) {
            int isInsert = statusMapper.insert(status);
            if (isInsert != 1) {
                throw new CommonException("error.status.create");
            }
        } else {
            status = select.get(0);
        }
        status = statusMapper.queryById(organizationId, status.getId());
        return modelMapper.map(status, StatusVO.class);
    }

    private Boolean checkNameUpdate(Long organizationId, Long statusId, String name) {
        StatusDTO status = new StatusDTO();
        status.setOrganizationId(organizationId);
        status.setName(name);
        StatusDTO res = statusMapper.selectOne(status);
        return res != null && !statusId.equals(res.getId());
    }

    @Override
    public StatusVO update(StatusVO statusVO) {
        if (checkNameUpdate(statusVO.getOrganizationId(), statusVO.getId(), statusVO.getName())) {
            throw new CommonException("error.statusName.exist");
        }
        if (!EnumUtil.contain(StatusType.class, statusVO.getType())) {
            throw new CommonException("error.status.type.illegal");
        }
        StatusDTO status = modelMapper.map(statusVO, StatusDTO.class);
        int isUpdate = statusMapper.updateByPrimaryKeySelective(status);
        if (isUpdate != 1) {
            throw new CommonException("error.status.update");
        }
        status = statusMapper.queryById(status.getOrganizationId(), status.getId());
        return modelMapper.map(status, StatusVO.class);
    }

    @Override
    public Boolean delete(Long organizationId, Long statusId) {
        StatusDTO status = statusMapper.queryById(organizationId, statusId);
        if (status == null) {
            throw new CommonException("error.status.delete.nofound");
        }
        Long draftUsed = nodeDraftMapper.checkStateDelete(organizationId, statusId);
        Long deployUsed = nodeDeployMapper.checkStateDelete(organizationId, statusId);
        if (draftUsed != 0 || deployUsed != 0) {
            throw new CommonException("error.status.delete");
        }
        if (status.getCode() != null) {
            throw new CommonException("error.status.illegal");
        }
        int isDelete = statusMapper.deleteByPrimaryKey(statusId);
        if (isDelete != 1) {
            throw new CommonException("error.status.delete");
        }
        return true;
    }

    @Override
    public StatusInfoVO queryStatusById(Long organizationId, Long stateId) {
        StatusDTO status = statusMapper.queryById(organizationId, stateId);
        if (status == null) {
            throw new CommonException("error.queryStatusById.notExist");
        }
        return modelMapper.map(status, StatusInfoVO.class);
    }

    @Override
    public List<StatusVO> queryAllStatus(Long organizationId) {
        StatusDTO status = new StatusDTO();
        status.setOrganizationId(organizationId);
        List<StatusDTO> statuses = statusMapper.select(status);
        return modelMapper.map(statuses, new TypeToken<List<StatusVO>>() {
        }.getType());
    }

    @Override
    public Map<Long, StatusMapVO> queryAllStatusMap(Long organizationId) {
        StatusDTO status = new StatusDTO();
        status.setOrganizationId(organizationId);
        List<StatusDTO> statuses = statusMapper.select(status);
        Map<Long, StatusMapVO> statusMap = new HashMap<>();
        for (StatusDTO sta : statuses) {
            StatusMapVO statusMapVO = modelMapper.map(sta, new TypeToken<StatusMapVO>() {
            }.getType());
            statusMap.put(statusMapVO.getId(), statusMapVO);
        }
        return statusMap;
    }

    @Override
    public StatusCheckVO checkName(Long organizationId, String name) {
        StatusDTO status = new StatusDTO();
        status.setOrganizationId(organizationId);
        status.setName(name);
        StatusDTO res = statusMapper.selectOne(status);
        StatusCheckVO statusCheckVO = new StatusCheckVO();
        if (res != null) {
            statusCheckVO.setStatusExist(true);
            statusCheckVO.setId(res.getId());
            statusCheckVO.setName(res.getName());
            statusCheckVO.setType(res.getType());
        } else {
            statusCheckVO.setStatusExist(false);
        }
        return statusCheckVO;
    }

    @Override
    public Map<Long, StatusDTO> batchStatusGet(List<Long> ids) {
        if (!ids.isEmpty()) {
            List<StatusDTO> statuses = statusMapper.batchStatusGet(ids);
            Map<Long, StatusDTO> map = new HashMap();
            for (StatusDTO status : statuses) {
                map.put(status.getId(), status);
            }
            return map;
        } else {
            return new HashMap<>();
        }

    }

    @Override
    public StatusVO createStatusForAgile(Long organizationId, Long stateMachineId, StatusVO statusVO) {
        if (stateMachineId == null) {
            throw new CommonException("error.stateMachineId.notNull");
        }
        if (stateMachineMapper.queryById(organizationId, stateMachineId) == null) {
            throw new CommonException("error.stateMachine.notFound");
        }

        String statusName = statusVO.getName();
        StatusDTO select = new StatusDTO();
        select.setName(statusName);
        select.setOrganizationId(organizationId);
        List<StatusDTO> list = statusMapper.select(select);
        if (list.isEmpty()) {
            statusVO = create(organizationId, statusVO);
        } else {
            statusVO = modelMapper.map(list.get(0), StatusVO.class);
        }
        //将状态加入状态机中，直接加到发布表中
        nodeService.createNodeAndTransformForAgile(organizationId, stateMachineId, statusVO);
        //清理状态机实例
        instanceCache.cleanStateMachine(stateMachineId);
        return statusVO;
    }

    @Override
    public void removeStatusForAgile(Long organizationId, Long stateMachineId, Long statusId) {
        if (statusId == null) {
            throw new CommonException("error.statusId.notNull");
        }
        StateMachineNodeDTO stateNode = new StateMachineNodeDTO();
        stateNode.setOrganizationId(organizationId);
        stateNode.setStateMachineId(stateMachineId);
        stateNode.setStatusId(statusId);
        StateMachineNodeDTO res = nodeDeployMapper.selectOne(stateNode);
        if (res == null) {
            throw new RemoveStatusException("error.status.exist");
        }
        if (res.getType().equals(NodeType.INIT)) {
            throw new RemoveStatusException("error.status.illegal");
        }
        if (res.getId() != null) {
            //删除节点
            nodeDeployMapper.deleteByPrimaryKey(res.getId());
            //删除节点关联的转换
            transformDeployMapper.deleteByNodeId(res.getId());
            //删除节点
            nodeDraftMapper.deleteByPrimaryKey(res.getId());
            //删除节点关联的转换
            transformDraftMapper.deleteByNodeId(res.getId());
        }
        //清理状态机实例
        instanceCache.cleanStateMachine(stateMachineId);
    }

    @Override
    public List<StatusVO> queryByStateMachineIds(Long organizationId, List<Long> stateMachineIds) {
        if (!stateMachineIds.isEmpty()) {
            List<StatusDTO> statuses = statusMapper.queryByStateMachineIds(organizationId, stateMachineIds);
            return modelMapper.map(statuses, new TypeToken<List<StatusVO>>() {
            }.getType());
        }
        return Collections.emptyList();
    }
}
