package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueAttachmentDTO
import io.choerodon.agile.app.service.IssueAttachmentService
import io.choerodon.agile.infra.feign.FileFeignClient
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.multipart.MultipartFile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.any

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

    @Autowired
    HttpServletRequest request

    @Autowired
    private WebApplicationContext webApplicationContext

    @Autowired
    @Qualifier("fileFeignClient")
    private FileFeignClient fileFeignClient

    @Autowired
    @Qualifier("issueAttachmentService")
    IssueAttachmentService issueAttachmentService

    @Shared
    def projectId = 1

    @Shared
    def issueId = 1L

    def 'uploadAttachment'() {
        given: '上传附件'
        Mockito.when(fileFeignClient.uploadFile(anyString(), anyString(), any(MultipartFile.class))).thenReturn(new ResponseEntity<>(
                "https://minio.choerodon.com.cn/agile-service/file_56a005f56a584047b538d5bf84b17d70_blob.png", HttpStatus.OK))

        FileSystemResource resource = new FileSystemResource(new File( "D:\\test1.txt"))
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>()
        param.add("file", resource)
        param.add("fileName", "test1.txt")
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(param, null)

        when: '发送请求'
        def entity = restTemplate.exchange("/v1/projects/1/issue_attachment?issueId=1", HttpMethod.POST, httpEntity, List)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueAttachmentDTO> list = entity.body

        expect: '期望值'
        list.size() == 1
    }
}

