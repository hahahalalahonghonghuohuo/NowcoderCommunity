package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成一个随机字符串(激活码，文件的随机名字)
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5 加密
    // 只能加密，不能解密
    // hello -> abc123def456
    // 每次加密都是这个值，在这个基础上加盐，防止字典攻击
    // hello + 3e4a8 -> abc123def456abc
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    // 通常会给浏览器返回编号、提示信息等
    // 后续重载该方法，从而便于调用
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        // 将传入的参数装入到对象中
        json.put("code", code);
        json.put("msg", msg);
        // map 需要打散并单独加入
        if (map != null) {
            // 遍历 map  的三种方式，key value key-value,这里是key
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    // 此工具类较简单, 用 main 方法测试
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        // {"msg":"ok","code":0,"name":"zhangsan","age":25}
        // 方便了前后端的交互
        System.out.println(getJSONString(0, "ok", map));
    }
}
