package com.nowcoder.community.actuator;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author: yaoyp
 * @Date: 2023/6/4 - 06 - 04 - 17:08
 * @Description: com.nowcoder.community.actuator
 * @version: 1.0
 */

@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    // 调用端点时尝试获取连接
    // 首先注入连接池
    @Autowired
    private DataSource dataSource;

    // 此注解表示该方法是一个 GET 请求
    // @WriteOperation 表示是 POST 请求
    @ReadOperation
    public String checkConnection() {
        try(
                // 小括号中初始化的资源在编译的时候会自动加上 finalize? ，这样就不用手动关闭了
                Connection conn = dataSource.getConnection();
                ) {
            return CommunityUtil.getJSONString(0, "获取连接成功！");
        } catch (SQLException e) {
            logger.error("获取连接失败：" + e.getMessage());
            return CommunityUtil.getJSONString(1, "获取连接失败！");
        }
    }




}
