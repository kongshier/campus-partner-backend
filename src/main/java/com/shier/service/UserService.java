package com.shier.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shier.model.domain.User;
import com.shier.model.request.UserRegisterRequest;
import com.shier.model.request.UserUpdateRequest;
import com.shier.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Shier
 * @description 针对表【user】的数据库操作Service
 * @createDate 2023-05-07 19:56:01
 */
public interface UserService extends IService<User> {
    long userRegister(String phone, String userAccount, String userPassword, String checkPassword);

    String userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User originUser);

    int userLogout(HttpServletRequest request);

    Page<User> searchUsersByTags(List<String> tagNameList, long currentPage);

    boolean isAdmin(User loginUser);

    boolean updateUser(User user, HttpServletRequest request);

    Page<UserVO> userPage(long currentPage);

    User getLoginUser(HttpServletRequest request);

//    List<User> matchUsers(long num, User user);

    Boolean isLogin(HttpServletRequest request);

    Page<UserVO> matchUser(long currentPage, User loginUser);

    UserVO getUserById(Long userId, Long loginUserId);

    List<String> getUserTags(Long id);

    void updateTags(List<String> tags, Long userId);

    void updateUserWithCode(UserUpdateRequest updateRequest, Long userId);

    Page<UserVO> getRandomUser();

    void updatePassword(String phone, String password, String confirmPassword);

    Page<UserVO> preMatchUser(long currentPage, String username, User loginUser);



    /**
     * 之后插入用户
     *
     * @param key     钥匙
     * @param userId  用户id
     * @param request 要求
     * @return {@link String}
     */
    String afterInsertUser(String key, long userId, HttpServletRequest request);


    Long adminRegister(UserRegisterRequest userRegisterRequest, HttpServletRequest request);

    /**
     * 管理员登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户暗语
     * @param request      要求
     * @return {@link String}
     */
    String adminLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 改变用户角色
     *
     * @param id id
     */
    void changeUserStatus(Long id);
}
