package bgu.spl.a2.sim;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import bgu.spl.a2.Promise;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private Computer computer;
	AtomicBoolean isFree;
	Queue<Promise<Computer>> promiseList;
	
	/**
	 * Constructor
	 * @param computer
	 */
	public SuspendingMutex(Computer computer){
		this.computer = computer;
		this.isFree = new AtomicBoolean(true);
		this.promiseList = new PriorityQueue<Promise<Computer>>();
	}
	
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediately.
	 * 
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down(){
		Promise<Computer> promise = new Promise<Computer>();
		if (this.isFree.getAndSet(false)) {
			promise.resolve(this.computer);
		}
		else 
			this.promiseList.add(promise);
		
		return promise;
	}
	
	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up(){
		if (this.promiseList.size() > 0) { 
			if(!this.isFree.getAndSet(true)) { // return false if isFree was false
				this.promiseList.poll().resolve(this.computer); // resolve the top (oldest) request for acquisition.
			}
		}
	}
	
	/*
	 * End of File.
	 */
}
