package bgu.spl.a2.sim.actions;

import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class addSpace extends Action<Boolean> {

	String course;
	int numToAdd;
	CountDownLatch currentPhase;
	
	public addSpace(String course, int num, CountDownLatch currentPhase) {
		this.course = course;
		this.numToAdd = num;
		this.currentPhase = currentPhase;
		this.Result = new Promise<Boolean>();
		this.setActionName("Add Spaces");
	}
	
	@Override
	protected void start() {
       this.actorState.addRecord(getActionName());
       Integer currentSpot = ((CoursePrivateState)this.actorState).getAvailableSpots();
       if(currentSpot.intValue() != -1) {//then isnt close
           ((CoursePrivateState)this.actorState).setAvailableSpots(currentSpot + (Integer)this.numToAdd);
           this.complete(true);
       }else {
    	   this.complete(false);
       }
       currentPhase.countDown(); // this action if finished.
	}

	/*
	 * End Of File.
	 */
}
