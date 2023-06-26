package com.nowcoder.community;

import java.io.IOException;

/**
 * @Author: yaoyp
 * @Date: 2023/6/2 - 06 - 02 - 18:08
 * @Description: com.nowcoder.community
 * @version: 1.0
 */
public class WkTests {
    public static void main(String[] args) {
        String cmd = "E:/JAVA/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com E:/JAVA/data/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
