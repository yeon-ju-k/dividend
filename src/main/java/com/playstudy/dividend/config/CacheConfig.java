package com.playstudy.dividend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration  // config 빈으로 사용할 것이기 때문에
public class CacheConfig {

    // application.properties 에서 설정한 Redis 정보 가져오기
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    // # 1. Redis 서버와 연결을 맺을 수 있는 RedisConnectionFactory 생성하기
    @Bean   // - Redis 서버와의 연결을 관리하기 위한 RedisConnectionFactory 빈을 초기화
    public RedisConnectionFactory redisConnectionFactory() {

        // +) Cluster 인스턴스 서버로 생성할 경우
        //RedisClusterConfiguration conf = new RedisClusterConfiguration();

        // 1) Single 인스턴스 서버로 생성하기 위해 -> StandaloneConfiguration 인스턴스를 생성
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();

        // 2) 생성한 인스턴스에 host 와 port 정보 넣기 - 모두 위의 멤버변수에 선언되어있어야함!
        conf.setHostName(this.host);    // host 설정
        conf.setPort(this.port);        // port 설정
        //conf.setPassword(패스워드멤버변수);   // 패스워드 설정

        // 3) 생성 + 설정된 Redis config 정보를
        //      -> LettuceConnectionFactory에 넣어서 인스턴스 생성
        return new LettuceConnectionFactory(conf);
    }


    // # 2. 위의 ConnectionFactory를 캐쉬에 넣어서 사용하기 위한 메소드 ( Cache Manager bean 생성 )
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
                // 직렬화 (Serialization) 하기 - 다른 시스템에서 사용할 수 있는 기본 테이터 타입인 바이트로 변환
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))        // 키값 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));     // value 값 직렬화



        return RedisCacheManager.RedisCacheManagerBuilder
                    .fromConnectionFactory(redisConnectionFactory)  // 위의 1번메소드에서 설정한 redisConnectionFactory값이 적용됨!
                    .cacheDefaults(conf)
                    .build();
    }


}
