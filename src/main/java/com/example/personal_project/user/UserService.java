package com.example.personal_project.user;

import com.example.personal_project.DataNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResourceLoader resourceLoader;

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

//    public void socialLogin(String code, String registrationId) {
//        String accessToken = getAccessToken(code, registrationId);
//        JsonNode userResourceNode = getUserResource(accessToken, registrationId);
//        System.out.println("userResourceNode = " + userResourceNode);
//
//        String id = userResourceNode.get("id").asText();
//        String email = userResourceNode.get("email").asText();
//        String nickname = userResourceNode.get("name").asText();
//        System.out.println("id = " + id);
//        System.out.println("email = " + email);
//        System.out.println("nickname = " + nickname);
//    }
//
//    private String getAccessToken(String authorizationCode, String registrationId) {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("code", authorizationCode);
//        params.add("client_id", clientId);
//        params.add("client_secret", clientSecret);
//        params.add("redirect_uri", redirectUri);
//        params.add("grant_type", "authorization_code");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        HttpEntity entity = new HttpEntity(params, headers);
//
//        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
//        JsonNode accessTokenNode = responseNode.getBody();
//        return accessTokenNode.get("access_token").asText();
//    }
//
//    private JsonNode getUserResource(String accessToken, String registrationId) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + accessToken);
//        HttpEntity entity = new HttpEntity(headers);
//        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
//    }
}
