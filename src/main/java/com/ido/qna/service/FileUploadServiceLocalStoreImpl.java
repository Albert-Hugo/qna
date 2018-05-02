package com.ido.qna.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class FileUploadServiceLocalStoreImpl implements FileUploadService {
    @Value("${storage.dir}")
    private  String BASE_DIR ;
    @Value("${static-file-host}")
    private  String STATIC_FILE_HOST ;

    public String upload(String fileName, InputStream is,Integer userId) throws IOException {
        if(fileName==null || fileName.equals("")){
            throw new IllegalArgumentException("file name is null or empty");
        }
        if(is==null){
            throw new IllegalArgumentException("input stream can not be null");
        }
        is = new BufferedInputStream(is);
//        log.info(fileName);
        String accessPrefix = userId+"/" + LocalDate.now()+"/";
        log.info(BASE_DIR +accessPrefix);
        Path userDir = Paths.get(BASE_DIR +userId);
        if(!Files.exists(userDir)){
            Files.createDirectory(userDir);
        }
        String destinationDir  = BASE_DIR +accessPrefix;
        Path desDir = Paths.get(destinationDir);
        if(!Files.exists(desDir)){
            Files.createDirectory(desDir);
        }
        String absPath = destinationDir +"/" +fileName;
        Path dir = Paths.get(absPath);
        if (!Files.exists(dir)) {
            Files.createFile(dir);
            log.info("file store in {}",absPath);
        }

        FileOutputStream fos = new FileOutputStream(dir.toFile());
        byte[] buffer = new byte[1024];
        while (-1 != is.read(buffer)) {
            fos.write(buffer);
        }
        if(fos!=null){
            fos.close();
        }
        if(is!=null){
            is.close();
        }
        //the file is stored in the  BASE_DIR/fileName
        return STATIC_FILE_HOST+accessPrefix+fileName;
    }

    @Override
    public String upload(String fileName, InputStream is, Integer userId, Map<String, String> header) throws IOException {
        return null;
    }
}
