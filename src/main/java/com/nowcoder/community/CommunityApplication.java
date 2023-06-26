package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

	// 该注解用于管理 Bean 的生命周期，主要是用来管理 Bean 的初始化方法
	@PostConstruct
	public void init() {
		// 解决 netty 启动冲突问题的
		// see Netty4Utils.setAvailableProcessors()
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}



}
