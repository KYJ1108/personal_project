package com.example.personal_project.hospital;

import com.example.personal_project.review.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String phone;
    private String description;

    @OneToMany(mappedBy = "hospital")
    private List<Review> reviews;
}
