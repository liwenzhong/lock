package com.lee.lock;

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
