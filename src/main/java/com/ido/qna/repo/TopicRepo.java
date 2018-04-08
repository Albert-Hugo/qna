package com.ido.qna.repo;

import com.ido.qna.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepo extends JpaRepository<Topic,Integer> {
}
