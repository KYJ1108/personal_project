package com.example.personal_project.comment;


import com.example.personal_project.DataNotFoundException;
import com.example.personal_project.community.Community;
import com.example.personal_project.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment create (Community community, String content, User user){
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setCommunity(community);
        comment.setUser(user);
        this.commentRepository.save(comment);
        return comment;
    }

    public Comment getComment(Integer id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()){
            return comment.get();
        }else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public void modify(Comment comment, String content){
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }

    public void delete(Comment comment){
        this.commentRepository.delete(comment);
    }

//    public void vote(Comment comment, User user){
//        comment.getVoter().add(user);
//        this.commentRepository.save(comment);
//    }
    // 질문 ID를 기반으로 해당 질문에 대한 답변 목록을 가져오는 메서드 -> 내림차순
    public Page<Comment> getListByQuestionId(int communityId, int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("createDate").descending());
        return this.commentRepository.findByCommunityId(communityId, pageable);
    }
    // 질문 ID를 기반으로 해당 질문에 대한 답변 목록을 가져오는 메서드 -> 추천순

//    public Page<Comment> getListByQuestionIdOrderByRecommendation(int communityId, int page) {
//        Pageable pageable = PageRequest.of(page, 5);
//        return commentRepository.findByCommunityIdOrderedByVoteCount(communityId, pageable);
//    }

    public List<Comment> getAnswersByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

//    public List<Comment> getCommentByCommunity(Community community){
//        return commentRepository.findByCommunity(community);
//    }
//
//    public void addComment(Comment comment){
//        commentRepository.save(comment);
//    }
}
