package com.lee.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Created by liwenzhong on 16-10-26.
 */
public class TicketLock extends AbstractLock implements Lock {

	private ThreadLocal<Integer> threadTktNum = new ThreadLocal<Integer>(); //ticketNum of the thread

	private AtomicInteger ticketNum = new AtomicInteger(0);

	private AtomicInteger serviceNum = new AtomicInteger(0);

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
		int threadTktNum = ticketNum.getAndIncrement();
		this.threadTktNum.set(threadTktNum);
		while (threadTktNum != serviceNum.get());
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
		int currTktNum = this.threadTktNum.get();
		this.serviceNum.compareAndSet(currTktNum, currTktNum+1);
	}
}
