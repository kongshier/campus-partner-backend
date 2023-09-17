package com.shier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shier.common.ErrorCode;
import com.shier.mapper.MessageMapper;
import com.shier.model.domain.Message;
import com.shier.model.domain.User;
import com.shier.model.enums.MessageTypeEnum;
import com.shier.model.vo.BlogCommentsVO;
import com.shier.model.vo.BlogVO;
import com.shier.model.vo.MessageVO;
import com.shier.model.vo.UserVO;
import com.shier.service.BlogCommentsService;
import com.shier.service.BlogService;
import com.shier.service.MessageService;
import com.shier.service.UserService;
import com.shier.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.shier.constants.RedisConstants.*;

/**
 * @author Shier
 * @description 针对表【message】的数据库操作Service实现
 * @createDate 2023-06-21 17:39:30
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {

    @Resource
    @Lazy
    private BlogCommentsService blogCommentsService;

    @Resource
    @Lazy
    private BlogService blogService;

    @Resource
    @Lazy
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public long getMessageNum(Long userId) {
        LambdaQueryWrapper<Message> messageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageLambdaQueryWrapper.eq(Message::getToId, userId).eq(Message::getIsRead, 0);
        return this.count(messageLambdaQueryWrapper);
    }

    @Override
    public long getLikeNum(Long userId) {
        String likeNumKey = MESSAGE_LIKE_NUM_KEY + userId;
        Boolean hasLike = stringRedisTemplate.hasKey(likeNumKey);
        if (Boolean.TRUE.equals(hasLike)) {
            String likeNum = stringRedisTemplate.opsForValue().get(likeNumKey);
            assert likeNum != null;
            return Long.parseLong(likeNum);
        } else {
            return 0;
        }
    }

    @Override
    public List<MessageVO> getLike(Long userId) {
        LambdaQueryWrapper<Message> messageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageLambdaQueryWrapper.eq(Message::getToId, userId)
                .and(wp -> wp.eq(Message::getType, 0).or().eq(Message::getType, 1))
                .orderBy(true, false, Message::getCreateTime);
        List<Message> messageList = this.list(messageLambdaQueryWrapper);
        if (messageList.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaUpdateWrapper<Message> messageLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        messageLambdaUpdateWrapper.eq(Message::getToId, userId).eq(Message::getType, 0)
                .or().eq(Message::getType, 1).set(Message::getIsRead, 1);
        this.update(messageLambdaUpdateWrapper);
        String likeNumKey = MESSAGE_LIKE_NUM_KEY + userId;
        Boolean hasLike = stringRedisTemplate.hasKey(likeNumKey);
        if (Boolean.TRUE.equals(hasLike)) {
            stringRedisTemplate.opsForValue().set(likeNumKey, "0");
        }
        return messageList.stream().map((item) -> {
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(item, messageVO);
            User user = userService.getById(messageVO.getFromId());
            if (user == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "发送人不存在");
            }
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            messageVO.setFromUser(userVO);
            if (item.getType() == MessageTypeEnum.BLOG_COMMENT_LIKE.getValue()) {
                BlogCommentsVO commentsVO = blogCommentsService.getComment(Long.parseLong(item.getData()), userId);
                messageVO.setComment(commentsVO);
            }
            if (item.getType() == MessageTypeEnum.BLOG_LIKE.getValue()) {
                BlogVO blogVO = blogService.getBlogById(Long.parseLong(item.getData()), userId);
                messageVO.setBlog(blogVO);
            }
            return messageVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BlogVO> getUserBlog(Long userId) {
        String key = BLOG_FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, System.currentTimeMillis(), 0, 10);
        if (typedTuples == null || typedTuples.size() == 0) {
            return new ArrayList<>();
        }
        ArrayList<BlogVO> blogVOList = new ArrayList<>(typedTuples.size());
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            long blogId = Long.parseLong(Objects.requireNonNull(tuple.getValue()));
            BlogVO blogVO = blogService.getBlogById(blogId, userId);
            blogVOList.add(blogVO);
        }
        String likeNumKey = MESSAGE_BLOG_NUM_KEY + userId;
        Boolean hasKey = stringRedisTemplate.hasKey(likeNumKey);
        if (Boolean.TRUE.equals(hasKey)) {
            stringRedisTemplate.opsForValue().set(likeNumKey, "0");
        }
        return blogVOList;
    }

    @Override
    public Boolean hasNewMessage(Long userId) {
        String likeNumKey = MESSAGE_LIKE_NUM_KEY + userId;
        Boolean hasLike = stringRedisTemplate.hasKey(likeNumKey);
        if (Boolean.TRUE.equals(hasLike)) {
            String likeNum = stringRedisTemplate.opsForValue().get(likeNumKey);
            assert likeNum != null;
            if (Long.parseLong(likeNum) > 0) {
                return true;
            }
        }
        String blogNumKey = MESSAGE_BLOG_NUM_KEY + userId;
        Boolean hasBlog = stringRedisTemplate.hasKey(blogNumKey);
        if (Boolean.TRUE.equals(hasBlog)) {
            String blogNum = stringRedisTemplate.opsForValue().get(blogNumKey);
            assert blogNum != null;
            return Long.parseLong(blogNum) > 0;
        }
        return false;
    }
}




