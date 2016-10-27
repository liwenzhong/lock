package com.lee.lock;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.Lock;

/**
 * Created by liwenzhong on 16-10-26.
 */
public class MCSLock extends AbstractLock implements Lock {

	private static class Node {
		volatile boolean isLocked = false;
		volatile Node next;
	}

	private ThreadLocal<Node> threadHolder = new ThreadLocal<Node>(); //hold the node of the thread

	private volatile Node queue; //the node queue

	private static AtomicReferenceFieldUpdater<MCSLock, Node> UPDATER = AtomicReferenceFieldUpdater.newUpdater(MCSLock.class, Node.class, "queue");

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
		Node currNode = new Node();
		threadHolder.set(currNode);

		Node preNode = UPDATER.getAndSet(this, currNode); // STEP 1
		if (preNode != null) {
			//another thread has the lock
			preNode.next = currNode;    //STEP 2
			while (!currNode.isLocked) ; //STEP 3
		}
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
		Node currNode = threadHolder.get();
		if (currNode.next == null) { //check if another thread wait for me
			if (UPDATER.compareAndSet(this, currNode, null)) { // STEP 4
				return;
			} else {
				//update fail, there is another thread is coming... then wait for inserting into the queue
				while (currNode.next == null) ;  // STEP 5
			}
		}

		currNode.next.isLocked = true;
		currNode.next = null;  //just for gc
	}
}
