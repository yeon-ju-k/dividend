package com.playstudy.dividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 1. 스레드 풀 생성
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

        // 2. CPU 코어 개수 받아오기 (스레드 풀 사이즈를 설정하기 위한 값)
        int n = Runtime.getRuntime().availableProcessors();

        // 3. 스레드 풀 사이즈 설정
        threadPool.setPoolSize(n);

        // 4. 스레드 풀 initialize
        threadPool.initialize();

        // 5. 스프링의 taskRegistrar 를 사용하여 스케줄러에 특정한 threadPool로 설정하는 코드
        taskRegistrar.setTaskScheduler(threadPool);

    }

}

