
package cache;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import com.baidu.rigel.biplatform.cache.redis.listener.PrintListener;

public class AppTest {

    
    @Test
    public void testSentinelJedis() {
        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.setMaster("biplatform_master");
        config.sentinel("10.57.204.73", 8379);
        config.sentinel("10.57.204.72", 8379);
        
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        GenericObjectPoolConfig poolConfig1 = new GenericObjectPoolConfig();
        poolConfig1.setMaxTotal(300);

        Set<String> sentinels = new HashSet<>();
        sentinels.add("10.57.204.73:8379");
        sentinels.add("10.57.204.72:8379");
        
        
        JedisSentinelPool sentinelPool = new JedisSentinelPool(config.getMaster().getName(), sentinels, poolConfig1);
        System.out.println("Current master: " + sentinelPool.getCurrentHostMaster().toString());
        
        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.setPoolConfig(poolConfig);
        
        
        Jedis jedis = sentinelPool.getResource();
        System.out.println(jedis.getClient().getHost());
        
//        jedis.auth("biplatform");
        jedis.set("key", "value");
        
        System.out.println(jedis.get("key"));
        sentinelPool.returnResource(jedis);
        
        Thread t = new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                System.out.println("start listen..");
//                System.out.println(jedis.get("key".getBytes()));
                Jedis jedis1 = new Jedis(sentinelPool.getCurrentHostMaster().getHost(),sentinelPool.getCurrentHostMaster().getPort());
                jedis1.auth("biplatform");
                System.out.println("prepare jedis:" + jedis1);
                System.out.println(jedis1.get("key"));
                jedis1.subscribe(new PrintListener(), "channel");
//                jedis1.close();
            }
        });
        
        t.start();
        
//        Jedis jedis2 = sentinelPool.getResource();
//        
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            
//        }
//        System.out.println("start to publish..");
//        jedis2.publish("channel", "testMsg");
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            
//        }
        
        System.out.println("publish end");
//        sentinelPool.returnResource(jedis2);
//        
//        
//        
//        
//        StringRedisTemplate template = new StringRedisTemplate(factory);
//        
//        template.execute(new RedisCallback<String>() {
//
//            @Override
//            public String doInRedis(RedisConnection connection) throws DataAccessException {
//                connection.set(template.getStringSerializer().serialize("key"), template.getStringSerializer().serialize("value"));
//                return null;
//                
//            }
//        });
//        
//        
//        
//        template.execute(new RedisCallback<String>() {
//
//            @Override
//            public String doInRedis(RedisConnection connection) throws DataAccessException {
//                byte[] value = connection.get(template.getStringSerializer().serialize("key"));
//                System.out.println(template.getStringSerializer().deserialize(value));
//                return null;
//                
//            }
//        });
    }
}

