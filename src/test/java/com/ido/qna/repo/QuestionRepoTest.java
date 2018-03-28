package com.ido.qna.repo;


import com.ido.qna.entity.Question;
import com.ido.qna.entity.QuestionLikeRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionRepoTest {
    @Autowired
    QuestionRepo repo;
    @Autowired
    QuestionLikeRecordRepo likeRecordRepo;
    @Test
    public void testLatest(){
        Sort.Order order = new Sort.Order(Sort.Direction.DESC,"createTime");
        Sort sort = new Sort(order);
        Pageable pageable = new PageRequest(0,10,sort);
        Page<Question> result = repo.findAll(pageable);
        Assert.assertNotNull(result);

    }


    @Test
    @Rollback
    @Transactional
    public void testAddVote(){
        likeRecordRepo.save(QuestionLikeRecord.builder()
                .userId(1)
                .liked(true)
                .questionId(23)
        .build());

    }

    @Test
    @Rollback
    @Transactional
    public void testCountLiked(){
        likeRecordRepo.save(QuestionLikeRecord.builder()
                .userId(1)
                .liked(true)
                .questionId(9999)
                .build());

        int count  = likeRecordRepo.countByQuestionIdAndLiked(9999,true);

        Assert.assertEquals(1,count);

    }
}
