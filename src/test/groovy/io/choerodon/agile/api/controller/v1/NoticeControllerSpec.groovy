package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.MessageVO
import io.choerodon.agile.infra.dataobject.MessageDTO
import io.choerodon.agile.infra.mapper.NoticeDetailMapper
import io.choerodon.agile.infra.mapper.NoticeMapper
import io.choerodon.core.convertor.ConvertHelper
import org.modelmapper.ModelMapper
import org.modelmapper.TypeToken
import org.modelmapper.convention.MatchingStrategies
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.annotation.PostConstruct

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/12.
 * Email: fuqianghuang01@gmail.com
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class NoticeControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    private NoticeMapper noticeMapper

    @Autowired
    private NoticeDetailMapper noticeDetailMapper

//    @Autowired
//    @Qualifier("mockUserRepository")
//    private UserService userService

    @Shared
    def projectId = 1L

    private ModelMapper modelMapper = new ModelMapper()

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT)
    }

//    def setup() {
//        userService.listRolesWithUserCountOnProjectLevel(*_) >> new ArrayList<RoleVO>()
//        userService.pagingQueryUsersByRoleIdOnProjectLevel(*_) >> new PageInfo<UserVO>()
//    }

    def 'updateNotice'() {
        given:
        String users = "1,2,3"
        List<MessageDTO> originList = noticeMapper.selectAll()
        for (MessageDTO messageDO : originList) {
            messageDO.setEnable(false)
            messageDO.setUser(users)
        }

        when:
        HttpEntity<List<MessageVO>> messages = new HttpEntity<>(modelMapper.map(originList, new TypeToken<List<MessageVO>>(){}.getType()))
        def entity = restTemplate.exchange("/v1/projects/{project_id}/notice",
                HttpMethod.PUT,
                messages,
                ResponseEntity.class,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()
    }

    def 'updateNotice unSuccess'() {
        given:
        List<MessageDTO> originList = noticeMapper.selectAll()
        originList.get(0).setEnable(false)
        originList.get(0).setObjectVersionNumber(originList.get(0).getObjectVersionNumber() + 1)

        when:
        HttpEntity<List<MessageVO>> messages = new HttpEntity<>(modelMapper.map(originList, new TypeToken<List<MessageVO>>(){}.getType()))
        def entity = restTemplate.exchange("/v1/projects/{project_id}/notice",
                HttpMethod.PUT,
                messages,
                String.class,
                projectId)

        then:
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.messageDetailDO.update"
    }

    def 'queryByProjectId'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/notice",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        for (MessageVO messageDTO : entity.body) {
            messageDTO.enable == false
            messageDTO.user == "1,2,3"
        }
    }



}
