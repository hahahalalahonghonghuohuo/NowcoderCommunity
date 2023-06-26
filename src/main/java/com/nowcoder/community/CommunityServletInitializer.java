package com.nowcoder.community;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @Author: yaoyp
 * @Date: 2023/6/7 - 06 - 07 - 13:30
 * @Description: com.nowcoder.community
 * @version: 1.0
 */
public class CommunityServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CommunityApplication.class);
    }
}
