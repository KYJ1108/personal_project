package com.example.personal_project.comment;

import com.example.personal_project.community.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByCommunity(Community community);

    Page<Comment> findByCommunityId(Integer communityId, Pageable pageable);

//    Page<Comment> findByCommunityIdOrderedByVoteCount(@Param("communityId") int communityId, Pageable pageable);

    List<Comment> findByUserId(Long userId);
}
