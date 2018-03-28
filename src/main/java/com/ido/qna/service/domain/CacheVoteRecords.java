package com.ido.qna.service.domain;

import com.ido.qna.entity.QuestionLikeRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CacheVoteRecords {
    Set<QuestionLikeRecord> voteRecords = new HashSet<>(0);
    int voteCount;
}
