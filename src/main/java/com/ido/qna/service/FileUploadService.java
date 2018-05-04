package com.ido.qna.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface FileUploadService {
    public String upload(String fileName,InputStream is,Integer userId) throws IOException;
    default  public String upload(File file,  Integer userId) throws IOException{
        return null;
    }
    public String upload(String fileName, InputStream is, Integer userId, Map<String,String> header) throws IOException;

}
