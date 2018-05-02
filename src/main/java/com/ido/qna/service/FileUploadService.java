package com.ido.qna.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface FileUploadService {
    public String upload(String fileName,InputStream is,Integer userId) throws IOException;
    public String upload(String fileName, InputStream is, Integer userId, Map<String,String> header) throws IOException;

}
