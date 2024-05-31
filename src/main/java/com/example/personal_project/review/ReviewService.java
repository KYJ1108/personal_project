package com.example.personal_project.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private ReviewRepository reviewRepository;

    public List<Review> findAllByHospitalId(Long hospitalId){
        return reviewRepository.findByHospitalId(hospitalId);
    }

    public Review save(Review review){
        return reviewRepository.save(review);
    }

    public void deleteById(Long id){
        reviewRepository.deleteById(id);
    }
}
