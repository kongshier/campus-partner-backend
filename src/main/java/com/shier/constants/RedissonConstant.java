package com.shier.constants;

/**
 * redisson常量
 *
 * @author Shier
 * @date 2023/06/22
 */
public interface RedissonConstant {
    /**
     * 应用锁
     */
    String APPLY_LOCK = "campus:apply:lock:";

    String DISBAND_EXPIRED_TEAM_LOCK = "campus:disbandTeam:lock";
    String USER_RECOMMEND_LOCK = "campus:user:recommend:lock";
}
