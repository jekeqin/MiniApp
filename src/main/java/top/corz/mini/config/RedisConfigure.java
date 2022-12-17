package top.corz.mini.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.alibaba.fastjson.parser.ParserConfig;

//@EnableCaching
//@Configuration
public class RedisConfigure extends CachingConfigurerSupport {

	@Bean
	public RedisMessageListenerContainer container (RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        return container;
	}

	@Bean("redisTemplate")
	@ConditionalOnMissingBean(RedisTemplate.class)
	public RedisTemplate<String, Object> template(RedisConnectionFactory factory)
	{
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(factory);

		ParserConfig.getGlobalInstance().addAccept("top.corz");
		RedisFastJsonSerializer<Object> jsonSeria = new RedisFastJsonSerializer<>(Object.class);
		
		StringRedisSerializer stringSeria = new StringRedisSerializer();
		template.setKeySerializer(stringSeria);
		template.setHashKeySerializer(stringSeria);
		
		template.setValueSerializer(jsonSeria);
		template.setHashValueSerializer(jsonSeria);
		
		template.afterPropertiesSet();

		return template;
	}
	
}
