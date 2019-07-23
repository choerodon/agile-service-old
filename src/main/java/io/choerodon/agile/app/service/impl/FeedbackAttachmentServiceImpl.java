package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.FeedbackAttachmentService;
import io.choerodon.agile.app.service.IFeedbackAttachmentService;
import io.choerodon.agile.infra.dataobject.ApplicationDTO;
import io.choerodon.agile.infra.dataobject.FeedbackAttachmentDTO;
import io.choerodon.agile.infra.feign.FileFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.FeedbackAttachmentMapper;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class FeedbackAttachmentServiceImpl implements FeedbackAttachmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackAttachmentServiceImpl.class);

    private static final String BACKETNAME = "feedback-service";

    @Autowired
    private FeedbackAttachmentMapper feedbackAttachmentMapper;

    @Autowired
    private IFeedbackAttachmentService iFeedbackAttachmentService;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;


    @Value("${services.attachment.url}")
    private String attachmentUrl;

    public void dealIssue(Long projectId, Long feedbackId, Long commnetId, String fileName, String url) {
        FeedbackAttachmentDTO feedbackAttachmentDTO = new FeedbackAttachmentDTO();
        feedbackAttachmentDTO.setProjectId(projectId);
        feedbackAttachmentDTO.setFeedbackId(feedbackId);
        if (commnetId != null) {
            feedbackAttachmentDTO.setCommentId(commnetId);
        }
        feedbackAttachmentDTO.setFileName(fileName);
        feedbackAttachmentDTO.setUrl(url);
        iFeedbackAttachmentService.createBase(feedbackAttachmentDTO);
    }

    private String dealUrl(String url) {
        String dealUrl = null;
        try {
            URL netUrl = new URL(url);
            dealUrl = netUrl.getFile().substring(BACKETNAME.length() + 2);
        } catch (MalformedURLException e) {
            throw new CommonException(e.getMessage());
        }
        return dealUrl;
    }

    @Override
    public List<FeedbackAttachmentDTO> create(Long projectId, Long feedbackId, Long commentId, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (files != null && !files.isEmpty()) {
            for (MultipartFile multipartFile : files) {
                String fileName = multipartFile.getOriginalFilename();
                ResponseEntity<String> response = fileFeignClient.uploadFile(BACKETNAME, fileName, multipartFile);
                if (response == null || response.getStatusCode() != HttpStatus.OK) {
                    throw new CommonException("error.attachment.upload");
                }
                dealIssue(projectId, feedbackId, commentId, fileName, dealUrl(response.getBody()));
            }
        }
        FeedbackAttachmentDTO feedbackAttachmentDTO = new FeedbackAttachmentDTO();
        feedbackAttachmentDTO.setFeedbackId(feedbackId);
        if (commentId != null) {
            feedbackAttachmentDTO.setCommentId(commentId);
        }
        List<FeedbackAttachmentDTO> result = feedbackAttachmentMapper.select(feedbackAttachmentDTO);
        if (result != null && !result.isEmpty()) {
            result.forEach(r -> r.setUrl(attachmentUrl + "/" + BACKETNAME + "/" + r.getUrl()));
        }
        return result;
    }

    private Boolean deleteById(Long projectId, Long id) {
        FeedbackAttachmentDTO feedbackAttachmentDTO = new FeedbackAttachmentDTO();
        feedbackAttachmentDTO.setProjectId(projectId);
        feedbackAttachmentDTO.setId(id);
        return iFeedbackAttachmentService.deleteBase(feedbackAttachmentDTO);
    }

    @Override
    public Boolean delete(Long projectId, Long id) {
        FeedbackAttachmentDTO feedbackAttachmentDTO = feedbackAttachmentMapper.selectByPrimaryKey(id);
        if (feedbackAttachmentDTO == null) {
            throw new CommonException("error.attachment.get");
        }
        Boolean result = deleteById(projectId, id);
        String url = null;
        try {
            url = URLDecoder.decode(feedbackAttachmentDTO.getUrl(), "UTF-8");
            fileFeignClient.deleteFile(BACKETNAME, attachmentUrl + "/" + BACKETNAME + "/" + url);
        } catch (Exception e) {
            LOGGER.error("error.attachment.delete", e);
        }
        return result;
    }

    @Override
    public List<String> uploadForAddress(Long projectId, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (!(files != null && !files.isEmpty())) {
            throw new CommonException("error.attachment.exits");
        }
        List<String> result = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String fileName = multipartFile.getOriginalFilename();
            ResponseEntity<String> response = fileFeignClient.uploadFile(BACKETNAME, fileName, multipartFile);
            if (response == null || response.getStatusCode() != HttpStatus.OK) {
                throw new CommonException("error.attachment.upload");
            }
            result.add(attachmentUrl + "/" + BACKETNAME + "/" + dealUrl(response.getBody()));
        }
        return result;
    }

    @Override
    public List<FeedbackAttachmentDTO> uploadAttachmentPublic(Long feedbackId, String token, HttpServletRequest request) {
        ApplicationDTO applicationDTO = userFeignClient.getApplicationByToken(new ApplicationDTO(token)).getBody();
        if (applicationDTO == null) {
            throw new CommonException("error.application.get");
        }
        Long projectId = applicationDTO.getProjectId();
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (files != null && !files.isEmpty()) {
            for (MultipartFile multipartFile : files) {
                String fileName = multipartFile.getOriginalFilename();
                ResponseEntity<String> response = fileFeignClient.uploadFile(BACKETNAME, fileName, multipartFile);
                if (response == null || response.getStatusCode() != HttpStatus.OK) {
                    throw new CommonException("error.attachment.upload");
                }
                dealIssue(projectId, feedbackId, null, fileName, dealUrl(response.getBody()));
            }
        }
        FeedbackAttachmentDTO feedbackAttachmentDTO = new FeedbackAttachmentDTO();
        feedbackAttachmentDTO.setFeedbackId(feedbackId);
        List<FeedbackAttachmentDTO> result = feedbackAttachmentMapper.select(feedbackAttachmentDTO);
        if (result != null && !result.isEmpty()) {
            result.forEach(r -> r.setUrl(attachmentUrl + "/" + BACKETNAME + "/" + r.getUrl()));
        }
        return result;
    }

}
