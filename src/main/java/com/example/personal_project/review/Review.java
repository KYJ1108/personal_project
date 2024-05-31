package com.example.personal_project.review;

import com.example.personal_project.hospital.Hospital;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    private String username;
    private int rating;
    private String comment;
    private LocalDateTime createDate;
}
