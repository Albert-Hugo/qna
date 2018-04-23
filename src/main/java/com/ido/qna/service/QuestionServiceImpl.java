package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.controller.request.HotQuestionReq;
import com.ido.qna.controller.request.ListQuestionReq;
import com.ido.qna.entity.Question;
import com.ido.qna.entity.QuestionLikeRecord;
import com.ido.qna.entity.UserInfo;
import com.ido.qna.repo.QuestionImageRepo;
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
public class QuestionServiceImpl implements QuestionService, FunctionInterface.BeforeCleanUp<Integer> {
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
    @Autowired
    ReplyService replyService;
    @Autowired
    QuestionImageRepo questionImageRepo;

    final String BASIC_QUESTION_RESULT_LIST = "q.id, q.title, q.content, q.create_time,q.read_count" +
            ", u.avatar_url, u.nick_name as userName , u.id as userId, u.gender ,ut.title as userTitle, ut.title_color as titleColor" +
            ", t.name as topicName ";

    private CacheMap<Integer> detailReadCountTable = new CacheMap<>(new ConcurrentHashMap<>(10), this);
    private CacheMap<Integer> likeRecordTable = new CacheMap<>(new ConcurrentHashMap<>(10), (toRemove -> {
        if (toRemove == null || toRemove.size() == 0) {
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

    }), 2 * 60, "like-record-clean-up");

    @Override
    public void delete(int userId, int questionId) {
        Question q = repo.findOne(questionId);
        if(q==null){
            return;
        }

        if(q.getUserId() != userId){
            throw new RuntimeException("删除失败");
        }
        repo.delete(questionId);
    }

    @Override
    public void vote(QuestionController.VoteReq req) {
//        if(likeRecordTable.get(req.getQuestionId()))
        //update the vote like , if user already vote , change to like  or dislike
        //if not ,put into the like Set
        CacheVoteRecords cacheVoteRecords = (CacheVoteRecords) likeRecordTable.get(req.getQuestionId());
        if (cacheVoteRecords == null) {
            log.error("the cache vote record should not be null in here");
        } else {
            //update record set
            QuestionLikeRecord rc = voteToQesLikeRd(req);
            int c = rc.getLiked() ? 1 : -1;
            //update vote count in cache
            cacheVoteRecords.setVoteCount(cacheVoteRecords.getVoteCount() + c);
            cacheVoteRecords.getVoteRecords().add(rc);

        }
    }

    private QuestionLikeRecord voteToQesLikeRd(QuestionController.VoteReq req) {
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
        if (toRemove == null || toRemove.size() == 0) {
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

    private boolean alreadyUpdatedToday(int userId) {
        UserInfo userInfo = useRepo.findOne(userId);
        log.info(userInfo.toString());
        return useRepo.countByIdAndUpdateTime(userId, new Date()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Question ask(QuestionController.QuestionReq req, MultipartFile file) {
        Date now = new Date();
        if (toUpdateUserInfo && !alreadyUpdatedToday(req.getUserId())) {
            log.info("update user info: {} ", req.toString());
            new SqlAppender(em)
                    .update("user_info")
                    .set("nick_name", "nickName", req.getNickName())
                    .set("avatar_url", "avatar", req.getAvatarUrl())
                    .set("gender", "gender", req.getGender())
                    .set("phone", "phone", req.getPhone())
                    .final_set("update_time", "update_time", new Date())
                    .update_where_1e1()
                    .update_where_and("id", "id", req.getUserId())
                    .execute_update();
        }
        String filePath = null;
        if (file != null) {
            try {
                filePath = uploadService.upload(file.getOriginalFilename(), file.getInputStream(), req.getUserId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return repo.save(Question.builder()
                .content(req.getContent())
                .title(req.getTitle())
//                .imgUrl(filePath)
                .topicId(req.getTopicId())
                .userId(req.getUserId())
                .createTime(now)
                .readCount(0)
                .updateTime(now)
                .build());
    }

    @Override
    public Page<Map<String, Object>> findQuestions(ListQuestionReq req) {

        StringBuilder sql = new StringBuilder("select " + BASIC_QUESTION_RESULT_LIST +
                " from question q" +
                " left join user_info u on q.user_id = u.id" +
                " left join user_title ut on ut.id = u.title_id " +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1 ");
        List<Map<String, Object>> result = new SqlAppender(em, sql)
                .and("q.topic_id", "topic_id", req.getTopicId())
                .and("q.user_id", "user_id", req.getUserId())
                .orderBy(req.getPageQuery().getSort())
                .limit(req.getPageQuery().getOffset(), req.getPageQuery().getLimit())
                .getResultList();

        //get the latest read count from memory
        addReturnVal(result);

        return new PageImpl<>(result);
    }

    private void addReturnVal(List<Map<String, Object>> result) {
        result.stream().forEach(m -> {
            int questionId = (int) m.get("id");
            Integer count = (Integer) detailReadCountTable.get(questionId);
            if (count != null) {
                m.put("readCount", count);

            }
            m.put("replyCount", replyService.getReplyCount(questionId));
            m.put("images", questionImageRepo.findByQuestionId(questionId));
            m.put("voteCount", getVoteCount(questionId));
            m.put("createTime", DateUtil.toYyyyMMdd_HHmmss((Date) m.get("createTime")));

        });
    }

    @Override
    public Page<Map<String, Object>> hotestQuestions(HotQuestionReq req) {
        //"q.id, q.title, q.content, q.create_time,q.read_count," +
//        "u.nick_name as userName , u.id as userId, u.gender ,ut.title as userTitle, ut.title_color as titleColor" +
//                ", t.name as topicName "
        StringBuilder sql = new StringBuilder("select distinct  " + BASIC_QUESTION_RESULT_LIST +
                ",(select count(*) from reply r1 where r1.question_id = q.id) as replyCount" +
                " from question q " +
                " left join reply r  on r.question_id = q.id\n" +
                " left join user_info u on q.user_id = u.id" +
                " join user_title ut on ut.id = u.title_id " +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1 order by replyCount DESC");
        List<Map<String, Object>> result = new SqlAppender(em, sql)
                .ownDefinedColumnAlias(Arrays.asList("id", "title"
                        , "content", "createTime", "readCount"
                        , "userName", "userId", "gender"
                        , "userTitle", "titleColor", "topicName"
                        , "replyCount"))
                .limit(req.getPageQuery().getOffset(), req.getPageQuery().getLimit())
                .getResultList();

        //get the latest read count from memory
        addReturnVal(result);

        return new PageImpl<>(result);
    }


    @Override
    public Map detail(QuestionController.DetailReq req) {
        int questionId = req.getQuestionId();
        int userId = req.getUserId();
        StringBuilder sql = new StringBuilder("select q.id, q.title, q.content, q.create_time,q.read_count" +
                " ,u.nick_name as userName , u.id as userId, u.avatar_url , t.name as topicName from question q" +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1 ");
        // add vote record , include if the user already vote for this question and how many user already vote

        List<Map<String, Object>> result = new SqlAppender(em, sql)
                .and("q.id", "id", Integer.valueOf(questionId))
                .getResultList();

        //decide if the login user like this post or not
        QuestionLikeRecord likeRecord = likeRecordRepo.findByUserIdAndQuestionId(userId, questionId);


        int voteCount = getVoteCount(questionId);


        final Integer vc = Integer.valueOf(voteCount);
        result.stream().forEach(r -> {
            r.put("createTime", DateUtil.toYyyyMMdd_HHmmss((Date) r.get("createTime")));
            r.put("voteCount", vc);
            r.put("userVoteRecord", likeRecord);
            r.put("images",questionImageRepo.findByQuestionId(questionId));
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
            m.put("readCount", readCount);
            return m;
        }
        return null;
    }

    /**
     * get the vote count for question
     *
     * @param questionId question id
     * @return (like - disLike)
     */
    private int getVoteCount(int questionId) {
        // get the vote cache from cache table ,if not exist , fetch it from db ,and store to cache
        CacheVoteRecords cacheVoteRecords = (CacheVoteRecords) likeRecordTable.get(questionId);
        int voteCount = 0;
        if (cacheVoteRecords == null) {
            int likeC = likeRecordRepo.countByQuestionIdAndLiked(questionId, true);
            int disLikeC = likeRecordRepo.countByQuestionIdAndLiked(questionId, false);
            voteCount = likeC - disLikeC;
            cacheVoteRecords = CacheVoteRecords.builder()
                    //only need to fetch the count
                    .voteCount(voteCount)
                    .voteRecords(new HashSet<>(0))
                    .build();
            likeRecordTable.put(questionId, cacheVoteRecords);
        } else {
            voteCount = cacheVoteRecords.getVoteCount();
        }
        return voteCount;
    }

    @Override
    public List<Map<String, Object>> checkQuestionsNeedToGenerateReputation() {
        StringBuilder sql = new StringBuilder("select q.id, q.user_id from question q" +
                " left join user_info u on q.user_id = u.id " +
                " join (select qlr.question_id ,count(qlr.id) as count_id from question_like_record qlr group by qlr.question_id having count_id > 0 ) as t1 on t1.question_id = q.id ");

        List<Map<String, Object>> result = new SqlAppender(em, sql)
                .getResultList();
        return result;
    }
}
