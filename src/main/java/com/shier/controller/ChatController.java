package com.shier.controller;

import com.shier.common.BaseResponse;
import com.shier.common.ErrorCode;
import com.shier.common.ResultUtils;
import com.shier.constants.ChatConstant;
import com.shier.exception.BusinessException;
import com.shier.model.domain.User;
import com.shier.model.request.ChatRequest;
import com.shier.model.vo.ChatMessageVO;
import com.shier.model.vo.PrivateChatVO;
import com.shier.service.ChatService;
import com.shier.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 聊天控制器
 *
 * @author Shier
 * @date 2023/06/19
 */
@RestController
@RequestMapping("/chat")
@Api(tags = "聊天管理模块")
@CrossOrigin(originPatterns = {"http://localhost:5173", "http://47.121.118.209","http://localhost:5174"}, allowCredentials = "true")
public class ChatController {
    /**
     * 聊天服务
     */
    @Resource
    private ChatService chatService;

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 私聊
     *
     * @param chatRequest 聊天请求
     * @param request     请求
     * @return {@link BaseResponse}<{@link List}<{@link ChatMessageVO}>>
     */
    @PostMapping("/privateChat")
    @ApiOperation(value = "获取私聊")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "chatRequest", value = "聊天请求"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<ChatMessageVO>> getPrivateChat(@RequestBody ChatRequest chatRequest, HttpServletRequest request) {
        if (chatRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> privateChat = chatService.getPrivateChat(chatRequest, ChatConstant.PRIVATE_CHAT, loginUser);
        return ResultUtils.success(privateChat);
    }

    /**
     * 团队聊天
     *
     * @param chatRequest 聊天请求
     * @param request     请求
     * @return {@link BaseResponse}<{@link List}<{@link ChatMessageVO}>>
     */
    @PostMapping("/teamChat")
    @ApiOperation(value = "获取队伍聊天")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "chatRequest", value = "聊天请求"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<ChatMessageVO>> getTeamChat(@RequestBody ChatRequest chatRequest, HttpServletRequest request) {
        if (chatRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求有误");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> teamChat = chatService.getTeamChat(chatRequest, ChatConstant.TEAM_CHAT, loginUser);
        return ResultUtils.success(teamChat);
    }

    /**
     * 大厅聊天
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link ChatMessageVO}>>
     */
    @GetMapping("/hallChat")
    @ApiOperation(value = "获取大厅聊天")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<ChatMessageVO>> getHallChat(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> hallChat = chatService.getHallChat(ChatConstant.HALL_CHAT, loginUser);
        return ResultUtils.success(hallChat);
    }


    /**
     * 私聊
     * @param request
     * @return
     */
    @GetMapping("/private/list")
    @ApiOperation(value = "获取私聊列表")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<PrivateChatVO>> getPrivateChatList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<PrivateChatVO> userList = chatService.getPrivateList(loginUser.getId());
        return ResultUtils.success(userList);
    }

    /**
     * 获取私聊未读消息数量（接受消息）
     *
     * @param request 要求
     * @return {@link BaseResponse}<{@link Integer}>
     */
    @GetMapping("/private/num")
    @ApiOperation(value = "获取私聊未读消息数量")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<Integer> getUnreadPrivateNum(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Integer unreadNum = chatService.getUnReadPrivateNum(loginUser.getId());
        return ResultUtils.success(unreadNum);
    }

    /**
     * 私聊已读
     * @param request
     * @param remoteId
     * @return
     */
    @PutMapping("/private/read")
    public BaseResponse<Boolean> readPrivateMessage(HttpServletRequest request, Long remoteId) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Boolean flag = chatService.readPrivateMessage(loginUser.getId(), remoteId);
        return ResultUtils.success(flag);
    }
}