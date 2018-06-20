package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.domain.agile.repository.DataLogRepository;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.dto.IssueAttachmentDTO;
import io.choerodon.agile.app.service.IssueAttachmentService;
import io.choerodon.agile.domain.agile.entity.IssueAttachmentE;
import io.choerodon.agile.domain.agile.repository.IssueAttachmentRepository;
import io.choerodon.agile.infra.dataobject.IssueAttachmentDO;
import io.choerodon.agile.infra.feign.FileFeignClient;
import io.choerodon.agile.infra.mapper.IssueAttachmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class IssueAttachmentServiceImpl implements IssueAttachmentService {

    private static final String BACKETNAME = "agile-service";
    private static final String FIELD_ATTACHMENT = "Attachment";
    private static final String ACTION_UPLOAD = "upload";
    private static final String ACTION_DELETE = "delete";

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private IssueAttachmentRepository issueAttachmentRepository;

    @Autowired
    private IssueAttachmentMapper issueAttachmentMapper;

    @Autowired
    private DataLogRepository dataLogRepository;

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    private void dataLogAttachment(Long projectId, Long issueId, String url, Long attachmentId, String action) {
        DataLogE dataLogE = new DataLogE();
        dataLogE.setProjectId(projectId);
        dataLogE.setField(FIELD_ATTACHMENT);
        dataLogE.setIssueId(issueId);
        if (ACTION_UPLOAD.equals(action)) {
            dataLogE.setNewValue(attachmentId.toString());
            dataLogE.setNewString(url);
            dataLogRepository.create(dataLogE);
        } else if (ACTION_DELETE.equals(action)) {
            dataLogE.setOldValue(attachmentId.toString());
            dataLogE.setOldString(url);
            dataLogE.setNewValue(null);
            dataLogE.setNewString(null);
            dataLogRepository.create(dataLogE);
        }
    }

    private void dealIssue(Long projectId, Long issueId, String fileName, String url) {
        IssueAttachmentE issueAttachmentE = new IssueAttachmentE();
        issueAttachmentE.setProjectId(projectId);
        issueAttachmentE.setIssueId(issueId);
        issueAttachmentE.setFileName(fileName);
        issueAttachmentE.setUrl(url);
        issueAttachmentE.setCommentId(1L);
        IssueAttachmentE result = issueAttachmentRepository.create(issueAttachmentE);
        dataLogAttachment(projectId, issueId, url, result.getAttachmentId(), ACTION_UPLOAD);
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
    public List<IssueAttachmentDTO> create(Long projectId, Long issueId, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (files != null && !files.isEmpty()) {
            for (MultipartFile multipartFile : files) {
                String fileName = multipartFile.getOriginalFilename();
                ResponseEntity<String> response = fileFeignClient.uploadFile(BACKETNAME, fileName, multipartFile);
                if (response == null || response.getStatusCode() != HttpStatus.OK) {
                    throw new CommonException("error.attachment.upload");
                }
                dealIssue(projectId, issueId, fileName, dealUrl(response.getBody()));
            }
        }
        IssueAttachmentDO issueAttachmentDO = new IssueAttachmentDO();
        issueAttachmentDO.setIssueId(issueId);
        return ConvertHelper.convertList(issueAttachmentMapper.select(issueAttachmentDO), IssueAttachmentDTO.class);
    }

    @Override
    public Boolean delete(Long projectId, Long issueAttachmentId) {
        IssueAttachmentE issueAttachmentE = ConvertHelper.convert(issueAttachmentMapper.selectByPrimaryKey(issueAttachmentId), IssueAttachmentE.class);
        if (issueAttachmentE == null) {
            throw new CommonException("error.attachment.get");
        }
        String url = null;
        try {
            url = URLDecoder.decode(issueAttachmentE.getUrl(), "UTF-8");
        } catch (IOException i) {
            throw new CommonException(i.getMessage());
        }
        ResponseEntity<String> response = fileFeignClient.deleteFile(BACKETNAME, attachmentUrl + url);
        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            throw new CommonException("error.attachment.delete");
        }
        Boolean result = issueAttachmentRepository.deleteById(issueAttachmentE.getAttachmentId());
        dataLogAttachment(projectId, issueAttachmentE.getIssueId(), issueAttachmentE.getUrl(), issueAttachmentId, ACTION_DELETE);
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
            result.add(dealUrl(response.getBody()));
        }
        return result;
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        IssueAttachmentDO issueAttachmentDO = new IssueAttachmentDO();
        issueAttachmentDO.setIssueId(issueId);
        return issueAttachmentMapper.delete(issueAttachmentDO);
    }
}
