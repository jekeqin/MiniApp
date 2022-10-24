package top.corz.mini.plugins;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

//@Component
public class RedisApi {

	@Resource
	@Qualifier("redisTemplate")
	private RedisTemplate<String, Object> redisTemplate;
	
	/**
	 * 设置过期时间
	 * @param key
	 * @param time
	 * @return
	 */
	public boolean expire(String key, long time) {
		try {
			if (time > 0) {
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 查看过期时间
	 * @param key
	 * @return
	 */
	public long getExpire(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}
	
	/**
	 * 校验是否存在key
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 移除keys
	 * @param key
	 */
	public void remove(String... key) {
		if (key != null && key.length > 0) {
			if (key.length == 1) {
				redisTemplate.delete(key[0]);
			} else {
				redisTemplate.delete(Arrays.asList(key));
			}
		}
	}
	
	
	// String ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	
	/**
	 * 获取key值
	 * @param key
	 * @return
	 */
	public Object valueGet(String key) {
		return key == null ? null : redisTemplate.opsForValue().get(key);
	}
	
	public Integer valueInt(String key) {
		Object obj = valueGet(key);
		if( obj==null )
			return 0;
		return (Integer)obj;
	}
	
	/**
	 * 存入key值
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean valuePut(String key, Object value) {
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 存入key值，并设置过期时间
	 * @param key
	 * @param value
	 * @param time
	 * @return
	 */
	public boolean valuePut(String key, Object value, long time) {
		try {
			if (time > 0) {
				redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
			} else {
				valuePut(key, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 递增key
	 * @param key
	 * @param delta
	 * @return
	 */
	public long incr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		return redisTemplate.opsForValue().increment(key, delta);
	}
	public long incr(String key, long delta, long timeout) {
		incr(key, delta);
		expire(key, timeout);
		return 1;
	}
	/**
	 * 递减key
	 * @param key
	 * @param delta
	 * @return
	 */
	public long decr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递减因子必须大于0");
		}
		return redisTemplate.opsForValue().increment(key, -delta);
	}
	
	// Hash ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	
	public Object hashGet(String key, String item) {
		return redisTemplate.opsForHash().get(key, item);
	}
	
	public Map<Object, Object> hashGetMap(String key) {
		return redisTemplate.opsForHash().entries(key);
	}
	
	public boolean hashPutMap(String key, Map<String, Object> map) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean hashPutMap(String key, Map<String, Object> map, long time) {
		boolean exec = this.hashPutMap(key, map);
		if (exec && time > 0) {
			expire(key, time);
		}
		return exec;
	}
	
	public boolean hashPut(String key, String item, Object value) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean hashPut(String key, String item, Object value, long time) {
		boolean exec = this.hashPut(key, item, value);
		if (exec && time > 0) {
			expire(key, time);
		}
		return exec;
	}
	
	public void hashRemove(String key, Object... item) {
		redisTemplate.opsForHash().delete(key, item);
	}
	
	public boolean hashHasKey(String key, String item) {
		return redisTemplate.opsForHash().hasKey(key, item);
	}
	
	public double hashIncr(String key, String item, double by) {
		return redisTemplate.opsForHash().increment(key, item, by);
	}
	
	public double hashDecr(String key, String item, double by) {
		return redisTemplate.opsForHash().increment(key, item, -by);
	}
	
	// Set ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	
	public Set<Object> setGet(String key) {
		try {
			return redisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean setHasKey(String key, Object value) {
		try {
			return redisTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public long setPut(String key, Object... values) {
		try {
			return redisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public long setPutExpire(String key, long time, Object... values) {
		long count = this.setPut(key, values);
		if (count>0 && time > 0) {
			expire(key, time);
		}
		return count;
	}
	
	public long setSize(String key) {
		try {
			return redisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public long setRemove(String key, Object... values) {
		try {
			Long count = redisTemplate.opsForSet().remove(key, values);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	// List ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	
	public List<Object> listGet(String key, long start, long end) {
		try {
			return redisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public long listSize(String key) {
		try {
			return redisTemplate.opsForList().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public Object listGetIndex(String key, long index) {
		try {
			return redisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
    /**
     * 从左边拿出来一个
     * @param k string key
     * @param t Long 超时秒数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    public Object listLeftPop(String key, long timeout){
		try {
			return redisTemplate.opsForList().leftPop(key, timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
	public boolean listLeftPut(String key, Object value) {
		try {
			redisTemplate.opsForList().leftPush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
    public Object listRightPop(String key, long timeout){
		try {
			return redisTemplate.opsForList().rightPop(key, timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

	public boolean listRightPut(String key, Object value) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean listRightPut(String key, Object value, long time) {
		boolean exec = this.listRightPut(key, value);
		if (exec && time > 0) {
			expire(key, time);
		}
		return exec;
	}
	
	public boolean listRightPut(String key, List<Object> value) {
		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean listRightPut(String key, List<Object> value, long time) {
		boolean exec = this.listRightPut(key, value);
		if (exec && time > 0) {
			expire(key, time);
		}
		return exec;
	}
	
	public boolean listUpdateIndex(String key, long index, Object value) {
		try {
			redisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public long listRemove(String key, long count, Object value) {
		try {
			Long remove = redisTemplate.opsForList().remove(key, count, value);
			return remove;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public long vagueRemove(String query) {
		Set<String> keys = redisTemplate.keys(query);
		if(keys!=null && keys.size()>0 )
			return redisTemplate.delete(keys);
		return 0;
	}
	
	public long dayExpire() {
		LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
		return  ChronoUnit.SECONDS.between(LocalDateTime.now(), end);
	}
	
	/**
	 * 	根据 yyMMddHHmm(12) + 自定义总位数自动生成ID
	 * @param key 
	 * @param length
	 * @return
	 */
	public String AtomicId(String cacheName, String prefix, int length) {
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
		RedisAtomicLong counter = new RedisAtomicLong(cacheName + date, redisTemplate.getConnectionFactory());
		counter.expire(61, TimeUnit.SECONDS);
		return prefix + date + String.format("%0"+(length - prefix.length() - date.length()) + "d", counter.incrementAndGet());
	}
	
}
