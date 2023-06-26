package com.nowcoder.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.*;

/**
 * @Author: yaoyp
 * @Date: 2023/6/2 - 06 - 02 - 18:21
 * @Description: com.nowcoder.community.config
 * @version: 1.0
 */

@Configuration
public class WkConfig {

    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String WkImageStorage;

    @PostConstruct
    public void init() {
        // 创建 WK 图片目录
        File file = new File(WkImageStorage);
        if (!file.exists()) {
            file.mkdir();
            // 方便今后通过日志追查
            logger.info("创建WK图片目录：" + WkImageStorage);
        }
    }

}
