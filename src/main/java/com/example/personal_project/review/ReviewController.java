package com.example.personal_project.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private ReviewService reviewService;

//    @PostMapping("/review")
//    public String addReview(@RequestParam Long hospitalId, @RequestParam String username, @RequestParam int rating, @RequestParam String comment){
//        Review review = new Review();
//        review.setHospital(hospitalId);
//        review.setUsername(username);
//        review.setRating(rating);
//        review.setComment(comment);
//        reviewService.save(review);
//        return "redirect:/hospital/"+hospitalId;
//    }
}
