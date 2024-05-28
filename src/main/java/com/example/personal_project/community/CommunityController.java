package com.example.personal_project.community;

import com.example.personal_project.comment.CommentForm;
import com.example.personal_project.user.User;
import com.example.personal_project.user.UserDetail;
import com.example.personal_project.user.UserService;
import lombok.RequiredArgsConstructor;
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

     //모든 게시물 조회
    @GetMapping("/list")
    public String list(Model model, CommentForm commentForm) {
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

    // 게시물 작성 페이지로 이동
    @GetMapping("/add")
    public String add(){
        return "communityPost_form";
    }
    // 게시물 등록
    @PostMapping("/add")
    public String add(@RequestParam("postImage") MultipartFile postImage,
                      @RequestParam("postDescription") String postDescription,
                      RedirectAttributes redirectAttributes,
                      @AuthenticationPrincipal UserDetail userDetail, Principal principal) {

        // principal 객체를 통해 현재 사용자의 이름을 가져옵니다.
        String username = principal.getName();

        // 사용자 이름을 이용하여 사용자 정보를 조회합니다.
        User user =  userService.getUser(username);

        // 사용자 정보가 null이 아닌지 확인하고, null이 아닌 경우에만 게시물을 저장합니다.
        if(user != null) {
            communityService.saveCommunity(postImage, postDescription, user);

            // 등록 성공 메시지 전달
            redirectAttributes.addFlashAttribute("successMessage", "게시물이 성공적으로 등록되었습니다.");
        } else {
            // 사용자 정보가 없는 경우, 적절한 예외 처리를 수행합니다.
            // 여기서는 간단히 오류 메시지를 설정하고 리다이렉트합니다.
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
        }
        return "redirect:/community/list"; // 리스트 페이지로 리다이렉
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
