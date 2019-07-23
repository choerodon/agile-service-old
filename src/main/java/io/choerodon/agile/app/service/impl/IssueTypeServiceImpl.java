package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.IssueTypeService;
import io.choerodon.agile.app.service.StateMachineSchemeConfigService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.infra.dataobject.IssueTypeDTO;
import io.choerodon.agile.infra.enums.InitIssueType;
import io.choerodon.agile.infra.mapper.IssueTypeMapper;
import io.choerodon.agile.infra.utils.PageUtil;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @Date 2018/8/8
 */
@Service
@RefreshScope
public class IssueTypeServiceImpl implements IssueTypeService {

    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private StateMachineSchemeConfigService stateMachineSchemeConfigService;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public IssueTypeVO queryById(Long organizationId, Long issueTypeId) {
        IssueTypeDTO issueType = issueTypeMapper.selectByPrimaryKey(issueTypeId);
        if (issueType != null) {
            return modelMapper.map(issueType, IssueTypeVO.class);
        }
        return null;
    }

    @Override
    public IssueTypeVO create(Long organizationId, IssueTypeVO issueTypeVO) {
        if (!checkName(organizationId, issueTypeVO.getName(), null)) {
            throw new CommonException("error.issueType.checkName");
        }
        issueTypeVO.setOrganizationId(organizationId);
        IssueTypeDTO issueType = modelMapper.map(issueTypeVO, IssueTypeDTO.class);
        return modelMapper.map(createIssueType(issueType), IssueTypeVO.class);
    }

    @Override
    public IssueTypeVO update(IssueTypeVO issueTypeVO) {
        if (issueTypeVO.getName() != null && !checkName(issueTypeVO.getOrganizationId(), issueTypeVO.getName(), issueTypeVO.getId())) {
            throw new CommonException("error.issueType.checkName");
        }
        IssueTypeDTO issueType = modelMapper.map(issueTypeVO, IssueTypeDTO.class);
        int isUpdate = issueTypeMapper.updateByPrimaryKeySelective(issueType);
        if (isUpdate != 1) {
            throw new CommonException("error.issueType.update");
        }
        issueType = issueTypeMapper.selectByPrimaryKey(issueType.getId());
        return modelMapper.map(issueType, IssueTypeVO.class);
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long issueTypeId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        IssueTypeDTO issueType = issueTypeMapper.selectByPrimaryKey(issueTypeId);
        if (issueType == null) {
            throw new CommonException("error.base.notFound");
        } else if (!issueType.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.issueType.illegal");
        }
        //判断要删除的issueType是否有使用中的issue【toDo】

        return result;
    }

    @Override
    public Boolean delete(Long organizationId, Long issueTypeId) {
        Map<String, Object> result = checkDelete(organizationId, issueTypeId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = issueTypeMapper.deleteByPrimaryKey(issueTypeId);
            if (isDelete != 1) {
                throw new CommonException("error.state.delete");
            }
            //关联删除一些东西【toDo】
        } else {
            return false;
        }
        //校验
        return true;
    }

    @Override
    public PageInfo<IssueTypeWithInfoVO> queryIssueTypeList(PageRequest pageRequest, Long organizationId, IssueTypeSearchVO issueTypeSearchVO) {
        PageInfo<Long> issuetypeIdsPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> issueTypeMapper.selectIssueTypeIds(organizationId, issueTypeSearchVO));
        List<IssueTypeWithInfoVO> issueTypeWithInfoVOList = new ArrayList<>(issuetypeIdsPage.getList().size());
        if (issuetypeIdsPage.getList() != null && !issuetypeIdsPage.getList().isEmpty()) {
            issueTypeWithInfoVOList.addAll(modelMapper.map(issueTypeMapper.queryIssueTypeList(organizationId, issuetypeIdsPage.getList()), new TypeToken<List<IssueTypeWithInfoVO>>() {
            }.getType()));
        }

