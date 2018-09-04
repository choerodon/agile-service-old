package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.PriorityDistributeDTO
import io.choerodon.agile.api.dto.SprintInfoDTO
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IterativeWorktableControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    def projectId = 1L

    @Shared
    def sprintId = 1L

    @Autowired
    @Qualifier("mockUserRepository")
    private UserRepository userRepository

    def setup() {
        given:
        Map<Long, UserMessageDO> userMessageDOMap = new HashMap<>()
        UserMessageDO userMessageDO = new UserMessageDO("admin", "http://XXX.png", "admin@gmail.com")
        userMessageDOMap.put(1, userMessageDO)
        userRepository.queryUsersMap(*_) >> userMessageDOMap
        UserDO userDO = new UserDO()
        userDO.setRealName("admin")
        userRepository.queryUserNameByOption(*_) >> userDO
    }

    def 'queryPriorityDistribute'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/priority?sprintId={sprintId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId,
                sprintId)

        then:
        entity.statusCode.is2xxSuccessful()
        List<PriorityDistributeDTO> result = entity.body
        result.size() == 3
        result.get(0).priorityCode.equals("high")
        result.get(1).priorityCode.equals("medium")
        result.get(2).priorityCode.equals("low")
    }

    def 'queryPriorityDistribute fail'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/priority?sprintId={sprintId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                String.class,
                projectId,
                0L)

        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.sprint.get"
    }

    def 'queryStatusCategoryDistribute'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/status?sprintId={sprintId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId,
                sprintId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.size() > 0
    }

    def 'queryStatusCategoryDistribute fail'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/status?sprintId={sprintId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                String.class,
                projectId,
                0L)

        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.sprint.get"
    }

    def 'querySprintInfo'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/sprint?sprintId={sprintId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                SprintInfoDTO.class,
                projectId,
                sprintId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.sprintId == 1
        entity.body.sprintName.equals("sprint-test")
        entity.body.assigneeIssueDTOList.size() > 0
    }

    def 'querySprintInfo fail'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/sprint?sprintId={sprintId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                String.class,
                projectId,
                0L)

        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.sprint.get"
    }

}
