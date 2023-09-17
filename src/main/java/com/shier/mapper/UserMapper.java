package com.shier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shier.model.domain.User;

import java.util.List;

/**
* @author Shier
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-05-07 19:56:01
* @Entity generator.domain.User
*/
public interface UserMapper extends BaseMapper<User> {

    List<User> getRandomUser();
}




