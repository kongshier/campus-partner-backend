package com.shier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author Shier
 */
@SpringBootApplication
@EnableRedisHttpSession
public class ShierPartnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShierPartnerApplication.class, args);
    }

}
