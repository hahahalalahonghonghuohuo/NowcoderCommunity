package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 起到一个容器的作用，即持有一个用户的信息，用于代替 session 对象。
 */
@Component
public class HostHolder {

    // set() 方法存值，get() 方法取值
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
