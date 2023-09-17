package com.shier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shier.model.domain.UserTeam;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Shier
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2023-05-14 11:45:06
* @Entity com.shier.domain.UserTeam
*/
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}




