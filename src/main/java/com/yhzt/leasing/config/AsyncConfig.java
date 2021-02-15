package com.yhzt.leasing.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig {
  private static final int CORE_POOL_SIZE = 6;
  private static final int MAX_POOL_SIZE = 10;
  private static final int QUEUE_CAPACITY = 400;

  /**
   * SpringBoot会优先使用名称为"taskExecutor"的线程池。 如果没有找到，才会使用其他类型为TaskExecutor或其子类的线程池。
   * 
   * @return
   */
  @Bean
  public Executor taskExecutor() {

    log.info("------------------启动多线程------------------");

    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    // 配置核心线程数
    threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
    // 配置最大线程数
    threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
    // 配置队列大小
    threadPoolTaskExecutor.setQueueCapacity(QUEUE_CAPACITY);
    // 设置线程活跃时间（秒）
    threadPoolTaskExecutor.setKeepAliveSeconds(60);
    // 配置线程池中的线程的名称前缀
    threadPoolTaskExecutor.setThreadNamePrefix("async-");
    // 设置拒绝策略
    // rejection-policy：当pool已经达到max size的时候，如何处理新任务
    // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
    threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    // 等待所有任务结束后再关闭线程池
    threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    // 执行初始化
    threadPoolTaskExecutor.initialize();
    return threadPoolTaskExecutor;
  }
}