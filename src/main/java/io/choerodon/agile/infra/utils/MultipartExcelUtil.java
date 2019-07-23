package io.choerodon.agile.infra.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/26.
 * Email: fuqianghuang01@gmail.com
 */
public class MultipartExcelUtil implements MultipartFile {

    private final String name;

    private String originalFilename;

    private String contentType;

    private final byte[] content;

    public MultipartExcelUtil(String name, String originalFilename, Workbook workbook) {
        Assert.hasLength(name, "Name must not be null");
        this.name = name;
        this.originalFilename = (originalFilename != null ? originalFilename : "");
        this.contentType = "application/vnd.ms-excel";
        this.content = ExcelUtil.getBytes(workbook);
    }


    /**
     * Create a new MultipartExcel with the given content.
     *
     * @param name    the name of the file
     * @param content the content of the file
     */
    public MultipartExcelUtil(String name, byte[] content) {
        this(name, "", null, content);
    }


    /**
     * Create a new MultipartExcel with the given content.
     *
     * @param name             the name of the file
     * @param originalFilename the original filename (as on the client's machine)
     * @param contentType      the content type (if known)
     * @param content          the content of the file
     */
    public MultipartExcelUtil(String name, String originalFilename, String contentType, byte[] content) {
        Assert.hasLength(name, "Name must not be null");
        this.name = name;
        this.originalFilename = (originalFilename != null ? originalFilename : "");
        this.contentType = contentType;
        this.content = (content != null ? content : new byte[0]);
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return (this.content.length == 0);
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException{
        FileCopyUtils.copy(this.content, dest);
    }

}
