package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.AssigneeDistributeVO
import io.choerodon.agile.api.vo.IssueTypeDistributeVO
import io.choerodon.agile.api.vo.PriorityDistributeVO
import io.choerodon.agile.api.vo.SprintInfoVO
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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IterativeWorktableControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueMapper issueMapper

    @Shared
    def organizationId = 1L

    @Shared
    def projectId = 1L

    @Shared
    def sprintId = 1L

//    @Autowired
//    @Qualifier("mockUserRepository")
//    private UserService userService

//    def setup() {
//        given:
//        Map<Long, UserMessageDO> userMessageDOMap = new HashMap<>()
//        UserMessageDO userMessageDO = new UserMessageDO("admin", "http://XXX.png", "admin@gmail.com")
//        userMessageDOMap.put(1, userMessageDO)
//        userService.queryUsersMap(*_) >> userMessageDOMap
//        UserDO userDO = new UserDO()
//        userDO.setRealName("admin")
//        userService.queryUserNameByOption(*_) >> userDO
//    }

    def 'queryPriorityDistribute'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/priority?sprintId={sprintId}&organizationId={organizationId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId,
                sprintId,
                organizationId)

        then:
        entity.statusCode.is2xxSuccessful()
        List<PriorityDistributeVO> result = entity.body
        result.size() == 1
        result.get(0).priorityVO.name.equals("高")
    }

    def 'queryPriorityDistribute fail'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/priority?sprintId={sprintId}&organizationId={organizationId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                String.class,
                projectId,
                0L,
                organizationId)

        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.sprint.get"
    }

    def 'queryStatusCategoryDistribute'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/status?sprintId={sprintId}&organizationId={organizationId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId,
                sprintId,
                organizationId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.size() > 0
    }

    def 'queryStatusCategoryDistribute fail'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/status?sprintId={sprintId}&organizationId={organizationId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                String.class,
                projectId,
                0L,
                organizationId)

        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.sprint.get"
    }

    def 'querySprintInfo'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/sprint/{organizationId}?sprintId={sprintId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                SprintInfoVO.class,
                projectId,
                1,
                sprintId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.sprintId == 1
        entity.body.sprintName.equals("sprint-test")
        entity.body.assigneeIssueVOList.size() > 0
    }

    def 'querySprintInfo fail'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/sprint/{organizationId}?sprintId={sprintId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                String.class,
                projectId,
                1,
                0L)

        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.sprint.get"
    }

    def 'queryAssigneeDistribute'() {
        when: '发请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/assignee_id?sprintId={sprintId}",
                HttpMethod.GET,
                null,
                List.class,
                projectId,
                sprintId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<AssigneeDistributeVO> result = entity.body

        expect: '期望值'
        result.size() == 1
        result.get(0).assigneeName == '未分配'
        result.get(0).percent == 100.0000
    }

    def 'queryIssueTypeDistribute'() {
        when: '发请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/iterative_worktable/issue_type?sprintId={sprintId}&organizationId={organizationId}",
                HttpMethod.GET,
                null,
                List.class,
                projectId,
                sprintId,
                organizationId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueTypeDistributeVO> result = entity.body

        expect: '期望值'
        result.size() == 1
        result.get(0).typeCode == 'story'
    }

}
