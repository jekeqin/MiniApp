package top.corz.mini.config;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class RedisFastJsonSerializer<T> implements RedisSerializer<T> {

	private static final Charset charset = Charset.forName("UTF-8");
	
	private Class<T> clazz;
	
	public RedisFastJsonSerializer(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}
	
	@Override
	public byte[] serialize(T t) throws SerializationException {
		if(t==null)
			return new byte[0];
		
		return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(charset);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if(bytes==null || bytes.length<=0)
			return null;
		
		String str = new String(bytes, charset);
		
		return (T)JSON.parseObject(str, clazz);
	}

	
}
