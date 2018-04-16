package com.ido.qna.repo;

import com.ido.qna.entity.SignInRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface SignInRecordRepo extends JpaRepository<SignInRecord,Integer> {
    int countByUserIdAndSignInDate(int userId, Date signInDate);
}
