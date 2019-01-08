package com.fqz.sso.client.utils;

import com.fqz.sso.client.model.SsoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author fuqianzhong
 * @date 19/1/3
 */
public class JedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(JedisUtils.class);

    private static ShardedJedisPool shardedJedisPool;
    private static final ReentrantLock lock = new ReentrantLock();

    private static String serverAddress = "";

    public static void init(String serverAddress){
        JedisUtils.serverAddress = serverAddress;
    }

    private static ShardedJedis getInstance(){
        if(shardedJedisPool == null){
            try {
                lock.lock();
                if(shardedJedisPool == null) {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(100);//最大连接数
                    jedisPoolConfig.setMaxIdle(40);//最大空闲连接数,超过则释放连接
                    jedisPoolConfig.setMinIdle(0);//最小空闲连接数
                    jedisPoolConfig.setBlockWhenExhausted(true);//阻塞等待连接
                    jedisPoolConfig.setMaxWaitMillis(5000);//阻塞等待连接的最长时间,负数会无限等待
                    jedisPoolConfig.setTestOnBorrow(true);//在获取连接的时候检查有效性
                    jedisPoolConfig.setTestOnReturn(true);//在释放连接的时候检查有效性

                    //Jedis 节点信息
                    List<JedisShardInfo> jedisShardList = new ArrayList<>();

                    String[] addressArr = serverAddress.split(",");
                    for (int i = 0; i < addressArr.length; i++) {
                        JedisShardInfo jedisShardInfo = new JedisShardInfo(addressArr[i]);
                        jedisShardList.add(jedisShardInfo);
                    }

                    shardedJedisPool = new ShardedJedisPool(jedisPoolConfig, jedisShardList, Hashing.MURMUR_HASH);
                    logger.info("ShardedJedisPool init success.");
                }
            }catch (Exception ex){
                logger.error("error init ShardedJedisPool, ", ex);
            }finally {
                lock.unlock();
            }
        }
        if(shardedJedisPool == null){
            throw new RuntimeException("error init ShardedJedisPool >>>>>>>>");
        }
        return shardedJedisPool.getResource();
    }

    public static void main(String[] args) {
        System.out.println(JedisUtils.setString("zhangsan","30"));
        System.out.println(JedisUtils.setString("lisi","28"));
        System.out.println(JedisUtils.setString("wangwu","29"));
        System.out.println(JedisUtils.setString("bajie","80"));
        System.out.println(JedisUtils.setStringEx("wukong","90",30));
        System.out.println(JedisUtils.setStringEx("tangsong","100",0));
        System.out.println(JedisUtils.setList("employees","zs","ww","ls"));
        System.out.println(JedisUtils.setHash("name","zs","30"));
        System.out.println(JedisUtils.setHash("name","ww","31"));
        System.out.println(JedisUtils.setHash("name","ls","31"));
        System.out.println(JedisUtils.addSet("students","zs"));
        System.out.println(JedisUtils.addSet("students","ww"));
        System.out.println(JedisUtils.addSet("students","ls"));

        SsoUser ssoUser = new SsoUser();
        ssoUser.setUserName("felix");
        ssoUser.setUserId(888);
        ssoUser.setPassword("pwd");
        System.out.println(JedisUtils.setObject("sso-user"+ssoUser.getUserId(),ssoUser));
        System.out.println(JedisUtils.getObject("sso-user"+ssoUser.getUserId()));



    }

    private static byte[] serialize(Object object) {
        ObjectOutputStream outputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.error("Redis Util : serialize fail , " + e);
        } finally {
            try {
                if(outputStream != null){
                    outputStream.close();
                }
                if(baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    private static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            logger.error("Redis Util : unserialize fail , " + e);
        } finally {
            try {
                if(bais != null){
                    bais.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    public static String setObject(String key, Object object){
        byte[] objBytes = serialize(object);
        String result = null;
        if(objBytes == null){
            return result;
        }
        ShardedJedis client = getInstance();
        try {
            result = client.set(key.getBytes(), objBytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public static Object getObject(String key){
        ShardedJedis client = getInstance();
        try {
            byte[] bytes = client.get(key.getBytes());
            if(bytes != null){
                return unserialize(bytes);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }

    public static Long deleteKey(String key){
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.del(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }
    //永不过期
    public static String setString(String key, String value){
        String result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.set(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public static String getString(String key){
        String result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.get(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    //expireSeconds:过期时间,必须大于0
    public static String setStringEx(String key, String value, int expireSeconds){
        String result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.setex(key,expireSeconds, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public static Long setList(String key, String... value){
        Long size = null;
        ShardedJedis client = getInstance();
        try {
            size = client.lpush(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return size;
    }

    public static Long setHash(String key , String field, String value){
        Long size = null;
        ShardedJedis client = getInstance();
        try {
            size = client.hset(key,field, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return size;
    }

    public static Long addSet(String key , String value){
        Long size = null;
        ShardedJedis client = getInstance();
        try {
            size = client.sadd(key,value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return size;
    }

    //为key设置过期时间,1:设置成功,0:设置失败,已经设置过过期时间或者key不存在
    public static long expire(String key, int seconds) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.expire(key, seconds);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }
}
