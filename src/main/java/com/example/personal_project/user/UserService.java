package com.example.personal_project.user;

import com.example.personal_project.DataNotFoundException;
import com.example.personal_project.community.Community;
import com.example.personal_project.community.CommunityController;
import com.example.personal_project.community.CommunityRepository;
import com.example.personal_project.community.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResourceLoader resourceLoader;
    private final CommunityRepository communityRepository;
    private final CommunityService communityService;

    private final RestTemplate restTemplate = new RestTemplate();

    public void create(String loginId,String nickname, String email, String password) {
        User user = new User();
        user.setLoginId(loginId);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreateDate(LocalDateTime.now());
        this.userRepository.save(user);
    }

    public User getUser(String loginId){
        Optional<User> user = this.userRepository.findByLoginId(loginId);
        if (user.isPresent()) {
            return user.get();
        }else{
            throw new DataNotFoundException("user not found");
        }
    }

    public void save(User user){
        this.userRepository.save(user);
    }

    public void changePassword(String userId, String oldPassword, String newPassword) throws CustomException{
        Optional<User> userOptional = userRepository.findByLoginId(userId);
        if (userOptional.isPresent()){
            User user = userOptional.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())){
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            }else{
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
        }else{
            throw new DataNotFoundException("User not found");
        }
    }

    public void updateProfile(String loginId, String nickname, String email) throws CustomException {
        // 데이터베이스에서 사용자를 검색
        Optional<User> userOptional = userRepository.findByLoginId(loginId);

        if (!userOptional.isPresent()){
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
        }

        // 사용자 정보를 업데이트
        User user = userOptional.get();
        user.setNickname(nickname);
        user.setEmail(email);

        // 업데이트된 사용자를 데이터베이스에 다시 저장
        userRepository.save(user);
    }

    public String temp_url(MultipartFile file){
        if(!file.isEmpty()){
            try {
                String path = resourceLoader.getResource("classpath:/static").getFile().getPath();
                File fileFolder = new File("/img");
                if(!fileFolder.exists()){
                    fileFolder.mkdirs();
                }
                String filePath = "/img/" + UUID.randomUUID().toString()+"."+ file.getContentType().split("/")[1];
                file.transferTo(Paths.get(path+filePath));
                return filePath;
            }catch (IOException ignored){

            }
        }
        return null;
    }

    public void saveimage(User user,String url){
        try{
            String path = resourceLoader.getResource("classpath:/static").getFile().getPath();
            if(user.getUrl() != null){
                File oldFile = new File(path+user.getUrl());
                if(oldFile.exists()){
                    oldFile.delete();
                }
            }

            user.setUrl(url);
            userRepository.save(user);
        }catch (IOException ignored){

        }
    }

    public void connectSocialUser(String loginId, String nickname, String email){
        Optional<User> user = userRepository.findByLoginId(loginId);
        if (user.isPresent()){
            User user1 = user.get();
            user1.setNickname(nickname);
            user1.setEmail(email);
            userRepository.save(user1);
        } else {
            User newUser = new User();
            newUser.setLoginId(loginId);
            newUser.setNickname(nickname);
            newUser.setEmail(email);
            userRepository.save(newUser);
        }
    }

    // 사용자가 작성한 게시물 가져오기
    public List<Community> getUserPosts(String loginId){
        User user = userRepository.findByLoginId(loginId).orElse(null);
        if (user!= null){
            return communityRepository.findByUser(user);
        }
        return Collections.emptyList();

//        return communityService.getUserPosts(loginId);
    }
}
