package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.entity.QuestionLikeRecord;
import com.ido.qna.entity.ZanRecord;
import com.ido.qna.repo.ZanRepo;
import com.ido.qna.service.domain.CacheVoteRecords;
import com.ido.qna.util.CacheMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ZanServiceImpl implements  ZanService{
    @Autowired
    ZanRepo zanRepo;
    private CacheMap<Integer> zanRecordTable = new CacheMap<>(new ConcurrentHashMap<>(10), (toRemove->{
        if(toRemove== null || toRemove.size() == 0){
            return;
        }
        Set<ZanRecord> toSave = new HashSet<>(toRemove.size());
        for (Map.Entry<Integer, Object> entry : toRemove.entrySet()) {
            Set<QuestionController.ZanReq> zanReqs = (Set<QuestionController.ZanReq>) entry.getValue();
            Set<ZanRecord> zanRecords =  zanReqs.stream().map(zanReq -> {
                return ZanRecord.builder()
                        .replyId(zanReq.getReplyId())
                        .userId(zanReq.getUserId())
                        .build();
            }).collect(Collectors.toSet());
            toSave.addAll(zanRecords);

        }
        log.info("flushing question zan record  cache to db");
        zanRepo.save(toSave);

    }),"zan-record-clean-up");


    @Override
    public void zan(QuestionController.ZanReq req) {
        Set<QuestionController.ZanReq> zanRecords = (Set<QuestionController.ZanReq>) zanRecordTable.get(req.getReplyId());
        if(zanRecords != null){
            zanRecords.add(req);
        }else {
            zanRecords = new HashSet<>(10);
            zanRecords.add(req);
            zanRecordTable.put(req.getReplyId(),zanRecords);
        }
    }

    @Override
    public List loadZan(int questionId, int userId, Pageable pageable) {
        return null;
    }

    @Override
    public boolean checkIfUserZanReply(int userId, int replyId) {
        Set<QuestionController.ZanReq> cacheReqs = (Set<QuestionController.ZanReq>) zanRecordTable.get(replyId);
        QuestionController.ZanReq zanReq = new QuestionController.ZanReq(replyId,userId);
        if(cacheReqs !=null && cacheReqs.contains(zanReq)){
            return true;
        }
        return zanRepo.countByUserIdAndReplyId(userId,replyId)>0;
    }

    @Override
    public long countByReplyId(int replyId) {
        Set<QuestionController.ZanReq> cacheReqs = (Set<QuestionController.ZanReq>) zanRecordTable.get(replyId);
        if(cacheReqs != null){
            return cacheReqs.size();
        }
        return zanRepo.countByReplyId(replyId);
    }
}
