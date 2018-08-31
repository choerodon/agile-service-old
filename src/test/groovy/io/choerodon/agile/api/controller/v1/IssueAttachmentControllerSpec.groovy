package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueAttachmentDTO
import io.choerodon.agile.infra.dataobject.IssueAttachmentDO
import io.choerodon.agile.infra.feign.FileFeignClient
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.multipart.MultipartFile

import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.any
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

    @Autowired
    HttpServletRequest request

    @Autowired
    private WebApplicationContext webApplicationContext

    @Autowired
    @Qualifier("fileFeignClient")
    private FileFeignClient fileFeignClient

    @Shared
    def projectId = 1

    @Shared
    def issueId = 1L

    def 'uploadAttachment'() {
        given: '上传附件'
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("file", "FileUploadTest.txt", "text/plain", "This is a Test".getBytes())
        Mockito.when(fileFeignClient.uploadFile(anyString(), anyString(),any(MultipartFile.class))).thenReturn(new ResponseEntity<>(
                "https://minio.choerodon.com.cn/agile-service/file_56a005f56a584047b538d5bf84b17d70_blob.png", HttpStatus.OK))
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/v1/projects/1/issue_attachment?issueId=1")
                .file(mockMultipartFile)).andReturn()
        List<IssueAttachmentDTO> issueAttachmentDTOList =  JSONObject.parseArray(result.getResponse().content.toString(),IssueAttachmentDTO)
        expect: '设置期望值'
        issueAttachmentDTOList.get(0).projectId == 1
    }
}
