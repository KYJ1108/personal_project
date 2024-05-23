package com.example.personal_project.community;


import com.example.personal_project.comment.Comment;
import com.example.personal_project.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외부 키의 컬럼 이름을 user_id로 지정
    private User user;

    private String title;

    private String postPhoto;

    @Column(columnDefinition = "TEXT")
    private String postDescription;

    private LocalDateTime postDateTime;

    // 기본 생성자 추가
    public Community() {
    }

    // 모든 필드를 초기화하는 생성자
    public Community(User user, String title, String postPhoto, String postDescription, LocalDateTime postDateTime) {
        this.user = user;
        this.title = title;
        this.postPhoto = postPhoto;
        this.postDescription = postDescription;
        this.postDateTime = postDateTime;
    }

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    private List<Comment> comments;

}