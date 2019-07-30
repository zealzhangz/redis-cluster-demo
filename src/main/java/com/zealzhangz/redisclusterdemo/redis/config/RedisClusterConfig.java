package com.zealzhangz.redisclusterdemo.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by zealzhangz.<br/>
 * @version Version: 0.0.1
 * @date DateTime: 2019/07/30 16:04:00<br/>
 */
@Configuration
@EnableCaching
public class RedisClusterConfig extends CachingConfigurerSupport {
    @Value("${spring.redis.prefix}")
    private String prefix;
    @Value("${spring.redis.cluster.expireSeconds}")
    private Integer expireSeconds;


    /**
     * Initial RedisTemplate
     * Spring using StringRedisTemplate package RedisTemplate object for data CRUD，support all redis native APIs
     *
     * @param clusterNodes
     * @param timeout
     * @param redirects
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Bean(name = "redisTemplate")
    public RedisTemplate redisTemplate(@Value("${spring.redis.cluster.nodes}") String clusterNodes,
                                       @Value("${spring.redis.cluster.timeout}") Long timeout,
                                       @Value("${spring.redis.cluster.maxRedirects}") int redirects) {

        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory(getClusterConfiguration(clusterNodes, timeout, redirects)));
        setSerializer(template);

        return template;
    }

    /**
     * Configure Redis Cluster
     *
     * @param clusterNodes
     * @param timeout
     * @param redirects
     * @return
     */
    public RedisClusterConfiguration getClusterConfiguration(String clusterNodes, Long timeout, int redirects) {
        Map<String, Object> source = new HashMap<>(3);
        source.put("spring.redis.cluster.nodes", clusterNodes);
        source.put("spring.redis.cluster.timeout", timeout);
        source.put("spring.redis.cluster.max-redirects", redirects);
        return new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));
    }

    /**
     * Configure connection pool
     *
     * @param configuration
     * @return
     */
    private RedisConnectionFactory connectionFactory(RedisClusterConfiguration configuration) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    /**
     * Serializer
     *
     * @param template
     */
    private void setSerializer(StringRedisTemplate template) {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
    }

    /**
     * Cache management
     *
     * @param redisTemplate
     * @return
     */
    @Primary
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // Number of seconds before expiration. Defaults to unlimited (0)
        //Default expiration time 3 Days
        cacheManager.setDefaultExpiration(expireSeconds);
        //Cache prefix
        cacheManager.setUsePrefix(true);
        cacheManager.setCachePrefix(s -> prefix.getBytes());
        return cacheManager;
    }

    /**
     * AD cache management
     * Never expiration
     * @param redisTemplate
     * @return
     */
    @Bean("adCacheManager")
    public CacheManager adCacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // Number of seconds before expiration. Defaults to unlimited (0)
        cacheManager.setDefaultExpiration(0);
        //Cache prefix
        cacheManager.setUsePrefix(true);
        cacheManager.setCachePrefix(s -> prefix.getBytes());
        return cacheManager;
    }

    /**
     * The Strategy of Producing Key
     *
     * @return
     */
    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return (Object target, Method method, Object... params) ->{
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

}
