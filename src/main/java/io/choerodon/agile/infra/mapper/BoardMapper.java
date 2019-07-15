package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.BoardDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardMapper extends Mapper<BoardDTO> {

    /**
     * 根据项目id查询看板，包括当前用户默认看板信息
     *
     * @param userId    userId
     * @param projectId projectId
     * @return BoardDTO
     */
    List<BoardDTO> queryByProjectIdWithUser(@Param("userId") Long userId, @Param("projectId") Long projectId);
}
