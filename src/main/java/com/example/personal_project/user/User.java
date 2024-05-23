package com.example.personal_project.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nickname;

    // 로그인 한 유저의 고유 ID가 들어감
    @Column(unique = true)
    private String loginId;

    private String password;

    @Column(unique = true)
    private String email;

    private String url;

    private LocalDateTime createDate;

    public User(String loginId, String password, List<SimpleGrantedAuthority> authorities) {
    }
}
