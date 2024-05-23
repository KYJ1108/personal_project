package com.example.personal_project.community;

import com.example.personal_project.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Integer> {

    Page<Community> findAllByTitleContainingOrPostDescriptionContaining(String title, String postDescription, Pageable pageable);

    List<Community> findByUser(User user);

//    List<Community> findByAuthorId(String loginId);
}
