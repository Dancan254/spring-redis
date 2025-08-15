package com.javaguy.springredis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class Main {

    private static final Log log = LogFactory.getLog(Main.class);
    public static void main(String[] args) {
        imperativeRedis();

    }

    /*
     * 1. LettuceConnectionFactory:
     *    - Creates a connection to a Redis server using the Lettuce client library.
     *    - Configured via its constructor with Redis connection details.
     *    - afterPropertiesSet() initializes the connection (similar to calling a "start" method).
     *
     * 2. RedisTemplate:
     *    - A helper class for performing Redis operations.
     *    - Configured with a connection factory and a serializer.
     *    - Keys are stored as UTF-8 strings instead of binary for better readability.
     *    - afterPropertiesSet() finalizes the setup.
     *
     * 3. Usage:
     *    - template.opsForValue().set("foo", "bar"); → Stores the key/value pair in Redis.
     *    - connectionFactory.destroy(); → Closes the Redis connection.
     */
    public static void imperativeRedis(){
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        connectionFactory.afterPropertiesSet();

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(StringRedisSerializer.UTF_8);
        template.afterPropertiesSet();

        template.opsForValue().set("foo", "bar");
        log.info("Value at foo: " + template.opsForValue().get("foo"));

        connectionFactory.destroy();
    }
}
