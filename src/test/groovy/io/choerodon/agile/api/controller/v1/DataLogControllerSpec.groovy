package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.DataLogCreateVO
import io.choerodon.agile.api.vo.DataLogVO
import io.choerodon.agile.infra.dataobject.UserMessageDTO
import io.choerodon.agile.infra.mapper.DataLogMapper
import io.choerodon.agile.app.service.UserService
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
    UserService userRepository

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
        DataLogCreateVO createDTO = new DataLogCreateVO()
        createDTO.field = field
        createDTO.newValue = 1L
        createDTO.newString = "待处理"
        createDTO.issueId = issueId

        when:
        HttpEntity<DataLogCreateVO> dataLogDTOHttpEntity = new HttpEntity<>(createDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/feedback_data_log",
                HttpMethod.POST,
                dataLogDTOHttpEntity,
                DataLogVO.class,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body != null
        entity.body.issueId == 1L
    }

    def 'listByIssueId'() {
        given:
        Map<Long, UserMessageDTO> userMessageDOMap = new HashMap<>()
        UserMessageDTO userMessageDO = new UserMessageDTO("admin", "http://admin.png", "admin@gmail.com")
        userMessageDOMap.put(1, userMessageDO)
        userRepository.queryUsersMap(*_) >> userMessageDOMap

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/feedback_data_log?issueId={issueId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId,
                issueId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.isEmpty() == false
    }

}
