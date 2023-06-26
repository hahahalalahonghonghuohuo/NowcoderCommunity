package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @Author: yaoyp
 * @Date: 2023/6/2 - 06 - 02 - 10:22
 * @Description: com.nowcoder.community.config
 * @version: 1.0
 */
// 配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {

    // 该配置类今后还可以再添加其他内容

    // FactoryBean 可简化 Bean 的实例化过程:
    // 1. 通过 FactoryBean 封装 Bean 的实例化过程
    // 2. 将 FactoryBean 装配到 Spring 容器中
    // 3. 将 FactoryBean 注入给其他的 Bean
    // 4. 该 Bean 得到的是 FactoryBean 所管理的对象实例

    // 配置 JobDetail
    // @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        // 声明该任务是长久地保存
        factoryBean.setDurability(true);
        // 任务是可恢复的
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 配置 Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
    // @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    // 刷新帖子分数的任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        // 声明该任务是长久地保存
        factoryBean.setDurability(true);
        // 任务是可恢复的
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}
