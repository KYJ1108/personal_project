package com.example.personal_project.security;

import com.example.personal_project.user.User;
import com.example.personal_project.user.UserDetail;
import com.example.personal_project.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MyOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        MySocialUser mySocialUser;

        switch (registrationId) {
            case "google" -> mySocialUser = googleService(oauth2User);
            case "kakao" -> mySocialUser = kakaoService(oauth2User);
            case "naver" -> mySocialUser = naverService(oauth2User);
            default -> throw new IllegalStateException("Unexpected value: " + registrationId);
        }

        User user = userRepository.findByLoginId(mySocialUser.getSub()).orElse(null);

        if (user == null) {
            user = new User();
            user.setLoginId(mySocialUser.getSub());
            user.setPassword(mySocialUser.getPass());
            user.setNickname(mySocialUser.getName());
            user.setEmail(mySocialUser.getEmail());
            user.setCreateDate(LocalDateTime.now());

            userRepository.save(user);
        }

        // 권한 설정
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        List<SimpleGrantedAuthority> authorities = List.of(authority);

        // UserDetail 객체 생성
        UserDetail userDetail = new UserDetail(user, authorities);

        // 인증 토큰을 생성하여 SecurityContextHolder에 설정
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return oauth2User;
    }

    public MySocialUser googleService(OAuth2User user) {
        String sub = user.getAttribute("sub");
        String pass = "";
        String name = user.getAttribute("name");
        String email = user.getAttribute("email");

        return new MySocialUser(sub, pass, name, email);
    }

   public MySocialUser kakaoService(OAuth2User user){
       String sub = user.getAttribute("id").toString();
       String pass = "";

       Map<String, Object> kakaoAccount = user.getAttribute("kakao_account");
       Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
       String name = (String) profile.get("nickname");
       String email = (String) kakaoAccount.get("email");

       return new MySocialUser(sub, pass, name, email);
   }

   public MySocialUser naverService(OAuth2User user){
       Map<String, Object> response = user.getAttribute("response");
       String sub = response.get("id").toString();
       String pass = "";
       String name = response.get("name").toString();
       String email = response.get("email").toString();

       return new MySocialUser(sub, pass, name, email);
   }
}
