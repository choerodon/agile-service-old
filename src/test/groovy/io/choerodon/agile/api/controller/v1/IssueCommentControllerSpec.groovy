package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.IssueCommentCreateVO
import io.choerodon.agile.api.vo.IssueCommentVO
import io.choerodon.agile.app.service.UserService
import io.choerodon.agile.infra.dataobject.UserDTO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.mapper.IssueCommentMapper
import io.choerodon.agile.infra.mapper.IssueMapper
import org.mockito.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  9:44 2018/8/24
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueCommentControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueMapper issueMapper

    @Autowired
    IssueCommentMapper issueCommentMapper

    @Autowired
    @Qualifier("userService")
    private UserService userRepository

    @Shared
    def projectId = 1

    @Shared
    def resultTest=0

    def setup() {
        given: '设置feign调用mockito'
        // *_表示任何长度的参数（这里表示只要执行了queryUsersMap这个方法，就让它返回一个空的Map
        Map<Long, UserMessageDO> userMessageDOMap = new HashMap<>()
        UserMessageDO userMessageDO = new UserMessageDO("管理员", "http://XXX.png", "dinghuang123@gmail.com")
        userMessageDOMap.put(1, userMessageDO)
        Mockito.when(userRepository.queryUsersMap(Matchers.any(List.class), Matchers.anyBoolean())).thenReturn(userMessageDOMap)
        UserDTO userDO = new UserDTO()
        userDO.setRealName("管理员")
        Mockito.when(userRepository.queryUserNameByOption(Matchers.anyLong(), Matchers.anyBoolean())).thenReturn(userDO)

    }

    def 'createIssueComment'() {
        given: '准备IssueCommentCreateDTO'
        resultTest=issueMapper.selectAll().get(0).issueId
        IssueCommentCreateVO issueCommentCreateDTO = new IssueCommentCreateVO()
        issueCommentCreateDTO.issueId = resultTest
        issueCommentCreateDTO.commentText = '这是一条测试评论'


        when: '发送创建issue评论请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issue_comment', issueCommentCreateDTO, IssueCommentVO, projectId)
        print(entity.body)
        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        IssueCommentVO issueCommentDTO = entity.body

        expect: '设置期望值'
        issueCommentDTO.commentText == issueCommentCreateDTO.commentText
        issueCommentDTO.issueId == issueCommentCreateDTO.issueId
        issueCommentMapper.selectAll().size() == 1
    }

    def 'updateIssueComment'() {
        given: '更新IssueComment'
        JSONObject issueCommentUpdate = new JSONObject()
        and: '设置更新issueComment的信息'
        issueCommentUpdate.put("commentId", 1L)
        issueCommentUpdate.put("commentText", '这是一条更新评论')
        issueCommentUpdate.put("objectVersionNumber", 1L)

        when: '发送更新issue评论请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issue_comment/update', issueCommentUpdate, IssueCommentVO, projectId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        IssueCommentVO issueCommentDTO = entity.body

        expect: '设置期望值'
        issueCommentDTO.commentText == "这是一条更新评论"
        issueCommentMapper.selectAll().size() == 1
    }

    def 'queryIssueCommentList'() {
        when: '发送查询issue评论请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue_comment/{issueId}', List, projectId, issueId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()
        List<IssueCommentVO> result = entity.body


        and: '设置值'
        result.size() == expectSize

        where: '期望值'
        issueId    | expectSize
        resultTest | 1
        2L         | 0
        10L        | 0
    }

    def 'deleteIssueComment'() {
        when: '发送查询issue评论请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_comment/{issueId}",
                HttpMethod.DELETE,
                null,
                ResponseEntity.class,
                projectId,
                commentId
        )

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        issueCommentMapper.selectByPrimaryKey(commentId) == expectObject

        where: '期望值'
        commentId | expectObject
        1L        | null
    }
}
