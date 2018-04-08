package com.ido.qna.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileUploadServiceLocalStoreImpl implements FileUploadService {
    private final String BASE_DIR = "/Users/rainful/Documents/imgs/";

    public String upload(String fileName, InputStream is) throws IOException {
        if(fileName==null || fileName.equals("")){
            throw new IllegalArgumentException("file name is null or empty");
        }
        if(is==null){
            throw new IllegalArgumentException("input stream can not be null");
        }
        is = new BufferedInputStream(is);
        log.info(fileName);
        String absPath = BASE_DIR + fileName;
        Path dir = Paths.get(absPath);
        if (!Files.exists(dir)) {
            Files.createFile(dir);
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

        return "imgs/"+fileName;
    }

}
