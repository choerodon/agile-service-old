package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.api.dto.BoardFeatureInfoDTO;
import io.choerodon.agile.infra.dataobject.BoardFeatureDO;
import io.choerodon.agile.infra.mapper.BoardFeatureMapper;
import io.choerodon.agile.infra.repository.BoardFeatureRepository;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
@Component
public class BoardFeatureRepositoryImpl implements BoardFeatureRepository {
    @Autowired
    private BoardFeatureMapper boardFeatureMapper;

    private static final String ERROR_BOARDFEATURE_ILLEGAL = "error.boardFeature.illegal";
    private static final String ERROR_BOARDFEATURE_CREATE = "error.boardFeature.create";
    private static final String ERROR_BOARDFEATURE_DELETE = "error.boardFeature.delete";
    private static final String ERROR_BOARDFEATURE_NOTFOUND = "error.boardFeature.notFound";
    private static final String ERROR_BOARDFEATURE_UPDATE = "error.boardFeature.update";

    @Override
    public BoardFeatureDO create(BoardFeatureDO boardFeature) {
        if (boardFeatureMapper.insert(boardFeature) != 1) {
            throw new CommonException(ERROR_BOARDFEATURE_CREATE);
        }
        return boardFeatureMapper.selectByPrimaryKey(boardFeature.getId());
    }

    @Override
    public void delete(Long boardFeatureId) {
        if (boardFeatureMapper.deleteByPrimaryKey(boardFeatureId) != 1) {
            throw new CommonException(ERROR_BOARDFEATURE_DELETE);
        }
    }

    @Override
    public void update(BoardFeatureDO boardFeature) {
        if (boardFeatureMapper.updateByPrimaryKeySelective(boardFeature) != 1) {
            throw new CommonException(ERROR_BOARDFEATURE_UPDATE);
        }
    }

    @Override
    public BoardFeatureDO queryById(Long projectId, Long boardFeatureId) {
        BoardFeatureDO boardFeature = boardFeatureMapper.selectByPrimaryKey(boardFeatureId);
        if (boardFeature == null) {
            throw new CommonException(ERROR_BOARDFEATURE_NOTFOUND);
        }
        if (!boardFeature.getProgramId().equals(projectId)) {
            throw new CommonException(ERROR_BOARDFEATURE_ILLEGAL);
        }
        return boardFeature;
    }

    @Override
    public BoardFeatureInfoDTO queryInfoById(Long projectId, Long boardFeatureId) {
        BoardFeatureInfoDTO boardFeature = boardFeatureMapper.queryInfoById(projectId, boardFeatureId);
        if (boardFeature == null) {
            throw new CommonException(ERROR_BOARDFEATURE_NOTFOUND);
        }
        if (!boardFeature.getProgramId().equals(projectId)) {
            throw new CommonException(ERROR_BOARDFEATURE_ILLEGAL);
        }
        return boardFeature;
    }

    @Override
    public void checkId(Long projectId, Long boardFeatureId) {
        queryById(projectId, boardFeatureId);
    }
}
