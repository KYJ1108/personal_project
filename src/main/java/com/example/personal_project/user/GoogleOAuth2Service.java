//package com.example.personal_project.user;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class GoogleOAuth2Service {
//
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String clientId;
//
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
//    private String clientSecret;
//
//    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
//    private String redirectUri;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public OAuth2AccessToken getAccessToken(String code) {
//        // 액세스 토큰 요청 파라미터 설정
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("code", code);
//        params.add("client_id", clientId);
//        params.add("client_secret", clientSecret);
//        params.add("redirect_uri", redirectUri);
//        params.add("grant_type", "authorization_code");
//
//        // HTTP 요청 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        // 액세스 토큰 요청
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
//        ResponseEntity<OAuth2AccessToken> response = restTemplate.postForEntity(
//                "https://oauth2.googleapis.com/token",
//                entity,
//                OAuth2AccessToken.class
//        );
//
//        // 액세스 토큰 반환
//        return response.getBody();
//    }
//
//    public OAuth2UserInfo getUserInfo(OAuth2AccessToken accessToken) {
//        // 액세스 토큰을 사용하여 사용자 정보 요청
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken.getTokenValue());
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//        ResponseEntity<OAuth2UserInfo> response = restTemplate.exchange(
//                "https://www.googleapis.com/oauth2/v2/userinfo",
//                HttpMethod.GET,
//                entity,
//                OAuth2UserInfo.class
//        );
//
//        // 사용자 정보 반환
//        return response.getBody();
//    }
//}
