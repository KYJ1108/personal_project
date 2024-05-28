package com.example.personal_project.community;

import com.example.personal_project.user.User;
import com.example.personal_project.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public List<Community> getAllPosts() {
        return communityRepository.findAllByOrderByPostDateTimeDesc();  // 최신순 정렬된 게시물 가져오기
    }


    // 모든 게시물 조회
    public Page<Community> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return communityRepository.findAllByTitleContainingOrPostDescriptionContaining(kw, kw, pageable);
    }

    // 게시물 저장
    public void saveCommunity(MultipartFile postImage, String postDescription,User user) {
        try {
            // 이미지를 업로드할 디렉토리 경로 설정
            String uploadDir = "src/main/resources/static/img";

            // 디렉토리 생성 (이미 존재하면 생성하지 않음)
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 이미지 파일을 서버에 저장
            String fileName = UUID.randomUUID().toString() + "_" + postImage.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(postImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 게시물 엔티티 생성 및 저장
            Community community = new Community();
            community.setPostPhoto("/img/" + fileName); // 파일 경로를 엔티티에 저장
            community.setPostDescription(postDescription); // 설명을 엔티티에 저장
            community.setPostDateTime(LocalDateTime.now()); // 현재 시간을 엔티티에 저장
            community.setUser(user);
            communityRepository.save(community);
        } catch (IOException e) {
            throw new RuntimeException("게시물을 저장하는 동안 오류가 발생했습니다.", e);
        }
    }

//    // 사용자 정보와 함께 게시물을 가져오는 메서드
//    public List<Community> getAllCommunityPostsWithUser() {
//        List<Community> posts = communityRepository.findAll();
//        posts.forEach(post -> {
//            User user = userRepository.findById(post.getUser().getId()).orElse(null);
//            post.setUser(user);
//        });
//        return posts;
//    }

    // 게시물 삭제
    public void deleteCommunity(int id) {
        communityRepository.deleteById(id);
    }

    // 게시물 수정
    public void updateCommunity(int id, MultipartFile postImage, String postDescription) {
        try {
            // 수정할 게시물을 데이터베이스에서 가져옴
            Community existingCommunity = communityRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("해당 ID의 게시물을 찾을 수 없습니다: " + id));

            // 새로운 이미지가 업로드된 경우
            if (!postImage.isEmpty()) {
                // 기존 이미지 파일 삭제
                deleteImage(existingCommunity.getPostPhoto());

                // 새로운 이미지를 서버에 저장
                String fileName = UUID.randomUUID().toString() + "_" + postImage.getOriginalFilename();
                Path filePath = Paths.get("src/main/resources/static/img", fileName);
                Files.copy(postImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                existingCommunity.setPostPhoto("/img/" + fileName); // 엔티티에 파일 경로 저장
            }

            // 게시물 설명 업데이트
            existingCommunity.setPostDescription(postDescription);
            existingCommunity.setPostDateTime(LocalDateTime.now()); // 수정 시간 업데이트

            // 수정된 게시물 저장
            communityRepository.save(existingCommunity);
        } catch (IOException e) {
            throw new RuntimeException("게시물을 수정하는 동안 오류가 발생했습니다.", e);
        }
    }

    // 이미지 파일 삭제 메서드
    private void deleteImage(String imagePath) {
        if (imagePath != null) {
            File imageFile = new File("src/main/resources/static" + imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
    }

    public Community getCommunity(int id) {
        Optional<Community> community = communityRepository.findById(id);
        return community.orElseThrow(() -> new IllegalArgumentException("Invalid community ID: " + id));
    }

}
