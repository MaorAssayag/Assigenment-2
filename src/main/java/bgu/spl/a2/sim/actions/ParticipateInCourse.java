package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Assignment doc - Action no.3; this action will be called from course Actor.
 * This action should try to register the student in the course, if it succeeds, should add the
 * course to the grades sheet of the student, and give him a grade if supplied. See the input example.
 */
public class ParticipateInCourse extends Action<Boolean> {
    private CountDownLatch currentPhase;
    private int grade;
    private String studentID;
    

	public ParticipateInCourse(String studentID, int grade, CountDownLatch currentPhase) {
		this.grade = grade;
		this.studentID = studentID;
		this.currentPhase = currentPhase;
		this.Result = new Promise<Boolean>();
		this.setActionName("Participate In Course");
	}
	
	@Override
	protected void start() {
		this.actorState.addRecord(getActionName());
        if (((CoursePrivateState)actorState).getAvailableSpots() == 0){ //there is no available space in this course.
        	currentPhase.countDown();
            return;
        }
        StudentPrivateState studentState = (StudentPrivateState)this.pool.getPrivateState(this.studentID);
        if (studentState == null) { // the student isn't in the system.
        	currentPhase.countDown();
        	return;
        }
        
        List<String> prequisites = ((CoursePrivateState)actorState).getPrequisites();
        Action<Boolean> isValid = new isValidForParticipate(prequisites,this.actorId,grade);
        List<Action<Boolean>> temp = new LinkedList<Action<Boolean>>();
        temp.add(isValid);//list that contains isValid
        Promise<Boolean> result = (Promise<Boolean>) sendMessage(isValid,this.studentID,studentState); // return the promise of isValid&insert isValid to the pool.
        
        then(temp,()->{ //still in the course Actor
            if (result.get().booleanValue()) { // then the student registration is valid.
            	if (((CoursePrivateState)this.actorState).getAvailableSpots().intValue() == 0) {
                    Action<Boolean> disenroll = new disEnroll(this.actorId);
                    sendMessage(disenroll, this.studentID, studentState);
                    complete(false);
            	}
            	else{
            		((CoursePrivateState)this.actorState).addRegStudent(this.studentID);
            		complete(true);
            	}
            }else {complete(false); }//for later if necessary tracking.
            currentPhase.countDown();
        });        
        return;	
	}
	
	/*
	 * End Of File.
	 */
}
