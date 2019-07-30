package com.zealzhangz.redisclusterdemo;

import com.zealzhangz.redisclusterdemo.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 展示了Redis Cluster 读写缓存的多种方式供参考
 * @author Created by zealzhangz.<br/>
 * @version Version: 0.0.1
 * @date DateTime: 2019/07/30 16:44:00<br/>
 */
@Service
@CacheConfig(cacheNames = "cacheTest")
public class TestService {

    @Autowired
    private RedisService redisService;

    /**
     * 自定义cache，可设置过期时间,其实用redisTemplate也可以实现而且更加优雅，这里多一种使用方法，算是一种特例使用
     */
    @Autowired
    @Qualifier("adCacheManager")
    private CacheManager adCacheManager;


    public Map<String, String> findUserIdName(String sub) {
        String cacheKey =  "userSubMap:" + sub;
        if (redisService.exists(cacheKey)) {
            return (HashMap<String, String>) redisService.get(cacheKey);
        } else {
            HashMap<String, String> map = new HashMap<>(1);
            map.put("test","test123");
            redisService.save(cacheKey, map);
            return map;
        }
    }

    public AdBase getAdBaseInfo(){
        AdBase adBase;
        Cache.ValueWrapper adBaseCache =  adCacheManager.getCache("ad").get("ad:adBaseInfo");
        if(null == adBaseCache){
            adBase = getMyADBaseInfo();
            adCacheManager.getCache("ad").put("ad:adBaseInfo",adBase);
        } else {
            adBase = (AdBase)adBaseCache.get();
        }
        return adBase;
    }

    public AdBase updateAdBaseInfo(AdBase adBase){
        adCacheManager.getCache("ad").put("ad:adBaseInfo",adBase);
        return adBase;
    }

    private AdBase getMyADBaseInfo(){
        return new AdBase("test","test123456");
    }

    @CacheEvict(key = "'username:' + #p0")
    public void clearCacheForSystemName(String userID) {
        // only clear cache
        System.out.println("Clear success!");
    }

    @Cacheable(key = "'username:' + #p0")
    public String findUsername(String userID) {
        return "testUsername";
    }

    public void testSaveEntry(String key,String value,long expiration){
        redisService.save(key,value,expiration);
    }
}
