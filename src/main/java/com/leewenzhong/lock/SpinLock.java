package com.lee.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

/**
 * Created by liwenzhong on 16-10-26.
 */
public class SpinLock extends AbstractLock implements Lock{

	private AtomicReference<Thread> threadHolder = new AtomicReference<Thread>(null);

	/**
	 * Acquires the lock.
	 * <p>
	 * <p>If the lock is not available then the current thread becomes
	 * disabled for thread scheduling purposes and lies dormant until the
	 * lock has been acquired.
	 * <p>
	 * <p><b>Implementation Considerations</b>
	 * <p>
	 * <p>A {@code Lock} implementation may be able to detect erroneous use
	 * of the lock, such as an invocation that would cause deadlock, and
	 * may throw an (unchecked) exception in such circumstances.  The
	 * circumstances and the exception type must be documented by that
	 * {@code Lock} implementation.
	 */
	@Override
	public void lock() {
		while (!threadHolder.compareAndSet(null, Thread.currentThread())) ;
	}

	/**
	 * none blocked
	 *
	 * @return
	 */
	@Override
	public boolean tryLock() {
		return threadHolder.compareAndSet(null, Thread.currentThread());
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		if (time <= 0L || unit == null) {
			throw new IllegalArgumentException("illegal arg time/unit!");
		}

		long expireTime = System.currentTimeMillis() + unit.toMillis(time);
		while (System.currentTimeMillis() - expireTime <= 0) {
			if (threadHolder.compareAndSet(null, Thread.currentThread())) {
				return true;
			}
		}

		throw new InterruptedException("wait for lock timeout!");
	}

	/**
	 * Releases the lock.
	 * <p>
	 * <p><b>Implementation Considerations</b>
	 * <p>
	 * <p>A {@code Lock} implementation will usually impose
	 * restrictions on which thread can release a lock (typically only the
	 * holder of the lock can release it) and may throw
	 * an (unchecked) exception if the restriction is violated.
	 * Any restrictions and the exception
	 * type must be documented by that {@code Lock} implementation.
	 */
	@Override
	public void unlock() {
		threadHolder.compareAndSet(Thread.currentThread(), null);
	}

}
