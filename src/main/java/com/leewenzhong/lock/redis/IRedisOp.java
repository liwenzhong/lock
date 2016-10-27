package com.lee.lock;

/**
 * Created by liwenzhong on 16-10-26.
 */
public interface IRedisOp {
	boolean setNx(String key, String value);

	void del(String key);

	/**
	 * set expire time for key
	 *
	 * @param key
	 * @param time second
	 * @return
	 */
	boolean expire(String key, long time);
 
        /**
	 * get expire time for key
	 *
	 * @param key
	 * @return
                ttl>0  key exists and has expireTime
		ttl=-1 key has no expireTime
		ttl=-2 key does not exist 
	 */
	long ttl(String key);
}
