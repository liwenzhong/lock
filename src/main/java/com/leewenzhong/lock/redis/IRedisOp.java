package com.lee.lock;

/**
 * Created by liwenzhong on 16-10-26.
 */
public interface IRedisOp {

	boolean setNx(String key, String value);

	/**
	 * 不存在时设置key, value, 且设置过期时间
	 * redis server >=2.6.12 http://redisdoc.com/string/set.html
	 *
	 * @param key
	 * @param value
	 * @param time  过期时间，单位毫秒,millisecond
	 * @return
	 */
	boolean setNx(String key, String value, long time);

	/**
	 * 获取key的剩余生存时间，单位毫秒, millisecond
	 *
	 * @param key
	 * @return
	       ttl>0  key exists and has expireTime
	       ttl=-1 key has no expireTime
	       ttl=-2 key does not exist
	 */
	long pttl(String key);

	String get(String key);

	void del(String key);
}