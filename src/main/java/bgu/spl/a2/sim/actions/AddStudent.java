package bgu.spl.a2.sim.actions;

import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;


/**
 * Assignment doc - Action no.2;
 * this class describe "add student" action.
 */
public class AddStudent extends Action<Boolean> {
    private String studentID;
    private CountDownLatch currentPhase;

    public AddStudent(String studentID,CountDownLatch currentPhase){
        this.setActionName("Add Student");
        this.studentID = studentID;
        this.Result = new Promise<Boolean>();
        this.currentPhase = currentPhase;
    }

    @Override
    protected void start() {
          if(((DepartmentPrivateState)this.actorState).AddStudent(studentID)) { // true if there was a change in the list
              StudentPrivateState student = new StudentPrivateState();
              student.setSignature(0);
              this.pool.submit(new EmptyAction(currentPhase),studentID,student);
          }
    }
}