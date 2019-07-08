package io.choerodon.agile.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.IssueAttachmentDTO;
import io.choerodon.agile.app.service.IssueAttachmentService;
import io.choerodon.agile.domain.agile.entity.IssueAttachmentE;
import io.choerodon.agile.infra.repository.IssueAttachmentRepository;
import io.choerodon.agile.infra.dataobject.IssueAttachmentDO;
import io.choerodon.agile.infra.feign.FileFeignClient;
import io.choerodon.agile.infra.mapper.IssueAttachmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
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
@Transactional(rollbackFor = Exception.class)
public class IssueAttachmentServiceImpl implements IssueAttachmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueAttachmentServiceImpl.class);

    private static final String BACKETNAME = "agile-service";

    private final FileFeignClient fileFeignClient;

    @Autowired
    public IssueAttachmentServiceImpl(FileFeignClient fileFeignClient) {
        this.fileFeignClient = fileFeignClient;
    }

    @Autowired
    private IssueAttachmentRepository issueAttachmentRepository;

    @Autowired
    private IssueAttachmentMapper issueAttachmentMapper;

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Override
    public void dealIssue(Long projectId, Long issueId, String fileName, String url) {
        IssueAttachmentE issueAttachmentE = new IssueAttachmentE();
        issueAttachmentE.setProjectId(projectId);
        issueAttachmentE.setIssueId(issueId);
        issueAttachmentE.setFileName(fileName);
        issueAttachmentE.setUrl(url);
        issueAttachmentE.setCommentId(1L);
        issueAttachmentRepository.create(issueAttachmentE);
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
        List<IssueAttachmentDO> issueAttachmentDOList = issueAttachmentMapper.select(issueAttachmentDO);
        List<IssueAttachmentDTO> result = new ArrayList<>();
        if (issueAttachmentDOList != null && !issueAttachmentDOList.isEmpty()) {
            issueAttachmentDOList.forEach(attachment -> {
                IssueAttachmentDTO issueAttachmentDTO = new IssueAttachmentDTO();
                BeanUtils.copyProperties(attachment, issueAttachmentDTO);
                issueAttachmentDTO.setUrl(attachmentUrl + attachment.getUrl());
                result.add(issueAttachmentDTO);
            });
        }
        return result;
    }

    @Override
    public Boolean delete(Long projectId, Long issueAttachmentId) {
        IssueAttachmentE issueAttachmentE = ConvertHelper.convert(issueAttachmentMapper.selectByPrimaryKey(issueAttachmentId), IssueAttachmentE.class);
        if (issueAttachmentE == null) {
            throw new CommonException("error.attachment.get");
        }
        Boolean result = issueAttachmentRepository.deleteById(issueAttachmentE.getAttachmentId());
        String url = null;
        try {
            url = URLDecoder.decode(issueAttachmentE.getUrl(), "UTF-8");
            fileFeignClient.deleteFile(BACKETNAME, attachmentUrl + url);
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
            result.add(attachmentUrl + dealUrl(response.getBody()));
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
