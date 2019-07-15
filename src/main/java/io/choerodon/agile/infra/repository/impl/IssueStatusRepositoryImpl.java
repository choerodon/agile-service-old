//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.api.vo.event.AddStatusWithProject;
//import io.choerodon.agile.infra.common.annotation.DataLog;
//import io.choerodon.agile.infra.common.aspect.DataLogRedisUtil;
//import io.choerodon.agile.infra.common.utils.RedisUtil;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.agile.domain.agile.entity.IssueStatusE;
//import io.choerodon.agile.infra.repository.IssueStatusRepository;
//import io.choerodon.agile.infra.dataobject.IssueStatusDTO;
//import io.choerodon.agile.infra.mapper.IssueStatusMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/5/14.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class IssueStatusRepositoryImpl implements IssueStatusRepository {
//
//    @Autowired
//    private IssueStatusMapper issueStatusMapper;
//    @Autowired
//    private RedisUtil redisUtil;
//    @Autowired
//    private DataLogRedisUtil dataLogRedisUtil;
//
//    private static final String AGILE = "Agile:";
//    private static final String PIECHART = AGILE + "PieChart";
//    private static final String STATUS = "status";
//
//    public static String getUUID() {
//        UUID uuid = UUID.randomUUID();
//        String str = uuid.toString();
//        String uuidStr = str.replace("-", "");
//        return uuidStr.substring(10);
//    }
//
//    @Override
//    public IssueStatusE create(IssueStatusE issueStatusE) {
//        IssueStatusDTO issueStatusDTO = ConvertHelper.convert(issueStatusE, IssueStatusDTO.class);
//        if (issueStatusMapper.insert(issueStatusDTO) != 1) {
//            throw new CommonException("error.IssueStatus.insert");
//        }
//        redisUtil.deleteRedisCache(new String[]{PIECHART + issueStatusE.getProjectId() + ':' + STATUS + "*"});
//        return ConvertHelper.convert(issueStatusMapper.selectByStatusId(issueStatusDTO.getProjectId(), issueStatusDTO.getStatusId()), IssueStatusE.class);
//    }
//
//    @Override
//    @DataLog(type = "batchUpdateIssueStatus", single = false)
//    public IssueStatusE update(IssueStatusE issueStatusE) {
//        IssueStatusDTO issueStatusDTO = ConvertHelper.convert(issueStatusE, IssueStatusDTO.class);
//        if (issueStatusMapper.updateByPrimaryKeySelective(issueStatusDTO) != 1) {
//            throw new CommonException("error.status.update");
//        }
//        dataLogRedisUtil.deleteByUpdateIssueStatus(issueStatusDTO);
//        return ConvertHelper.convert(issueStatusMapper.selectByStatusId(issueStatusDTO.getProjectId(), issueStatusDTO.getStatusId()), IssueStatusE.class);
//    }
//
//    @Override
//    public void delete(IssueStatusE issueStatusE) {
//        IssueStatusDTO issueStatusDTO = ConvertHelper.convert(issueStatusE, IssueStatusDTO.class);
//        if (issueStatusMapper.delete(issueStatusDTO) != 1) {
//            throw new CommonException("error.status.delete");
//        }
//        dataLogRedisUtil.deleteByUpdateIssueStatus(issueStatusDTO);
//    }
//
//    @Override
//    public void batchCreateStatusByProjectIds(List<AddStatusWithProject> addStatusWithProjects, Long userId) {
//        issueStatusMapper.batchCreateStatusByProjectIds(addStatusWithProjects, userId);
//    }
//
//}
