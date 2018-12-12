package io.choerodon.agile.domain.agile.repository

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.ProjectDTO
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.feign.UserFeignClient
import io.choerodon.agile.infra.repository.impl.UserRepositoryImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Stepwise

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/9/11
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class UserRepositorySpec extends Specification {

    @Autowired
    UserRepository userRepository

    def 'queryUserNameByOption'() {
        given: 'mockFeign'
        def userFeignClient = Mock(UserFeignClient)
        userRepository = new UserRepositoryImpl(userFeignClient)

        and: '给定返回参数'
        UserDO mock = new UserDO()
        mock.loginName = '测试'
        mock.realName = 'XX'
        ResponseEntity<UserDO> responseEntity = new ResponseEntity<>(mock, HttpStatus.OK)

        when: '根据参数查询用户信息'
        UserDO userDO = userRepository.queryUserNameByOption(userId, withId)

        then: '判断mock交互并且设置返回值'
        if (userId != 0) {
            1 * userFeignClient.query(_, _) >> responseEntity
        }

        expect: '设置期望值'
        userDO.realName == realName

        where: '条件'
        userId | withId || realName
        1      | false  || 'XX'
        1      | true   || '测试XX'
        0      | false  || null

    }

    def 'queryUsersMap'() {
        given: 'mockFeign'
        def userFeignClient = Mock(UserFeignClient)
        userRepository = new UserRepositoryImpl(userFeignClient)

        and: '给定返回参数'
        List<UserDO> mock = new ArrayList<>()
        UserDO userDO = new UserDO()
        userDO.loginName = '测试'
        userDO.realName = 'XX'
        userDO.id = 1
        mock.add(userDO)
        ResponseEntity<List<UserDO>> responseEntity = new ResponseEntity<>(mock, HttpStatus.OK)

        and: '方法参数'
        List<Long> assigneeIdList = new ArrayList<>()
        assigneeIdList.add(1L)

        when: '根据用户id列表查询用户信息'
        Map<Long, UserMessageDO> userMessageDOMap = userRepository.queryUsersMap(assigneeIdList, withLoginName)

        then: '判断mock交互并且设置返回值'
        1 * userFeignClient.listUsersByIds(*_) >> responseEntity

        expect: '设置期望值'
        userMessageDOMap.size() == expectSize

        where: '条件'
        withLoginName || expectSize
        true          || 1
    }

    def 'queryProject'() {
        given: 'mockFeign'
        def userFeignClient = Mock(UserFeignClient)
        userRepository = new UserRepositoryImpl(userFeignClient)

        and: '给定返回参数'
        ProjectDTO mock = new ProjectDTO()
        mock.id = 1
        ResponseEntity<ProjectDTO> responseEntity = new ResponseEntity<>(mock, HttpStatus.OK)

        and: '方法参数'
        List<Long> assigneeIdList = new ArrayList<>()
        assigneeIdList.add(1L)

        when: '根据用户id列表查询用户信息'
        ProjectDTO projectDTO = userRepository.queryProject(1)

        then: '判断mock交互并且设置返回值'
        1 * userFeignClient.queryProject(_) >> responseEntity

        expect: '设置期望值'
        projectDTO.id == 1

    }

}

