package com.cheney.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public DefaultRedisScript<Long> script() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        //放在和application.yml 同层目录下
        redisScript.setLocation(new ClassPathResource("stock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置序列化器
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(new ObjectMapper());
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);

        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        return redisTemplate;
    }


}
