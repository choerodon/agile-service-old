package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.common.aspect.DataLogRedisUtil;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.SprintDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.converter.SprintConverter;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.repository.SprintRepository;
import io.choerodon.agile.infra.mapper.SprintMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Component
public class SprintRepositoryImpl implements SprintRepository {

    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private SprintConverter sprintConverter;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DataLogRedisUtil dataLogRedisUtil;

    private static final String INSERT_ERROR = "error.sprint.insert";
    private static final String DELETE_ERROR = "error.sprint.delete";
    private static final String UPDATE_ERROR = "error.sprint.update";

    @Override
    public SprintE createSprint(SprintE sprintE) {
        SprintDTO sprintDTO = sprintConverter.entityToDo(sprintE);
        if (sprintMapper.insertSelective(sprintDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        //清除冲刺报表相关缓存
        dataLogRedisUtil.deleteByCreateSprint(sprintE);
        return sprintConverter.doToEntity(sprintMapper.selectByPrimaryKey(sprintDTO.getSprintId()));
    }

    @Override
    public SprintE updateSprint(SprintE sprintE) {
        SprintDTO sprintDTO = sprintConverter.entityToDo(sprintE);
        if (sprintMapper.updateByPrimaryKeySelective(sprintDTO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        //清除冲刺报表相关缓存
        dataLogRedisUtil.deleteByUpdateSprint(sprintE);
        return sprintConverter.doToEntity(sprintMapper.selectByPrimaryKey(sprintDTO.getSprintId()));
    }

    @Override
    public Boolean deleteSprint(SprintE sprintE) {
        SprintDTO sprintDTO = sprintConverter.entityToDo(sprintE);
        if (sprintMapper.delete(sprintDTO) != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        //清除冲刺报表相关缓存
        dataLogRedisUtil.deleteByUpdateSprint(sprintE);
        return true;
    }

    @Override
    public void updateSprintNameByBatch(Long programId, List<Long> sprintIds) {
        sprintMapper.updateSprintNameByBatch(programId, sprintIds);
    }
}
