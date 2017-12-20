package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class addSpace extends Action<Boolean> {

	String course;
	int numToAdd;
	
	public addSpace(String course, int num) {
		this.course = course;
		this.numToAdd = num;
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
	}

	/*
	 * End Of File.
	 */
}
