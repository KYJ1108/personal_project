package com.example.personal_project.comment;

import com.example.personal_project.community.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByCommunity(Community community);
}
