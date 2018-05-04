package com.ido.qna.controller;

import com.ido.qna.controller.request.HotQuestionReq;
import com.ido.qna.controller.request.ListQuestionReq;
import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.entity.QuestionImage;
import com.ido.qna.entity.QuestionVideo;
import com.ido.qna.repo.QuestionImageRepo;
import com.ido.qna.repo.QuestionVideoRepo;
import com.ido.qna.service.*;
import com.ido.qna.util.FfmpegUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("question")
@Slf4j
public class QuestionController {
    @Autowired
    QuestionService questionServ;
    @Autowired
    ReplyService replyService;
    @Autowired
    ZanService zanService;

    @Autowired
    @Qualifier("cosService")
    FileUploadService uploadService;
    @Autowired
    TopicService topicService;
    @Autowired
    QuestionImageRepo questionImageRepo;
    @Autowired
    QuestionVideoRepo videoRepo;

    private String temDir = "temp";
    @Value("${fmmPath}")
    private String fmmPath;

    @GetMapping("topics")
    public ResponseDTO topics() throws IOException {
        return ResponseDTO.succss(topicService.loadTopic());
    }

    @PostMapping("upload")
    public ResponseDTO upload(Integer userId, Integer questionId, MultipartFile file) {
        Map<String, String> headers = new HashMap<>(2);
        headers.put("content-type", file.getContentType());
        log.info("content type is {}", file.getContentType());
        String filePath = null;
        String videoPosterUrl = null;
        String tempFileName = null;
        if (file.getContentType().equals("video/mp4")) {
            InputStream is = null;
            OutputStream fos = null;
            try {
                filePath = uploadService.upload(file.getOriginalFilename(), file.getInputStream(), userId, headers);
                if (filePath == null) {
                    return ResponseDTO.falied("filePath is null", 10000);
                }


                Path dir = Paths.get(this.temDir);
                if (!Files.exists(dir)) {
                    Files.createDirectory(dir);
                }
                is = new BufferedInputStream(file.getInputStream());
                tempFileName = temDir + "/" + file.getOriginalFilename();
                fos = new BufferedOutputStream(new FileOutputStream(tempFileName));
                while (is.available() > 0) {
                    byte[] bs = new byte[1024 * 50];
                    is.read(bs);
                    fos.write(bs);
                }

                String tempPicFilePath = tempFileName+".jpg";
                if(FfmpegUtil.screenImage(fmmPath,tempFileName,tempPicFilePath)){
                    log.info("cap success");
                }
//                Map<String, String> imageFileHeaders  = new HashMap<>(2);
//                imageFileHeaders.put("content-type", "image/jpg");
                videoPosterUrl = uploadService.upload(new File(tempPicFilePath),userId);
                log.info(filePath);


            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }
                }

            }


            videoRepo.save(QuestionVideo.builder()
                    .videoUrl(filePath)
                    .questionId(questionId)
                    .videoPosterUrl(videoPosterUrl)
                    .build());
        } else {
            questionImageRepo.save(QuestionImage.builder()
                    .imgUrl(filePath)
                    .questionId(questionId)
                    .build());
        }

        log.info(filePath);
        return ResponseDTO.succss("ok");
    }

    @PostMapping("ask")
    public ResponseDTO ask(QuestionReq req, MultipartFile file) {
        return ResponseDTO.succss(questionServ.ask(req, file));
    }

    @PostMapping("reply")
    public ResponseDTO reply(@RequestBody ReplyReq req) {
        //TODO add reply ui in the front page
        return ResponseDTO.succss(replyService.reply(req));
    }

    @PostMapping("zan")
    public ResponseDTO zan(@RequestBody ZanReq req) {
        //TODO add reply ui in the front page
        zanService.zan(req);
        return ResponseDTO.succss("ok");
    }


    @PostMapping("vote")
    public ResponseDTO vote(@RequestBody VoteReq req) {
        questionServ.vote(req);
        return ResponseDTO.succss("vote ok");
    }

    @PostMapping("list")
    public ResponseDTO list(@RequestBody ListQuestionReq req) {
        return ResponseDTO.succss(questionServ.findQuestions(req));
    }

    @PostMapping("hot")
    public ResponseDTO hotQuestions(@RequestBody HotQuestionReq req) {
        return ResponseDTO.succss(questionServ.hotestQuestions(req));
    }

    @DeleteMapping("delete")
    public ResponseDTO delete(Integer userId, Integer questionId) {
        questionServ.delete(userId, questionId);
        return ResponseDTO.succss(null);
    }

    @GetMapping("detail")
    public ResponseDTO detail(DetailReq req) {
        return ResponseDTO.succss(questionServ.detail(req));
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailReq {
        Integer questionId;
        Integer userId;


    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(exclude = {"id"})
    public static class ZanReq {
        Integer replyId;
        Integer id;
        Integer userId;

        public ZanReq(Integer replyId, Integer userId) {
            this.replyId = replyId;
            this.userId = userId;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoteReq {
        Integer questionId;
        Integer id;
        Integer userId;
        Boolean like;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionReq {
        String content;
        String title;
        Integer topicId;
        Integer userId;
        String nickName;
        String avatarUrl;
        String phone;
        byte gender;
        String country;
        String province;
        String city;
//        UserBasicInfo userBasicInfo;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyReq {
        String content;
        Integer questionId;
        Integer userId;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBasicInfo {
        String nickName;
        String avatarUrl;
        String phone;
        byte gender;
        String country;
        String province;
        String city;
    }

}
