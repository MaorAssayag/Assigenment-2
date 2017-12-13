package bgu.spl.a2;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
	
	private BlockingQueue<Thread> threadsQueue;
	private ConcurrentHashMap<String, PrivateState> actorsPrivateStates;
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Action<?>>> actorsQueues;
	private ConcurrentHashMap<String, Boolean> isTheActorLocked;
	private AtomicBoolean isRunning;
	

	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */
	public ActorThreadPool(int nthreads) {
		this.actorsQueues = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Action<?>>>(); //Initialize private data members of the pool
		this.actorsPrivateStates = new ConcurrentHashMap<String, PrivateState>();
		this.isTheActorLocked = new ConcurrentHashMap<String, Boolean>();
		this.threadsQueue = new LinkedBlockingQueue<Thread>(nthreads);
		this.isRunning.set(false); // until the start() method will be called
		for (int i = 0; i < nthreads; i++) { //lets add 'nthreads' threads
			this.threadsQueue.add(new Thread( ()-> ThreadCode()) ); // ThreadCode method - Thread individual code
		}
	}

	/**
	 * getter for actors
	 * @return actors
	 */
	public Map<String, PrivateState> getActors(){
		return this.actorsPrivateStates;
	}
	
	/**
	 * getter for actor's private state
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId){
		return this.actorsPrivateStates.get(actorId);
	}


	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {
		boolean found = actorsPrivateStates.containsKey(actorId);
		if (found) {
			((ConcurrentLinkedQueue<Action<?>>)this.actorsQueues.get(actorId)).add(action);
		}else {
			ConcurrentLinkedQueue<Action<?>> temp = new ConcurrentLinkedQueue<Action<?>>();
			temp.add(action);
			this.actorsQueues.put(actorId, temp);
			this.isTheActorLocked.put(actorId, false); //this is a New Actor & he is available.
			this.actorsPrivateStates.put(actorId, actorState);
		}
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
		this.isRunning.set(false);
		for(Thread t:this.threadsQueue) {
			t.interrupt(); // Interrupts this thread - maybe.  
		}
		for(Thread t: this.threadsQueue) {
			t.join(); // Waits for this thread to die. 
		}
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		this.isRunning.set(true);
		for(Thread t:this.threadsQueue) {
			t.start(); // Interrupts this thread - maybe.  
		}
	}
	/**
	 * Thread individual code with the Loop design pattern
	 */
	private void ThreadCode() {
		
	}

}
