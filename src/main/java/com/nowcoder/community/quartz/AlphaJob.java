package com.nowcoder.community.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @Author: yaoyp
 * @Date: 2023/6/2 - 06 - 02 - 10:20
 * @Description: com.nowcoder.community.quartz
 * @version: 1.0
 */
public class AlphaJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println(Thread.currentThread().getName() + ": execute a quartz job.");
    }
}
