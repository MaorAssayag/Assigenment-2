package bgu.spl.a2.sim.actions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
/**
 * Assignment doc - Action no.5;
 * If the student is enrolled in the course, this action should unregister him (update the
 * list of students of course, remove the course from the grades sheet of the student and increases the
 * number of available spaces).
 */
public class unRegister extends Action<Boolean>{
	String course;
	String studentID;

	public unRegister(String course, String studentID) {
		this.course = course;
		this.studentID = studentID;
		this.Result = new Promise<Boolean>();
		this.setActionName("Unregister");
	}
	
	@Override
	protected void start() {	
		StudentPrivateState studentState = (StudentPrivateState)pool.getPrivateState(this.studentID);
		if (studentState==null) {
			this.complete(false);
			return;
		}
        Action<Boolean> disenroll = new disEnroll(this.actorId); // will remove the grade of this course from student, if exsit.
        LinkedList<Action<Boolean>> temp = new LinkedList<Action<Boolean>>();
        temp.add(disenroll);
        
        Boolean found = false;
        ConcurrentLinkedQueue<Action<?>> actions = this.pool.getActorQueue(course);
		Iterator<Action<?>> it = actions.iterator();
		while (it.hasNext()) {
			Action<?> tempAction = it.next();
			if (tempAction.getActionName()=="Participate In Course") {
				if (((ParticipateInCourse) tempAction).getStudentId()==this.studentID) {
					tempAction.getResult().subscribe(()->{
						sendMessage(disenroll, this.studentID, studentState);	
					});
					found = true;
					break;
				}
			}
		}
		
		if(!found) {sendMessage(disenroll, this.studentID, studentState);}
        then(temp, ()->{
            ((CoursePrivateState)this.actorState).RemoveStudent(this.studentID); // will increase the avaliable spot by 1, if the sutdent was reg.
            complete(true);
        });	
	}
	/*
	 * End Of File.
	 */
}
