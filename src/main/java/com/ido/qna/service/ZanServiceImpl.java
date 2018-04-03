package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.entity.QuestionLikeRecord;
import com.ido.qna.entity.ZanRecord;
import com.ido.qna.repo.ZanRepo;
import com.ido.qna.service.domain.CacheVoteRecords;
import com.ido.qna.util.CacheMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ZanServiceImpl implements  ZanService{
    private CacheMap<Integer> zanRecordTable = new CacheMap<>(new ConcurrentHashMap<>(10), (toRemove->{
        if(toRemove== null || toRemove.size() == 0){
            return;
        }
        //  todo finish the cache clean up logic
//
//        Set<ZanRecord> toSave = new HashSet<>(toRemove.size());
//        for (Map.Entry<Integer, Object> entry : toRemove.entrySet()) {
//            ZanRecord ZanRecords = (ZanRecord) entry.getValue();
//            toSave.addAll(cacheVoteRecords.getVoteRecords());
//
//        }
//        log.info("flushing question like record  cache to db");
//        // update the already exist record instead of save one more for those user already vote before
//        zanRepo.save(toSave);

    }),"like-record-clean-up");
    @Autowired
    ZanRepo zanRepo;
    @Override
    public void zan(QuestionController.ZanReq req) {
        //TODO
    }

    @Override
    public List loadZan(int questionId, int userId, Pageable pageable) {
        return null;
    }
}
