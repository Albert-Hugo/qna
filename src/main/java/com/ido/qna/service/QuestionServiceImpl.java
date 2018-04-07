package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.entity.Question;
import com.ido.qna.entity.QuestionLikeRecord;
import com.ido.qna.repo.QuestionLikeRecordRepo;
import com.ido.qna.repo.QuestionRepo;
import com.ido.qna.repo.UserInfoRepo;
import com.ido.qna.service.domain.CacheVoteRecords;
import com.ido.qna.util.CacheMap;
import com.ido.qna.util.FunctionInterface;
import com.rainful.dao.SqlAppender;
import com.rainful.util.BeanUtil;
import com.rainful.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.ido.qna.QnaApplication.toUpdateUserInfo;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService,FunctionInterface.BeforeCleanUp<Integer> {
    @Autowired
    QuestionRepo repo;
    @Autowired
    UserInfoRepo useRepo;
    @Autowired
    @Qualifier("mysqlManager")
    EntityManager em;
    @Autowired
    QuestionLikeRecordRepo likeRecordRepo;
    @Autowired
    FileUploadService uploadService;

    private CacheMap<Integer> detailReadCountTable = new CacheMap<>(new ConcurrentHashMap<>(10), this);
    private CacheMap<Integer> likeRecordTable = new CacheMap<>(new ConcurrentHashMap<>(10), (toRemove->{
        if(toRemove== null || toRemove.size() == 0){
            return;
        }

        Set<QuestionLikeRecord> toSave = new HashSet<>(toRemove.size());
        for (Map.Entry<Integer, Object> entry : toRemove.entrySet()) {
            CacheVoteRecords cacheVoteRecords = (CacheVoteRecords) entry.getValue();
            toSave.addAll(cacheVoteRecords.getVoteRecords());

        }
        log.info("flushing question like record  cache to db");
        // update the already exist record instead of save one more for those user already vote before
        likeRecordRepo.save(toSave);

    }),"like-record-clean-up");


    @Override
    public void vote(QuestionController.VoteReq req) {
//        if(likeRecordTable.get(req.getQuestionId()))
        //update the vote like , if user already vote , change to like  or dislike
        //if not ,put into the like Set
        CacheVoteRecords cacheVoteRecords = (CacheVoteRecords) likeRecordTable.get(req.getQuestionId());
        if(cacheVoteRecords == null){
            log.error("the cache vote record should not be null in here");
        }else{
            //update record set
            QuestionLikeRecord rc = voteToQesLikeRd(req);
            int c = rc.getLiked() ? 1 : -1 ;
            //update vote count in cache
            cacheVoteRecords.setVoteCount(cacheVoteRecords.getVoteCount()+c);
            cacheVoteRecords.getVoteRecords().add(rc);

        }
    }

    private QuestionLikeRecord voteToQesLikeRd(QuestionController.VoteReq req ){
        return QuestionLikeRecord.builder()
                .liked(req.getLike())
                .id(req.getId())
                .questionId(req.getQuestionId())
                .userId(req.getUserId())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void beforeCleanUp(Map<Integer, Object> toRemove) {
        if(toRemove== null || toRemove.size() == 0){
            return;
        }
        List<Question> toUpdatas = new ArrayList<>(toRemove.size());
        for (Map.Entry<Integer, Object> entry : toRemove.entrySet()) {
            Integer id = entry.getKey();
            Integer readCount = (Integer) entry.getValue();
            Question q = new Question();
            q.setId(id);
            q.setReadCount(readCount);
            Question toUpdate = repo.findOne(id);
            BeanUtil.copyNonNullProperties(q, toUpdate);
            toUpdatas.add(toUpdate);

        }
        log.info("flushing read count cache to db");
        repo.save(toUpdatas);

    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void ask(QuestionController.QuestionReq req,MultipartFile file) {
        Date now = new Date();
        if (toUpdateUserInfo) {
            new SqlAppender(em)
                    .update("user_info")
                    .set("nick_name", "nickName", req.getNickName())
                    .set("avatar_url", "avatar", req.getAvatarUrl())
                    .set("gender", "gender", req.getGender())
                    .set("phone", "phone", req.getPhone())
                    .update_where_1e1()
                    .update_where_and("id", "id", req.getUserId())
                    .execute_update();
        }
        String filePath = null;
        if(file != null){
            try {
                filePath = uploadService.upload(file.getOriginalFilename(),file.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        repo.save(Question.builder()
                .content(req.getContent())
                .title(req.getTitle())
                .imgUrl(filePath)
                .topicId(req.getTopicId())
                .userId(req.getUserId())
                .createTime(now)
                .readCount(0)
                .updateTime(now)
                .build());
    }

    @Override
    public Page<Map<String, Object>> getLatest(Pageable pageable) {
        StringBuilder sql = new StringBuilder("select q.id, q.title, q.content, q.create_time,q.read_count," +
                "u.nick_name as userName , u.id as userId,  t.name as topicName from question q" +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1 order by create_time desc");
        List<Map<String, Object>> result = new SqlAppender(em, sql)
                .limit(pageable.getOffset(), pageable.getPageSize())
                .getResultList();

        //get the latest read count from memory
        result.stream().forEach(m -> {
            Integer count = (Integer) detailReadCountTable.get((Integer) m.get("id"));
            if (count != null) {
                m.put("readCount", count);

            } else {
                return;
            }

        });

        StringBuilder countSql = new StringBuilder("select count(*) from question q " +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1");
        long total = new SqlAppender(em, countSql)
                .count();
        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Map detail(QuestionController.DetailReq req) {
        int questionId = req.getQuestionId();
        int userId = req.getUserId();
        StringBuilder sql = new StringBuilder("select q.id, q.title, q.content, q.create_time,q.read_count, q.img_url" +
                " ,u.nick_name as userName , u.id as userId, u.avatar_url , t.name as topicName from question q" +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1 ");
        // add vote record , include if the user already vote for this question and how many user already vote

        List<Map<String, Object>> result = new SqlAppender(em, sql)
                .and("q.id", "id", Integer.valueOf(questionId))
                .getResultList();
        QuestionLikeRecord likeRecord = likeRecordRepo.findByUserIdAndQuestionId(userId,questionId);

        // get the vote cache from cache table ,if not exist , fetch it from db ,and store to cache
        CacheVoteRecords cacheVoteRecords = (CacheVoteRecords) likeRecordTable.get(questionId);
        int voteCount = 0;
        if(cacheVoteRecords == null){
            int likeC = likeRecordRepo.countByQuestionIdAndLiked(questionId,true);
            int disLikeC = likeRecordRepo.countByQuestionIdAndLiked(questionId,false);
            voteCount = likeC - disLikeC;
            cacheVoteRecords = CacheVoteRecords.builder()
                    //only need to fetch the count
                    .voteCount(voteCount)
                    .voteRecords(new HashSet<>(0))
                    .build();
            likeRecordTable.put(questionId,cacheVoteRecords);
        }else{
            voteCount = cacheVoteRecords.getVoteCount();
        }


        final Integer vc = Integer.valueOf(voteCount);
        result.stream().forEach(r -> {
            r.put("createTime", DateUtil.toYyyyMMdd_HHmmss((Date) r.get("createTime")));
            r.put("voteCount",vc);
            r.put("userVoteRecord",likeRecord);
        });

        if (!result.isEmpty()) {
            Map m = result.get(0);
            Integer idg = Integer.valueOf(questionId);
            Integer readCount = (Integer) detailReadCountTable.get(idg);
            if (readCount == null) {
                detailReadCountTable.put(idg, (Integer) m.get("readCount") + 1);
            } else {
                ++readCount;
                detailReadCountTable.put(questionId, readCount);
            }
            //return the newest read count
            m.put("readCount",readCount);
            return m;
        }
        return null;
    }


}
