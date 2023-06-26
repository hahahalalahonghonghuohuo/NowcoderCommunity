package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "Here we can gamble and do sth related to sex. Even drug and moneyWashing is allowed.But all of these are illegal!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "Here we can ☆gam☆ble☆,and do sth related to ☆se☆x☆,Even ☆d☆rug☆,and ☆money☆Washing☆ is allowed.But all of these are illegal!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }

}
