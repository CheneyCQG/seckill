package com.cheney.seckill;

import com.cheney.seckill.pojo.User;
import com.cheney.seckill.service.IUserService;
import com.cheney.seckill.utils.JsonUtil;
import com.cheney.seckill.utils.UUIDUtil;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillApplicationTests {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    void insertUsers() {
        User user = new User();
        for (int i = 0; i < 10000; i++) {
            user.setId(13838385438L+i);
            user.setNickname("jack"+i);
            user.setPassword("8fcfc1309625f26ff2b57a9b3cc5128d");
            user.setSalt("1008610086");
            iUserService.save(user);
        }

    }
    @Test
    void tokenToRedis() throws IOException {
        List<User> list = iUserService.list();
        BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt"));
        for (User user : list) {
            String s = JsonUtil.object2JsonStr(user);
            String uuid = UUIDUtil.uuid();
            bw.write(user.getId()+","+uuid);
            bw.newLine();
            redisTemplate.opsForValue().set("user:uuid:"+uuid,s);
        }
        bw.close();

    }


    @Test
    void testLock01(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ValueOperations valueOperations = redisTemplate.opsForValue();
                    Boolean isLock = valueOperations.setIfAbsent("k1", "v1",100, TimeUnit.SECONDS);
                    if (isLock){
                        valueOperations.set("name","xxxx");
                        String name = (String) valueOperations.get("name");
                        System.out.println(name);
                        int j = 1 / 0;
                        redisTemplate.delete("k1");
                    }else {
                        System.out.println("有线程在使用，请稍后");
                    }
                }
            }).start();

        }

    }
    @Autowired
    private DefaultRedisScript script;
    @Test
    public void testLock03(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        //给锁添加一个过期时间，防止应用在运行过程中抛出异常导致锁无法及时得到释放
        Boolean isLock = valueOperations.setIfAbsent("k1",value,5, TimeUnit.SECONDS);
        //没人占位
        if (isLock){
            valueOperations.set("name","xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println(name);
            System.out.println(valueOperations.get("k1"));
            //释放锁
            Boolean result = (Boolean) redisTemplate.execute(script,
                    Collections.singletonList("k1"), value);
            System.out.println(result);
        }else {
            //有人占位，停止/暂缓 操作
            System.out.println("有线程在使用，请稍后");
        }
    }

}
