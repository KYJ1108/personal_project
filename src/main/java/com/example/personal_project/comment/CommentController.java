package com.example.personal_project.comment;

import com.example.personal_project.community.Community;
import com.example.personal_project.community.CommunityService;
import com.example.personal_project.user.User;
import com.example.personal_project.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/comment")
public class CommentController {
    private CommentService commentService;
    private CommunityService communityService;
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComment(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm,
                               BindingResult bindingResult, Principal principal) {
        Community community = this.communityService.getCommunity(id);
        User user = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", community);
            return "question_detail";
        }
        Comment comment = this.commentService.create(community, commentForm.getContent(), user);
        return String.format("redirect:/community/detail/%s#comment_%s", comment.getCommunity().getId(),comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyComment(CommentForm answerForm, @PathVariable("id") Integer id, Principal principal){
        Comment comment = this.commentService.getComment(id);
        if(!comment.getUser().getLoginId().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정 권한이 없습니다.");
        }
        answerForm.setContent(comment.getContent());
        return String.format("redirect:/community/detail/%s#comment_%s", comment.getCommunity().getId(),comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(@PathVariable("id") Integer id, Principal principal){
        Comment comment = this.commentService.getComment(id);
        if(!comment.getUser().getLoginId().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/community/detail/%s#comment_%s",comment.getCommunity().getId(),comment.getId());
    }

//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/vote/{id}")
//    public String commentVote(Principal principal, @PathVariable("id") Integer id){
//        Comment comment = this.commentService.getComment(id);
//        User user = this.userService.getUser(principal.getName());
//        this.commentService.vote(comment,user);
//        return String.format("redirect:/community/detail/%s",comment.getCommunity().getId());
//    }

//    @PostMapping("/add")
//    @ResponseBody
//    public Map<String, Object> addComment(@RequestParam int loginId, @RequestParam String text, @AuthenticationPrincipal UserDetails userDetails){
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Community community = communityService.getCommunity(loginId);
//            User user = userService.findByLoginId(userDetails.getUsername());
//
//            Comment comment = new Comment();
//            comment.setCommunity(community);
//            comment.setUser(user);
//            comment.setText(text);
//            comment.setCreateDate(LocalDateTime.now());
//
//            commentService.addComment(comment);
//
//            response.put("success", true);
//            response.put("userNickname", user.getNickname());
//        } catch (Exception e) {
//            response.put("success", false);
//        }
//
//        return response;
//    }
}
