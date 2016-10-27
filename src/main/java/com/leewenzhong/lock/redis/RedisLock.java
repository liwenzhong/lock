package com.lee.lock;

/**
 * Created by liwenzhong on 16-10-26.
 */
public class RedisLock {

	private IRedisOp redisOp;

	private static long DEFAULT_TIME = 10L; //default 10 seconds
	private static String DEFAULT_VALUE = "_DEFAULT_VALUE";

	public boolean lock(String key) {
		return this.lock(key, DEFAULT_VALUE, DEFAULT_TIME);
	}

	public boolean lock(String key, long expireSecond) {
		return this.lock(key, DEFAULT_VALUE, expireSecond);
	}

	/**
	 * Acquires the lock. none block
	 */
	public boolean lock(String key, String value, long expireSecond) {
		long ttl = this.redisOp.ttl(key);
		//case 1: ttl>0  key exists and has expireTime
		//case 2: ttl=-1 key has no expireTime
		//case 3: ttl=-2 key does not exist
		if (ttl > 0L) {
			//already locked
			return false;
		} else if (ttl == -1L) {
			this.redisOp.del(key);
			return false;
		}

		boolean success = this.redisOp.setNx(key, value);
		if (success) {
			success = this.redisOp.expire(key, expireSecond);
		}
		return success;
	}

	/**
	 * Releases the lock.
	 */
	public void unlock(String key) {
		this.redisOp.del(key);
	}
}