        return PageUtil.buildPageInfoWithPageInfoList(issuetypeIdsPage, issueTypeWithInfoVOList);
    }

    @Override
    public Boolean checkName(Long organizationId, String name, Long id) {
        IssueTypeDTO select = new IssueTypeDTO();
        select.setName(name);
        select.setOrganizationId(organizationId);
        select = issueTypeMapper.selectOne(select);
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }

    @Override
    public List<IssueTypeVO> queryByOrgId(Long organizationId) {
        List<IssueTypeDTO> issueTypes = issueTypeMapper.queryByOrgId(organizationId);
        return modelMapper.map(issueTypes, new TypeToken<List<IssueTypeVO>>() {
        }.getType());
    }

    @Override
    public List<IssueTypeVO> queryIssueTypeByStateMachineSchemeId(Long organizationId, Long schemeId) {
        List<IssueTypeVO> issueTypeVOS = queryByOrgId(organizationId);
        List<StateMachineSchemeConfigVO> configVOS = stateMachineSchemeConfigService.queryBySchemeId(true, organizationId, schemeId);
        Map<Long, StateMachineSchemeConfigVO> configMap = configVOS.stream().collect(Collectors.toMap(StateMachineSchemeConfigVO::getIssueTypeId, x -> x));
        for (IssueTypeVO issueTypeVO : issueTypeVOS) {
            StateMachineSchemeConfigVO configVO = configMap.get(issueTypeVO.getId());
            if (configVO != null) {
                StateMachineVO stateMachineVO = stateMachineService.queryStateMachineById(organizationId, configVO.getStateMachineId());
                issueTypeVO.setStateMachineName(stateMachineVO.getName());
                issueTypeVO.setStateMachineId(stateMachineVO.getId());
            }
        }
        return issueTypeVOS;
    }

    @Override
    public void initIssueTypeByConsumeCreateOrganization(Long organizationId) {
        for (InitIssueType initIssueType : InitIssueType.values()) {
            //创建默认问题类型
            createIssueType(new IssueTypeDTO(initIssueType.getIcon(), initIssueType.getName(), initIssueType.getDescription(), organizationId, initIssueType.getColour(), initIssueType.getTypeCode(), true));
        }
    }


    private IssueTypeDTO createIssueType(IssueTypeDTO issueType) {
        //保证幂等性
        List<IssueTypeDTO> issueTypes = issueTypeMapper.select(issueType);
        if (!issueTypes.isEmpty()) {
            return issueTypes.get(0);
        }

        if (issueTypeMapper.insert(issueType) != 1) {
            throw new CommonException("error.issueType.create");
        }
        return issueTypeMapper.selectByPrimaryKey(issueType);
    }

    @Override
    public Map<Long, IssueTypeVO> listIssueTypeMap(Long organizationId) {
        IssueTypeDTO issueType = new IssueTypeDTO();
        issueType.setOrganizationId(organizationId);
        List<IssueTypeDTO> issueTypes = issueTypeMapper.select(issueType);
        Map<Long, IssueTypeVO> issueTypeVOMap = new HashMap<>();
        for (IssueTypeDTO iType : issueTypes) {
            issueTypeVOMap.put(iType.getId(), modelMapper.map(iType, new TypeToken<IssueTypeVO>() {
            }.getType()));
        }
        return issueTypeVOMap;
    }

    @Override
    public Map<Long, Map<String, Long>> initIssueTypeData(Long organizationId, List<Long> orgIds) {
        Map<Long, Map<String, Long>> result = new HashMap<>();
        for (Long orgId : orgIds) {
            Map<String, Long> temp = new HashMap<>();
            for (InitIssueType initIssueType : InitIssueType.values()) {
                IssueTypeDTO issueType = createIssueType(new IssueTypeDTO(initIssueType.getIcon(), initIssueType.getName(), initIssueType.getDescription(), orgId, initIssueType.getColour(), initIssueType.getTypeCode(), true));
                temp.put(initIssueType.getTypeCode(), issueType.getId());
            }
            result.put(orgId, temp);
        }
        return result;
    }
}
