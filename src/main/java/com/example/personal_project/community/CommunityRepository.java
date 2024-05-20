package com.example.personal_project.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Integer> {

    Page<Community> findAllByTitleContainingOrPostDescriptionContaining(String title, String postDescription, Pageable pageable);
}
