package com.lee.lock;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by liwenzhong on 16-10-26.
 */
public class RedisLock {

	private IRedisOp redisOp;

	private static long DEFAULT_TIME = 10000L; //default 10 seconds

	private static final String UID = genUid();

	/**
	 * 生成uid， 尽量每台机器不同!!!这里时一个参考实现。
	 *
	 * @return
	 */
	private static String genUid() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return RandomStringUtils.random(8);
		}
	}

	/**
	 * 生成locked value
	 *
	 * @return
	 */
	private static String genLockedValue() {
		return String.format("%s_%s", UID, Thread.currentThread().getId());
	}

	/**
	 * get locked value
	 *
	 * @param key
	 * @return
	 */
	public String getLockedValue(String key) {
		return this.redisOp.get(key);
	}

	/**
	 * blocking lock! wait for the lock until available
	 *
	 * @param key
	 */
	public void lock(String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is blabk!");
		}

		while (!this.redisOp.setNx(key, genLockedValue())) ;
	}

	/**
	 * blocking lock! wait for the lock until available or interrupted or timeout!
	 *
	 * @param key
	 * @param time 等待的timeout时间
	 * @param unit
	 * @throws InterruptedException
	 */
	public void lock(String key, long time, TimeUnit unit) throws InterruptedException {
		if (StringUtils.isBlank(key) || time < 1L || unit == null) {
			throw new IllegalArgumentException("key/time/unit is invalid!");
		}

		long expireTime = System.currentTimeMillis() + unit.toMillis(time);
		while (System.currentTimeMillis() - expireTime <= 0) {
			if (this.redisOp.setNx(key, genLockedValue())) {
				return;
			}

			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
		}

		throw new InterruptedException("wait for lock timeout!");
	}

	/**
	 * none blocking lock
	 *
	 * @param key
	 * @return
	 */
	public boolean tryLock(String key) {
		return this.tryLock(key, genLockedValue(), DEFAULT_TIME);
	}

	/**
	 * none blocking lock
	 *
	 * @param key
	 * @param expireSecond
	 * @return
	 */
	public boolean tryLock(String key, long expireSecond) {
		return this.tryLock(key, genLockedValue(), expireSecond);
	}

	/**
	 * Acquires the lock. none blocking
	 */
	private boolean tryLock(String key, String value, long expireMs) {
		return this.redisOp.setNx(key, value, expireMs);
	}

	/**
	 * Releases the lock.
	 */
	public void unlock(String key) {
		if (StringUtils.equals(getLockedValue(key), genLockedValue())) {
			this.redisOp.del(key);
		}
	}

	/**
	 * force to release the lock.
	 */
	public void destoryLock(String key) {
		this.redisOp.del(key);
	}
}