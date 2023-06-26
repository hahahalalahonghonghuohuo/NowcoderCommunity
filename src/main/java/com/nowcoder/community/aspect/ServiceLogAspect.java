package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

// 压力测试前，ServiceLogAspect 也要注释掉

/**
 * 在进行压力测试时，为了提高测试效率和准确性，我们会尽可能地减少测试过程中出现的外部因素干扰。
 * 在这种情况下，日志记录等调试信息可能会对测试结果产生影响，
 * 特别是在高并发的情况下，输出大量的日志信息可能会占用大量系统资源，影响测试进程。
 *
 * 因此，在进行压力测试前，通常会对系统中的日志记录等调试代码进行优化，
 * 包括关闭不必要的日志记录、注释掉调试程序或关闭调试模式等等。
 * ServiceLogAspect是一个切面，用于记录服务端接口请求和响应的数据，
 * 如果在压力测试期间保留它，可能会影响测试结果的准确性和性能。
 * 因此，在进行压力测试前，我们建议注释掉ServiceLogAspect这个切面。
 */

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    // 声明切点
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户[1.2.3.4],在[xxx],访问了[com.nowcoder.community.service.xxx()].
        // 如何获取用户 IP ?
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        // 如果对象 attributes 为空，会报空指针异常 NullPointerException
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        // 获取时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));
    }

}
