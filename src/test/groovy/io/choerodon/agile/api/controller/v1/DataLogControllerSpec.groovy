package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.DataLogDTO
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.mapper.DataLogMapper
import io.choerodon.agile.infra.mapper.IssueMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/20.
 * Email: fuqianghuang01@gmail.com
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class DataLogControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    UserRepository userRepository

    @Autowired
    private DataLogMapper dataLogMapper

    @Shared
    def projectId = 1L

    @Shared
    def issueId = 1L

    @Shared
    def field = "status"


    def 'createDataLog'() {
        given:
        DataLogDTO dataLogDTO = new DataLogDTO()
        dataLogDTO.projectId = projectId
        dataLogDTO.field = field
        dataLogDTO.newValue = 1L
        dataLogDTO.newString = "待处理"
        dataLogDTO.issueId = issueId

        when:
        HttpEntity<DataLogDTO> dataLogDTOHttpEntity = new HttpEntity<>(dataLogDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/data_log",
                HttpMethod.POST,
                dataLogDTOHttpEntity,
                DataLogDTO.class,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body != null
        entity.body.issueId == 1L
    }

    def 'listByIssueId'() {
        given:
        Map<Long, UserMessageDO> userMessageDOMap = new HashMap<>()
        UserMessageDO userMessageDO = new UserMessageDO("admin", "http://admin.png", "admin@gmail.com")
        userMessageDOMap.put(1, userMessageDO)
        userRepository.queryUsersMap(*_) >> userMessageDOMap

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/data_log?issueId={issueId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId,
                issueId)

        then:
        entity.statusCode.is2xxSuccessful()
        List<DataLogDTO> dataLogDTOList = entity.body
        dataLogDTOList.size() == 1
    }

}
