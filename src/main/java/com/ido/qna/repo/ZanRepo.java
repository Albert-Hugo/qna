package com.ido.qna.repo;

import com.ido.qna.entity.ZanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZanRepo extends JpaRepository<ZanRecord,Integer> {
}
