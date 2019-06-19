package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.MessageDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface NoticeMapper extends Mapper<MessageDO> {

    List<MessageDO> selectChangeMessageByProjectId(Long projectId);

    MessageDO selectChangeMessageByDetail(@Param("projectId") Long projectId,
                                          @Param("event") String event,
                                          @Param("noticeType") String noticeType);

    List<MessageDO> selectByEvent(String event);

    List<MessageDO> selectByProjectIdAndEvent(@Param("projectId") Long projectId,
                                              @Param("event") String event);


}
