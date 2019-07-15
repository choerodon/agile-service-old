package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.IssueAttachmentVO
import io.choerodon.agile.app.service.IssueAttachmentService
import io.choerodon.agile.infra.dataobject.IssueAttachmentDTO
import io.choerodon.agile.infra.feign.FileFeignClient
import io.choerodon.agile.infra.mapper.IssueAttachmentMapper
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.multipart.MultipartFile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.servlet.http.HttpServletRequest

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: ChangpingShi0213@gmail.com
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
    IssueAttachmentMapper issueAttachmentMapper

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

        FileSystemResource resource = new FileSystemResource(new File("D:\\test1.txt"))
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>()
        param.add("file", resource)
        param.add("fileName", "test1.txt")
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(param, null)

        when: '发送请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_attachment?issueId={issueId}", HttpMethod.POST, httpEntity, List, projectId, issueId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueAttachmentVO> list = entity.body

        expect: '期望值'
        list.size() == 1
    }

    def 'deleteAttachment'() {
        given: '删除附件'
        List<IssueAttachmentDTO> list = issueAttachmentMapper.selectAll()
        Mockito.when(fileFeignClient.deleteFile(anyString(), anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK))

        when: '发送请求'
        try {
            def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_attachment/{issueAttachmentId}",
                    HttpMethod.DELETE,
                    null,
                    ResponseEntity.class,
                    projectId,
                    issueAttachmentId)
        } catch (Exception e) {
            expectObject = e
        }

        then: '返回值'
        if (expectObject != null) {
            issueAttachmentMapper.selectByPrimaryKey(issueAttachmentId) == expectObject
        }

        where: '期望值'
        issueAttachmentId | expectObject
        1L                | null
        2L                | Exception
    }

    def 'uploadForAddress'() {
        given: '上传附件'
        Mockito.when(fileFeignClient.uploadFile(anyString(), anyString(), any(MultipartFile.class))).thenReturn(new ResponseEntity<>(
                "https://minio.choerodon.com.cn/agile-service/file_56a005f56a584047b538d5bf84b17d70_blob.png", HttpStatus.OK))

        FileSystemResource resource = new FileSystemResource(new File("D:\\test2.txt"))
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>()
        param.add("file", resource)
        param.add("fileName", "test2.txt")
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(param, null)

        when: '发送请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_attachment/upload_for_address",
                HttpMethod.POST,
                httpEntity,
                List,
                projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<String> result = entity.body

        expect: '设置值'
        result.get(0) == "file_56a005f56a584047b538d5bf84b17d70_blob.png"
    }
}

