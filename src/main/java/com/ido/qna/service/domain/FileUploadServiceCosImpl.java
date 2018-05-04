package com.ido.qna.service.domain;

import com.ido.qna.service.FileUploadService;
import com.rainful.service.CosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static com.ido.qna.config.ScheduledTask.TODAY;

@Service("cosService")
@Slf4j
public class FileUploadServiceCosImpl implements FileUploadService {
    @Autowired
    private CosService cosService;
    @Override
    public String upload(String fileName, InputStream is,Integer userId) throws IOException {
        return null;
    }

    @Override
    public String upload(File file,  Integer userId) throws IOException {
        String remoteFilePath = userId+"/"+TODAY+"/"+file.getName();
        cosService.store(file,remoteFilePath);
        String urlToView = cosService.getViewUrl(remoteFilePath);
        return urlToView;
    }

    @Override
    public String upload(String fileName, InputStream is, Integer userId, Map<String, String> header) throws IOException {
        if(fileName==null || fileName.equals("")){
            throw new IllegalArgumentException("file name is null or empty");
        }
        if(is==null){
            throw new IllegalArgumentException("input stream can not be null");
        }
        is = new BufferedInputStream(is);
        String remoteFilePath = userId+"/"+TODAY+"/"+fileName;
        cosService.store(is,remoteFilePath,header);
        String urlToView = cosService.getViewUrl(remoteFilePath);
        return urlToView;
    }
}
