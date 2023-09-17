package com.shier.service;

import com.shier.model.domain.BlogComments;
import com.shier.model.request.AddCommentRequest;
import com.shier.model.vo.BlogCommentsVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Shier
* @description 针对表【blog_comments】的数据库操作Service
* @createDate 2023-06-08 12:44:45
*/
public interface BlogCommentsService extends IService<BlogComments> {

    void addComment(AddCommentRequest addCommentRequest, Long userId);

    List<BlogCommentsVO> listComments(long blogId, long userId);

    BlogCommentsVO getComment(long commentId, Long userId);

    void likeComment(long commentId, Long userId);

    void deleteComment(Long id, Long userId, boolean isAdmin);

    List<BlogCommentsVO> listMyComments(Long id);
}
