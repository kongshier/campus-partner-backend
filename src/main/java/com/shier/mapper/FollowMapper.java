package com.shier.mapper;

import com.shier.model.domain.Follow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Shier
* @description 针对表【follow】的数据库操作Mapper
* @createDate 2023-06-11 13:02:31
* @Entity com.shier.model.domain.Follow
*/
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

}




