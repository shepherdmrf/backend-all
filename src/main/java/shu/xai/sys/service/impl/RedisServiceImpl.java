//package shu.xai.sys.service.impl;
//
//
//import shu.xai.sys.service.RedisService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//
//
///**
// * Created by yuziyi on 2021/8/27.
// */
//@Service("RedisService")
//public class RedisServiceImpl implements RedisService {
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//
//    @Override
//    public void test() {
//       // redisTemplate.opsForHash().put("hashMap","hashKey","hashValue");
//        redisTemplate.opsForValue().set("yzy","123456");
//    }
//}
