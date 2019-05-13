package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.BoardFeatureCreateDTO;
import io.choerodon.agile.api.dto.BoardFeatureDTO;
import io.choerodon.agile.api.dto.BoardFeatureUpdateDTO;
import io.choerodon.agile.app.service.BoardFeatureService;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.dataobject.BoardFeatureDO;
import io.choerodon.agile.infra.mapper.BoardFeatureMapper;
import io.choerodon.agile.infra.repository.BoardFeatureRepository;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
@Service
public class BoardFeatureServiceImpl implements BoardFeatureService {

    @Autowired
    private BoardFeatureMapper boardFeatureMapper;
    @Autowired
    private BoardFeatureRepository boardFeatureRepository;

    public static final String UPDATE_ERROR = "error.boardFeature.update";
    public static final String DELETE_ERROR = "error.boardFeature.deleteById";
    public static final String INSERT_ERROR = "error.boardFeature.create";
    public static final String EXIST_ERROR = "error.boardFeature.existData";
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public BoardFeatureDTO create(Long projectId, BoardFeatureCreateDTO createDTO) {
        BoardFeatureDO boardFeatureDO = modelMapper.map(createDTO, BoardFeatureDO.class);
        boardFeatureDO.setProgramId(projectId);
        checkExist(boardFeatureDO);
        handleRank(projectId, boardFeatureDO, createDTO.getBefore(), createDTO.getOutsetId());
        boardFeatureRepository.create(boardFeatureDO);
        return queryById(projectId, boardFeatureDO.getId());
    }

    @Override
    public BoardFeatureDTO update(Long projectId, Long boardFeatureId, BoardFeatureUpdateDTO updateDTO) {
        boardFeatureRepository.checkId(projectId, boardFeatureId);
        BoardFeatureDO update = modelMapper.map(updateDTO, BoardFeatureDO.class);
        update.setId(boardFeatureId);
        update.setProgramId(projectId);
        BoardFeatureDO select = boardFeatureMapper.selectOne(update);
        if (select != null && !select.getId().equals(boardFeatureId)) {
            throw new CommonException(EXIST_ERROR);
        }
        handleRank(projectId, update, updateDTO.getBefore(), updateDTO.getOutsetId());
        boardFeatureRepository.update(update);
        return queryById(projectId, boardFeatureId);
    }

    /**
     * 判断是否已经存在
     *
     * @param boardFeatureDO
     */
    private void checkExist(BoardFeatureDO boardFeatureDO) {
        if (!boardFeatureMapper.select(boardFeatureDO).isEmpty()) {
            throw new CommonException(EXIST_ERROR);
        }
    }

    /**
     * 处理rank值
     *
     * @param projectId
     * @param boardFeatureDO
     * @param before
     * @param outSetId
     */
    private void handleRank(Long projectId, BoardFeatureDO boardFeatureDO, Boolean before, Long outSetId) {
        if (outSetId.equals(0L)) {
            String rank = RankUtil.mid();
            boardFeatureDO.setRank(rank);
        } else if (before) {
            String outSetRank = queryById(projectId, outSetId).getRank();
            boardFeatureDO.setRank(RankUtil.genNext(outSetRank));
        } else {
            String outSetRank = queryById(projectId, outSetId).getRank();
            String rightRank = boardFeatureMapper.queryRightRank(boardFeatureDO, outSetRank);
            if (rightRank == null) {
                boardFeatureDO.setRank(RankUtil.genPre(outSetRank));
            } else {
                boardFeatureDO.setRank(RankUtil.between(outSetRank, rightRank));
            }
        }
    }

    @Override
    public BoardFeatureDTO queryById(Long projectId, Long boardFeatureId) {
        return modelMapper.map(boardFeatureRepository.queryById(projectId, boardFeatureId), BoardFeatureDTO.class);
    }

    @Override
    public void deleteById(Long projectId, Long boardFeatureId) {
        boardFeatureRepository.checkId(projectId, boardFeatureId);
        boardFeatureRepository.delete(boardFeatureId);
    }
}
