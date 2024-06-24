package com.shier.controller;

import com.shier.common.BaseResponse;
import com.shier.common.ErrorCode;
import com.shier.common.ResultUtils;
import com.shier.exception.BusinessException;
import com.shier.manager.RedisLimiterManager;
import com.shier.model.domain.User;
import com.shier.model.request.AddCommentRequest;
import com.shier.model.vo.BlogCommentsVO;
import com.shier.service.BlogCommentsService;
import com.shier.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 博文评论控制器
 *
 * @author Shier
 * @date 2023/06/11
 */
@RestController
@RequestMapping("/comments")
@Api(tags = "博文评论管理模块")
@CrossOrigin(originPatterns = {"http://localhost:5173", "http://47.121.118.209","http://localhost:5174"}, allowCredentials = "true")
public class BlogCommentsController {
    /**
     * 博客评论服务
     */
    @Resource
    private BlogCommentsService blogCommentsService;

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    /**
     * 添加评论
     *
     * @param addCommentRequest 添加评论请求
     * @param request           请求
     * @return {@link BaseResponse}<{@link String}>
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加评论")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "addCommentRequest", value = "博文评论添加请求"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<String> addComment(@RequestBody AddCommentRequest addCommentRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 限流
        boolean doRateLimit = redisLimiterManager.doRateLimit(loginUser.getId().toString());
        if (!doRateLimit) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
        if (addCommentRequest.getBlogId() == null || StringUtils.isBlank(addCommentRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        blogCommentsService.addComment(addCommentRequest, loginUser.getId());
        return ResultUtils.success("添加成功");
    }

    /**
     * 博客评论列表
     *
     * @param blogId  博文id
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link BlogCommentsVO}>>
     */
    @GetMapping
    @ApiOperation(value = "根据id获取博文评论")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "blogId", value = "博文id"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<BlogCommentsVO>> listBlogComments(long blogId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<BlogCommentsVO> blogCommentsVOList = blogCommentsService.listComments(blogId, loginUser.getId());
        return ResultUtils.success(blogCommentsVOList);
    }

    /**
     * 喜欢评论
     *
     * @param id      id
     * @param request 请求
     * @return {@link BaseResponse}<{@link String}>
     */
    @PutMapping("/like/{id}")
    @ApiOperation(value = "点赞博文评论")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "id", value = "博文评论id"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<String> likeComment(@PathVariable Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        blogCommentsService.likeComment(id, loginUser.getId());
        return ResultUtils.success("ok");
    }

    /**
     * 通过id获取评论
     *
     * @param id      id
     * @param request 请求
     * @return {@link BaseResponse}<{@link BlogCommentsVO}>
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取评论")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "id", value = "博文评论id"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<BlogCommentsVO> getCommentById(@PathVariable Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        BlogCommentsVO commentsVO = blogCommentsService.getComment(id, loginUser.getId());
        return ResultUtils.success(commentsVO);
    }

    /**
     * 删除博客评论
     *
     * @param id      id
     * @param request 请求
     * @return {@link BaseResponse}<{@link String}>
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除评论")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "id", value = "博文评论id"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<String> deleteBlogComment(@PathVariable Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean isAdmin = userService.isAdmin(loginUser);
        blogCommentsService.deleteComment(id, loginUser.getId(), isAdmin);
        return ResultUtils.success("ok");
    }

    /**
     * 获取我的评论
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link BlogCommentsVO}>>
     */
    @GetMapping("/list/my")
    @ApiOperation(value = "获取我的评论")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<BlogCommentsVO>> listMyBlogComments(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<BlogCommentsVO> commentsVOList = blogCommentsService.listMyComments(loginUser.getId());
        return ResultUtils.success(commentsVOList);
    }
}
