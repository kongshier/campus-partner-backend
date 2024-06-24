package com.shier.service.impl;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shier.common.ErrorCode;
import com.shier.constants.UserConstants;
import com.shier.exception.BusinessException;
import com.shier.mapper.UserMapper;
import com.shier.model.domain.Follow;
import com.shier.model.domain.User;
import com.shier.model.request.UserRegisterRequest;
import com.shier.model.request.UserUpdateRequest;
import com.shier.model.vo.UserVO;
import com.shier.service.FollowService;
import com.shier.service.UserService;
import com.shier.utils.AlgorithmUtil;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.shier.constants.RedisConstants.*;
import static com.shier.constants.SystemConstants.*;
import static com.shier.constants.UserConstants.*;

/**
 * @author Shier
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-05-07 19:56:01
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FollowService followService;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "Shier";

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public long userRegister(String phone, String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(phone, userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getPhone, phone);
        long phoneNum = this.count(userLambdaQueryWrapper);
        if (phoneNum >= 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "该手机号已注册");
        }
        // String key = REGISTER_CODE_KEY + phone;
        // Boolean hasKey = stringRedisTemplate.hasKey(key);
        // if (Boolean.FALSE.equals(hasKey)) {
        //     throw new BusinessException(ErrorCode.FORBIDDEN, "请先获取验证码");
        // }
        // String correctCode = stringRedisTemplate.opsForValue().get(key);
        // if (correctCode == null) {
        //     throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        // }
        // if (!correctCode.equals(code)) {
        //     throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        // }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher accountMatcher = Pattern.compile(validPattern).matcher(userAccount);
        if (accountMatcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }
        // 合法手机号码
        String validPhone = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
        Matcher phoneMatcher = Pattern.compile(validPhone).matcher(phone);
        if (!phoneMatcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入合法的手机号码");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        Random random = new Random();
        user.setAvatarUrl(avatarUrls[random.nextInt(avatarUrls.length)]);
        user.setPhone(phone);
        user.setUsername(userAccount);
        user.setUserAccount(userAccount);
        user.setPassword(encryptPassword);
        ArrayList<String> tag = new ArrayList<>();
        Gson gson = new Gson();
        String jsonTag = gson.toJson(tag);
        user.setTags(jsonTag);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // stringRedisTemplate.delete(key);
        return user.getId();
    }

    @Override
    public String userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入内容不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能小于8位");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserAccount, userAccount);
        User userInDatabase = this.getOne(userLambdaQueryWrapper);
        if (userInDatabase == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        if (!userInDatabase.getPassword().equals(encryptPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(userInDatabase);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        request.getSession().setMaxInactiveInterval(900);
        String token = UUID.randomUUID().toString(true);
        Gson gson = new Gson();
        String userStr = gson.toJson(safetyUser);
        stringRedisTemplate.opsForValue().set(LOGIN_USER_KEY + token, userStr);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, Duration.ofMinutes(15));
        return token;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setRole(originUser.getRole());
        safetyUser.setStatus(originUser.getStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setProfile(originUser.getProfile());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户（内存过滤）
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Override
    public Page<User> searchUsersByTags(List<String> tagNameList, long currentPage) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        for (String tagName : tagNameList) {
            userLambdaQueryWrapper = userLambdaQueryWrapper.or().like(Strings.isNotEmpty(tagName), User::getTags, tagName);
        }
        return page(new Page<>(currentPage, PAGE_SIZE), userLambdaQueryWrapper);
    }

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getRole() == UserConstants.ADMIN_ROLE;
    }

    @Override
    public boolean updateUser(User user, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        user.setId(loginUser.getId());
        if (!(isAdmin(loginUser) || loginUser.getId().equals(user.getId()))) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return updateById(user);
    }

    @Override
    public Page<UserVO> userPage(long currentPage) {
        Page<User> page = this.page(new Page<>(currentPage, PAGE_SIZE));
        Page<UserVO> userVOPage = new Page<>();
        BeanUtils.copyProperties(page, userVOPage);
        return userVOPage;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return null;
        }
        String userStr = stringRedisTemplate.opsForValue().get(LOGIN_USER_KEY + token);
        if (StrUtil.isBlank(userStr)) {
            return null;
        }
        Gson gson = new Gson();
        User user = gson.fromJson(userStr, User.class);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        request.getSession().setMaxInactiveInterval(900);
        return user;
    }

    @Override
    public Boolean isLogin(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            return false;
        }
        return true;
    }

//    @Override
//    public List<User> matchUsers(long num, User loginUser) {
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("id", "tags");
//        queryWrapper.isNotNull("tags");
//        List<User> userList = this.list(queryWrapper);
//        String tags = loginUser.getTags();
//        Gson gson = new Gson();
//        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
//        }.getType());
//        // 用户列表的下标 => 相似度
//        List<Pair<User, Long>> list = new ArrayList<>();
//        // 依次计算所有用户和当前用户的相似度
//        for (int i = 0; i < userList.size(); i++) {
//            User user = userList.get(i);
//            String userTags = user.getTags();
//            // 无标签或者为当前用户自己
//            if (StringUtils.isBlank(userTags) || Objects.equals(user.getId(), loginUser.getId())) {
//                continue;
//            }
//            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
//            }.getType());
//            // 计算分数
//            long distance = AlgorithmUtil.minDistance(tagList, userTagList);
//            list.add(new Pair<>(user, distance));
//        }
//        // 按编辑距离由小到大排序
//        List<Pair<User, Long>> topUserPairList = list.stream()
//                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
//                .limit(num)
//                .collect(Collectors.toList());
//        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
//        String idStr = StringUtils.join(userIdList, ",");
//        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
//        userQueryWrapper.in("id", userIdList).last("ORDER BY FIELD(id," + idStr + ")");
//        return this.list(userQueryWrapper)
//                .stream()
//                .map(this::getSafetyUser)
//                .collect(Collectors.toList());
//    }

    @Override
    public Page<UserVO> matchUser(long currentPage, User loginUser) {
        String tags = loginUser.getTags();
        if (tags == null) {
            return this.userPage(currentPage);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        List<User> userList = this.list(queryWrapper);
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || Objects.equals(user.getId(), loginUser.getId())) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtil.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .collect(Collectors.toList());
        // 截取currentPage所需的List
        ArrayList<Pair<User, Long>> finalUserPairList = new ArrayList<>();
        int begin = (int) ((currentPage - 1) * PAGE_SIZE);
        int end = (int) (((currentPage - 1) * PAGE_SIZE) + PAGE_SIZE) - 1;
        if (topUserPairList.size() < end) {
            // 剩余数量
            int temp = (int) (topUserPairList.size() - begin);
            if (temp <= 0) {
                return new Page<>();
            }
            for (int i = begin; i <= begin + temp - 1; i++) {
                finalUserPairList.add(topUserPairList.get(i));
            }
        } else {
            for (int i = begin; i < end; i++) {
                finalUserPairList.add(topUserPairList.get(i));
            }
        }
        // 获取排列后的UserId
        List<Long> userIdList = finalUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        String idStr = StringUtils.join(userIdList, ",");
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList).last("ORDER BY FIELD(id," + idStr + ")");
        List<UserVO> userVOList = this.list(userQueryWrapper)
                .stream()
                .map((user) -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(user, userVO);
                    LambdaQueryWrapper<Follow> followLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    followLambdaQueryWrapper.eq(Follow::getUserId, loginUser.getId()).eq(Follow::getFollowUserId, userVO.getId());
                    long count = followService.count(followLambdaQueryWrapper);
                    userVO.setIsFollow(count > 0);
                    return userVO;
                })
                .collect(Collectors.toList());
        Page<UserVO> userVOPage = new Page<>();
        userVOPage.setRecords(userVOList);
        userVOPage.setCurrent(currentPage);
        userVOPage.setSize(userVOList.size());
        userVOPage.setTotal(userVOList.size());
        return userVOPage;
    }

    @Override
    public UserVO getUserById(Long userId, Long loginUserId) {
        User user = this.getById(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        LambdaQueryWrapper<Follow> followLambdaQueryWrapper = new LambdaQueryWrapper<>();
        followLambdaQueryWrapper.eq(Follow::getUserId, loginUserId).eq(Follow::getFollowUserId, userId);
        long count = followService.count(followLambdaQueryWrapper);
        userVO.setIsFollow(count > 0);
        return userVO;
    }

    @Override
    public List<String> getUserTags(Long id) {
        User user = this.getById(id);
        String userTags = user.getTags();
        Gson gson = new Gson();
        return gson.fromJson(userTags, new TypeToken<List<String>>() {
        }.getType());
    }

    @Override
    public void updateTags(List<String> tags, Long userId) {
        User user = new User();
        Gson gson = new Gson();
        String tagsJson = gson.toJson(tags);
        user.setId(userId);
        user.setTags(tagsJson);
        this.updateById(user);
    }

    @Override
    public void updateUserWithCode(UserUpdateRequest updateRequest, Long userId) {
        String key;
        boolean isPhone = false;
        if (StringUtils.isNotBlank(updateRequest.getPhone())) {
            key = USER_UPDATE_PHONE_KEY + updateRequest.getPhone();
            isPhone = true;
        } else {
            key = USER_UPDATE_EMAIL_KEY + updateRequest.getEmail();
        }
        String correctCode = stringRedisTemplate.opsForValue().get(key);
        if (correctCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先发送验证码");
        }
        if (!correctCode.equals(updateRequest.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        if (isPhone) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone, updateRequest.getPhone());
            User user = this.getOne(userLambdaQueryWrapper);
            if (user != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该手机号已被绑定");
            }
        } else {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getEmail, updateRequest.getEmail());
            User user = this.getOne(userLambdaQueryWrapper);
            if (user != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已被绑定");
            }
        }
        User user = new User();
        BeanUtils.copyProperties(updateRequest, user);
        user.setId(userId);
        this.updateById(user);
    }

    @Override
    public Page<UserVO> getRandomUser() {
        List<User> randomUser = userMapper.getRandomUser();
        List<UserVO> userVOList = randomUser.stream().map((item) -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(item, userVO);
            return userVO;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(randomUser, userVOList);
        Page<UserVO> userVOPage = new Page<>();
        userVOPage.setRecords(userVOList);
        return userVOPage;
    }

    @Override
    public void updatePassword(String phone, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        String key = USER_FORGET_PASSWORD_KEY + phone;
        // String correctCode = stringRedisTemplate.opsForValue().get(key);
        // if (correctCode == null) {
        //     throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先获取验证码");
        // }
        // if (!correctCode.equals(code)) {
        //     throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        // }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getPhone, phone);
        User user = this.getOne(userLambdaQueryWrapper);
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        user.setPassword(encryptPassword);
        this.updateById(user);
        stringRedisTemplate.delete(key);
    }

    @Override
    public Page<UserVO> preMatchUser(long currentPage, String username, User loginUser) {
        Gson gson = new Gson();
        if (loginUser != null) {
            String key = USER_RECOMMEND_KEY + loginUser.getId() + ":" + currentPage;
            if (StringUtils.isNotBlank(username)) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.like(User::getUsername, username);
                Page<User> userPage = this.page(new Page<>(currentPage, PAGE_SIZE), userLambdaQueryWrapper);
                Page<UserVO> userVOPage = new Page<>();
                BeanUtils.copyProperties(userPage, userVOPage);
                List<UserVO> userVOList = userPage.getRecords().stream().map((user) -> this.getUserById(user.getId(), loginUser.getId())).collect(Collectors.toList());
                userVOPage.setRecords(userVOList);
                return userVOPage;
            }
            if (currentPage <= DEFAULT_CACHE_PAGE) {
                Boolean hasKey = stringRedisTemplate.hasKey(key);
                if (Boolean.TRUE.equals(hasKey)) {
                    String userVOPageStr = stringRedisTemplate.opsForValue().get(key);
                    return gson.fromJson(userVOPageStr, new TypeToken<Page<UserVO>>() {
                    }.getType());
                } else {
                    Page<UserVO> userVOPage = this.matchUser(currentPage, loginUser);
                    String userVOPageStr = gson.toJson(userVOPage);
                    stringRedisTemplate.opsForValue().set(key, userVOPageStr);
                    return userVOPage;
                }
            } else {
                Page<UserVO> userVOPage = this.matchUser(currentPage, loginUser);
                String userVOPageStr = gson.toJson(userVOPage);
                stringRedisTemplate.opsForValue().set(key, userVOPageStr);
                return userVOPage;
            }
        } else {
            if (StringUtils.isNotBlank(username)) {
                throw new BusinessException(ErrorCode.NOT_LOGIN);
            }
            return this.getRandomUser();
        }
    }


    @Deprecated
    private List<User> searchByMemory(List<String> tagNameList) {
        List<User> userList = userMapper.selectList(null);
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            String tags = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tags, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 管理员注册
     *
     * @param userRegisterRequest 用户登记要求
     * @param request             要求
     * @return {@link Long}
     */
    @Override
    public Long adminRegister(UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        Integer role = loginUser.getRole();
        if (!role.equals(ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        String phone = userRegisterRequest.getPhone();
        String account = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getUserPassword();
        checkAccountValid(account);
        checkAccountRepetition(account);
        return insetUser(phone, account, password);
    }


    /**
     * 插图用户
     *
     * @param phone    电话
     * @param account  账户
     * @param password 暗语
     * @return long
     */
    private long insetUser(String phone, String account, String password) {
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 插入数据
        User user = new User();
        Random random = new Random();
        user.setAvatarUrl(avatarUrls[random.nextInt(avatarUrls.length)]);
        user.setPhone(phone);
        user.setUsername(account);
        user.setUserAccount(account);
        user.setPassword(encryptPassword);
        ArrayList<String> tag = new ArrayList<>();
        Gson gson = new Gson();
        String jsonTag = gson.toJson(tag);
        user.setTags(jsonTag);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return user.getId();
    }

    /**
     * 之后插入用户
     *
     * @param key     钥匙
     * @param userId  用户id
     * @param request 要求
     * @return {@link String}
     */
    @Override
    public String afterInsertUser(String key, long userId, HttpServletRequest request) {
        stringRedisTemplate.delete(key);
        User userInDatabase = this.getById(userId);
        User safetyUser = this.getSafetyUser(userInDatabase);
        String token = UUID.randomUUID().toString(true);
        Gson gson = new Gson();
        String userStr = gson.toJson(safetyUser);
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        request.getSession().setMaxInactiveInterval(MAXIMUM_LOGIN_IDLE_TIME);
        stringRedisTemplate.opsForValue().set(LOGIN_USER_KEY + token, userStr);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, Duration.ofSeconds(MAXIMUM_LOGIN_IDLE_TIME));
        return token;
    }

    /**
     * 手机号码验证
     *
     * @param account 账户
     */
    private void checkAccountValid(String account) {
        String validPattern = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(account);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确的手机号码");
        }
    }

    /**
     * 账号重复
     *
     * @param account 账户
     */
    private void checkAccountRepetition(String account) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserAccount, account);
        long count = this.count(userLambdaQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "此账号已存在！");
        }
    }

    /**
     * 管理员登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户暗语
     * @param request      要求
     * @return {@link String}
     */
    @Override
    public String adminLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        validateUserRequest(userAccount, userPassword);
        User userInDatabase = getUserInDatabase(userAccount, userPassword);
        if (!userInDatabase.getRole().equals(1)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "非管理员禁止登录");
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(userInDatabase);
        // 4. 记录用户的登录态
        return setUserLoginState(request, safetyUser);
    }


    /**
     * 取得数据库数据
     *
     * @param userAccount  用户账户
     * @param userPassword 用户暗语
     * @return {@link User}
     */
    public User getUserInDatabase(String userAccount, String userPassword) {
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserAccount, userAccount);
        User userInDatabase = this.getOne(userLambdaQueryWrapper);
        if (userInDatabase == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        if (!userInDatabase.getPassword().equals(encryptPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        if (!userInDatabase.getStatus().equals(0)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "该用户已被封禁");
        }
        return userInDatabase;
    }


    /**
     * 改变用户地位
     *
     * @param id id
     */
    @Override
    public void changeUserStatus(Long id) {
        User user = this.getById(id);
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (user.getStatus().equals(0)) {
            // 状态设置为1 封禁
            userLambdaUpdateWrapper.eq(User::getId, id).set(User::getStatus, 1);
        } else {
            userLambdaUpdateWrapper.eq(User::getId, id).set(User::getStatus, 0);
        }
        try {
            this.update(userLambdaUpdateWrapper);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误");
        }
    }

    /**
     * 验证用户请求
     *
     * @param userAccount  用户账户
     * @param userPassword 用户暗语
     */
    public void validateUserRequest(String userAccount, String userPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        if (userAccount.length() < MINIMUM_ACCOUNT_LEN) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号非法");
        }
        if (userPassword.length() < MINIMUM_PASSWORD_LEN) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码非法");
        }
        // 账户不能包含特殊字符
        String validPattern = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号非法");
        }
    }

    /**
     * 设置用户登录状态
     *
     * @param request    要求
     * @param safetyUser 安全用户
     * @return {@link String}
     */
    public String setUserLoginState(HttpServletRequest request, User safetyUser) {
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        request.getSession().setMaxInactiveInterval(MAXIMUM_LOGIN_IDLE_TIME);
        String token = UUID.randomUUID().toString(true);
        Gson gson = new Gson();
        String userStr = gson.toJson(safetyUser);
        stringRedisTemplate.opsForValue().set(LOGIN_USER_KEY + token, userStr);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, Duration.ofSeconds(MAXIMUM_LOGIN_IDLE_TIME));
        return token;
    }

}




