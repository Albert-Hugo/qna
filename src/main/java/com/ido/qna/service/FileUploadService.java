package com.ido.qna.service;

import java.io.IOException;
import java.io.InputStream;

public interface FileUploadService {
    public String upload(String fileName,InputStream is) throws IOException;

}
