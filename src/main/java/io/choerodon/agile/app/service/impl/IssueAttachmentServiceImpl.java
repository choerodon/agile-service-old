package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.IIssueAttachmentService;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.IssueAttachmentDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.IssueAttachmentVO;
import io.choerodon.agile.app.service.IssueAttachmentService;
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
    private IssueAttachmentMapper issueAttachmentMapper;

    @Autowired
    private IIssueAttachmentService iIssueAttachmentService;

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Override
    public void dealIssue(Long projectId, Long issueId, String fileName, String url) {
        IssueAttachmentDTO issueAttachmentDTO = new IssueAttachmentDTO();
        issueAttachmentDTO.setProjectId(projectId);
        issueAttachmentDTO.setIssueId(issueId);
        issueAttachmentDTO.setFileName(fileName);
        issueAttachmentDTO.setUrl(url);
        issueAttachmentDTO.setCommentId(1L);
        iIssueAttachmentService.createBase(issueAttachmentDTO);
    }

//    @DataLog(type = "createAttachment")
//    public IssueAttachmentDTO insertIssueAttachment(IssueAttachmentDTO issueAttachmentDTO) {
//        if (issueAttachmentMapper.insert(issueAttachmentDTO) != 1) {
//            throw new CommonException(INSERT_ERROR);
//        }
//        return issueAttachmentMapper.selectByPrimaryKey(issueAttachmentDTO.getAttachmentId());
//    }

//    @DataLog(type = "deleteAttachment")
//    public Boolean deleteById(Long attachmentId) {
//        IssueAttachmentDTO issueAttachmentDTO = issueAttachmentMapper.selectByPrimaryKey(attachmentId);
//        if (issueAttachmentDTO == null) {
//            throw new CommonException("error.attachment.get");
//        }
//        if (issueAttachmentMapper.delete(issueAttachmentDTO) != 1) {
//            throw new CommonException("error.attachment.delete");
//        }
//        return true;
//    }

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
    public List<IssueAttachmentVO> create(Long projectId, Long issueId, HttpServletRequest request) {
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
        IssueAttachmentDTO issueAttachmentDTO = new IssueAttachmentDTO();
        issueAttachmentDTO.setIssueId(issueId);
        List<IssueAttachmentDTO> issueAttachmentDTOList = issueAttachmentMapper.select(issueAttachmentDTO);
        List<IssueAttachmentVO> result = new ArrayList<>();
        if (issueAttachmentDTOList != null && !issueAttachmentDTOList.isEmpty()) {
            issueAttachmentDTOList.forEach(attachment -> {
                IssueAttachmentVO issueAttachmentVO = new IssueAttachmentVO();
                BeanUtils.copyProperties(attachment, issueAttachmentVO);
                issueAttachmentVO.setUrl(attachmentUrl + "/" + BACKETNAME + "/" + attachment.getUrl());
                result.add(issueAttachmentVO);
            });
        }
        return result;
    }

    @Override
    public Boolean delete(Long projectId, Long issueAttachmentId) {
        IssueAttachmentDTO issueAttachmentDTO = issueAttachmentMapper.selectByPrimaryKey(issueAttachmentId);
        if (issueAttachmentDTO == null) {
            throw new CommonException("error.attachment.get");
        }
        Boolean result = iIssueAttachmentService.deleteBase(issueAttachmentDTO.getAttachmentId());
        String url = null;
        try {
            url = URLDecoder.decode(issueAttachmentDTO.getUrl(), "UTF-8");
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
    public int deleteByIssueId(Long issueId) {
        IssueAttachmentDTO issueAttachmentDTO = new IssueAttachmentDTO();
        issueAttachmentDTO.setIssueId(issueId);
        return issueAttachmentMapper.delete(issueAttachmentDTO);
    }
}
