package bgu.spl.a2.sim.actions;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

/**
 * Assignment doc - Action no.1;
 * This action opens a new course in a specied department. The course has an initially
 * available spaces and a list of prerequisites.
 */
public class OpenANewCourse extends Action<Boolean> {
	
	private int space;
    private String courseName;
    private List<String> prequisites;
    private CountDownLatch currentPhase;

    public OpenANewCourse(int space , String courseName, List<String> prequisites,CountDownLatch currentPhase){
        this.space = space;
        this.courseName = courseName;
        this.prequisites = prequisites;
        this.Result = new Promise<Boolean>();
        this.currentPhase = currentPhase;
        setActionName("Open A New Course");
    }

    @Override
    protected void start() {
        this.actorState.addRecord(this.getActionName());
        ((DepartmentPrivateState)this.actorState).AddCourse(this.courseName);
        CoursePrivateState courseState = new CoursePrivateState();
        courseState.setAvailableSpots(this.space);
        for (String prequisite :prequisites){
        	courseState.addPrequisite(prequisite);
        }
        this.pool.submit(new EmptyAction(currentPhase), this.courseName, courseState); 
    }
	
    /*
     * End Of File.
     */
}
