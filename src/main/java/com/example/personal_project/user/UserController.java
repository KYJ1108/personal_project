package com.example.personal_project.user;


import com.example.personal_project.community.Community;
import com.example.personal_project.community.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final SendEmailService sendEmailService;
    private final PasswordEncoder passwordEncoder;
    private final CommunityService communityService;
    private static final String UPLOAD_DIR = "./uploads"; // 이미지 업로드 디렉토리

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm){
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try {
            userService.create(userCreateForm.getLoginId(), userCreateForm.getNickname(), userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch (Exception e){
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "login_form";
    }

    @GetMapping("findPw")
    public String findPw(UserPwRequestDto userPwRequestDto){
        return "findPassword_form";
    }

    @PostMapping(value = "/findPw")
    public String pwFind(@Valid UserPwRequestDto userPwRequestDto, BindingResult bindingResult, org.springframework.ui.Model model) {
        if(bindingResult.hasErrors()){
            model.addAttribute("error","아이디와 이메일을 확인해주세요.");
            return "findPassword_form";
        }

        String tempPassword = this.sendEmailService.getTempPassword();
        User user = this.userService.getUser(userPwRequestDto.getLoginId());
        user.setPassword(passwordEncoder.encode(tempPassword));
        this.userService.save(user);
        this.sendEmailService.mailSend(user,"임시비밀번호 발송","임시 비밀번호는" +tempPassword+" 입니다.");

        return "redirect:/user/login"; // 비밀번호 재설정이 성공했음을 알리는 페이지로 이동
    }

    @GetMapping("/modifyPassword")
    public String modifyPw(){
        return "modifyPassword_form";
    }

    @PostMapping("/modifyPassword")
    public String changePassword(org.springframework.ui.Model model, Principal principal,
                                 @RequestParam("currentPassword")String currentPassword,
                                 @RequestParam("newPassword")String newPassword,
                                 @RequestParam("confirmPassword")String confirmPassword){
        if (!newPassword.equals(confirmPassword)){
//             비밀번호가 일치하지 않음.
            model.addAttribute("error", "새로운 비밀번호와 새로운 비밀번호 확인이 일치하지 않습니다.");
            return "modifyPassword_form";
        }
        try {
            // 현재 사용자의 아이디를 얻어옴
            String userName = principal.getName();
            // 사용자의 비밀번호 변경을 userService를 통해 처리
            userService.changePassword(userName, currentPassword, newPassword);
            return "redirect:/user/profile";
        } catch (CustomException e){
            // 비밀번호 변경 중에 예외가 발생한 경우 에러 메시지를 받아와서 비밀번호 변경 페이지로
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "modifyPassword_form";
        }
    }

    @GetMapping("/profile")
    public String userProfile(Principal principal, Model model){
        // 사용자가 로그인되어 있는지 확인
        if (principal == null){
            // 사용자가 로그인되어 있지 않은 경우 처리
            return "redirect:/user/login";
        }

        // 현재 로그인한 사용자의 아이디를 가져옴
        String userId = principal.getName();

        // userService를 사용하여 사용자의 정보를 가져옴
        User user = userService.getUser(userId);
        model.addAttribute("user", user);

        // 사용자가 작성간 게시물 등의 정보를 가져옴
        List<Community> userPosts = userService.getUserPosts(user.getLoginId());
        model.addAttribute("userPosts", userPosts);

        return "profile_form";
    }

    @GetMapping("/modifyProfile")
    public String modifyProfile(Model model, Principal principal){
        // 현재 로그인한 사용자의 아이디를 가져옴
        String userId = principal.getName();

        // userService를 사용하여 사용자 정보를 가져옴
        User user = userService.getUser(userId);
        String nickname = user.getNickname();
        model.addAttribute("user", user);
        model.addAttribute("nickname",nickname);

        return "modifyProfile_form";
    }

    @PostMapping("/modifyProfile")
    public String changeProfile(Model model, Principal principal,
                                @RequestParam("nickname")String nickname,
                                @RequestParam("email")String email,
                                @RequestParam("file") MultipartFile file,
                                @RequestParam(value = "url",defaultValue = "")String url) {

        // Principal 객체가 null인지 확인
        if(principal == null) {
            // 처리할 방법을 결정하거나 예외를 처리합니다.
            return "redirect:/login"; // 로그인 페이지로 리다이렉트 또는 오류 처리
        }

        // 현재 로그인한 사용자의 아이디를 가져옴
        String userId = principal.getName();
        User user = userService.getUser(userId);
        String userImage = user.getUrl();

        // 파일이 업로드된 경우에만 처리
        if (!file.isEmpty()) {
            if (file.getContentType() != null && file.getContentType().startsWith("image")) {
                // 이미지를 서버에 저장하고 url을 얻음
                String tempUrl = userService.temp_url(file);
                if (tempUrl != null) {
                    url = tempUrl;
                    // 사용자 정보에 프로필 사진 URL 저장
                    userService.saveimage(user, url);
                } else {
                    model.addAttribute("error", "파일 업로드에 실패했습니다.");
                    return "modifyProfile_form";
                }
                try{
                    userService.updateProfile(user, nickname, email,url);
                    return "redirect:/user/profile";
                } catch (CustomException e){
                    e.printStackTrace();
                    model.addAttribute("error", "프로필 업데이트에 실패했습니다.");
                    return "modifyProfile_form";
                }
            }
        }else{
            try{
                userService.updateProfile(user, nickname, email,userImage);
                return "redirect:/user/profile";
            } catch (CustomException e){
                e.printStackTrace();
                model.addAttribute("error", "프로필 업데이트에 실패했습니다.");
                return "modifyProfile_form";
            }
        }
        // URL이 비어 있지 않으면 저장
        if (!url.isBlank()) {
            userService.saveimage(user, url);
        }

        // 사용자 정보 업데이트
        return "redirect:/user/profile";
    }

    @PostMapping("/imageform")
    public String imageform(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @RequestParam("text") String text) {
        String url = null;

        try {
            // 파일 타입이 이미지인지 확인
            if (file != null && file.getContentType() != null && file.getContentType().contains("image")) {
                // userService.temp_save(file) 메서드를 호출하여 URL을 얻음
                url = userService.temp_save(file);

                // URL이 null이면 예외를 발생시켜 처리하도록 함
                if (url == null) {
                    throw new IllegalStateException("Failed to save image, URL is null.");
                }
            } else {
                throw new IllegalArgumentException("File is not an image.");
            }
        } catch (Exception e) {
            // 예외가 발생하면 기본값을 설정하거나 에러 메시지를 추가
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            // 기본 URL 설정 (예: placeholder 이미지 URL)
            return "redirect:/user/profile";
        }

        // URL 및 텍스트를 리다이렉트 속성에 추가
        redirectAttributes.addFlashAttribute("url", url);
        redirectAttributes.addFlashAttribute("text", text);

        return "redirect:/user/profile";
    }
}