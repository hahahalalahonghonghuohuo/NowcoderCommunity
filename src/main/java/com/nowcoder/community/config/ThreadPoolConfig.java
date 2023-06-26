package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: yaoyp
 * @Date: 2023/6/1 - 06 - 01 - 23:43
 * @Description: com.nowcoder.community.config
 * @version: 1.0
 */

@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
