package com.ido.qna.service;

import com.ido.qna.repo.TopicRepo;
import com.rainful.dao.SqlAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
@Service
public class TopicServiceImpl implements TopicService{
  @Autowired
  TopicRepo topicRepo;
  @Autowired
  @Qualifier("mysqlManager")
  EntityManager em;
  @Override
  public List loadTopic(){
    StringBuilder sql = new StringBuilder("select id, name from topic  ");
    return new SqlAppender(em, sql)
            .getResultList();
  }

}
