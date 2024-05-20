package com.example.personal_project.community;

import com.example.personal_project.user.User;
import com.example.personal_project.user.UserDetail;
import com.example.personal_project.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;
    private final UserService userService;
//
//    @GetMapping("/")
//    public String community(Model model) {
//        // 여기에 메인 페이지에서 필요한 데이터를 가져오는 로직을 추가할 수 있습니다.
//        return "community_form"; // main.html을 리턴합니다.
//    }

     //모든 게시물 조회
    @GetMapping("/list")
    public String list(Model model) {
        List<Community> communities = communityService.getAllPosts(); // 게시물 목록을 가져오는 메서드
        model.addAttribute("communities", communities); // 모델에 게시물 목록 추가
        return "community_form"; // 컨트롤러에서 반환하는 뷰 이름
    }

//    public String list(Model model, @RequestParam(value = "page", defaultValue = "0")int page,
//                @RequestParam(value = "kw", defaultValue = "") String kw) {
//        Page<Community> paging = this.communityService.getList(page, kw);
//        model.addAttribute("paging", paging);
//        model.addAttribute("kw", kw);
//
//        return "community_form";
//    }

    // 작성 페이지로 이동
//    @GetMapping("/community/write")
//    public String showWritePage() {
//        return "communityPost_form";
//    }

    // 게시물 등록
    @GetMapping("/add")
    public String add(){
        return "communityPost_form";
    }
    // 게시물 등록
    @PostMapping("/add")
    public String addCommunity(@RequestParam("postImage") MultipartFile postImage,
                               @RequestParam("postDescription") String postDescription,
                               RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetail userDetail) {
        // 작성 유저 찾기
        User user =  userService.getUser(userDetail.getUsername());
        // 이미지 업로드 및 게시물 저장
        communityService.saveCommunity(postImage, postDescription,user);

        // 등록 성공 메시지 전달
        redirectAttributes.addFlashAttribute("successMessage", "게시물이 성공적으로 등록되었습니다.");

        return "redirect:/community/list"; // 리스트 페이지로 리다이렉트
    }

    // 게시물 삭제
    @PostMapping("/delete/{id}")
    public String deleteCommunity(@PathVariable int id,
                                  RedirectAttributes redirectAttributes) {
        // 게시물 삭제
        communityService.deleteCommunity(id);

        // 삭제 성공 메시지 전달
        redirectAttributes.addFlashAttribute("successMessage", "게시물이 성공적으로 삭제되었습니다.");

        return "redirect:/community/list"; // 리스트 페이지로 리다이렉트
    }
}
