package bgu.spl.a2.sim.actions;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class isValidForParticipate extends Action<Boolean> {

	CountDownLatch currentPhase;
	List<String> prequisites;
	String courseName;
	int grade;
	
	@Override
	protected void start() { 
		this.actorState.addRecord(getActionName());
		HashMap<String, Integer>gradeSheet = ((StudentPrivateState)this.actorState).getGrades();
        boolean  isValid = true;
        for(String course : prequisites){
            if(gradeSheet.get(course) == null || gradeSheet.get(course) < 56){
            	isValid = false;
                break;
            }
        }
        this.complete(isValid);
	}
	
	public isValidForParticipate(List<String> prequisites, String courseName, int grade) {
        this.Result = new Promise<Boolean>();
        this.prequisites = prequisites;
        this.courseName = courseName;
        this.grade = grade;
        setActionName("is Valid For Participate");
	}

	/*
	 * End Of File.
	 */
}
