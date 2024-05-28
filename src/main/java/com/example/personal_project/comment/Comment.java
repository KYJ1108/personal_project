package com.example.personal_project.comment;

import com.example.personal_project.community.Community;
import com.example.personal_project.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    // 댓글 내용
    @Column(columnDefinition = "TEXT")
    private String content;

    // 작성 시간
    private LocalDateTime createDate;

    // 작성자
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
