package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueAttachmentDTO
import io.choerodon.agile.infra.feign.FileFeignClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.servlet.http.HttpServletRequest

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: scp
 * Date:  19:38 2018/8/27
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueAttachmentControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate
//
//    @Autowired
//    HttpServletRequest request

    @Autowired
    @Qualifier("mockFileFeignClient")
    private FileFeignClient fileFeignClient

    @Shared
    def projectId = 1

    @Shared
    def issueId = 1L

    def 'uploadAttachment'() {
        given: '上传附件'
        ResponseEntity<String> responseEntity = new ResponseEntity<>()
        responseEntity.statusCode = HttpStatus.OK
        fileFeignClient.uploadFile(*_) >> responseEntity

        and: '准备文件'
        FileInputStream fis = new FileInputStream("D:\\test.txt")
        byte[] data = toByteArray(fis)
        in.close()

//        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "txt", fis)
//        HttpServletRequest request =ServletActionContext.getRequest()
//        request.content(data)

        when: '发送创建issue评论请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issue_attachment', request, List, projectId, issueId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueAttachmentDTO> result = entity.body

        expect: '设置期望值'
        result.size() == 1
        result.get(0).projectId == 1L
        result.get(0).issueId == 1L

    }
}
