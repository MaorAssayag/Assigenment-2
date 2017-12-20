/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.AddStudent;
import bgu.spl.a2.sim.actions.OpenANewCourse;
import bgu.spl.a2.sim.actions.ParticipateInCourse;
import bgu.spl.a2.sim.actions.addSpace;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	public static ActorThreadPool actorThreadPool;
	public static JsonObject currentJsonObject;
	
	
	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start(){
    	//0. get the computers array and add them to a new warehouse.
    	//
    	JsonArray computers = currentJsonObject.getAsJsonArray("Computers");
    	Warehouse warehouse = new Warehouse(computers.size());
    	for (int i = 0; i < computers.size(); i++) {
			JsonObject data = computers.get(i).getAsJsonObject();
			Computer currentComputer = new Computer(data.get("Type").getAsString());
			currentComputer.setFailSig(data.get("Sig Fail").getAsLong());
			currentComputer.setSuccessSig(data.get("Sig Success").getAsLong());
			warehouse.addComputer(currentComputer);
		}
    	
    	//1. Phase 1 - An array of all the open courses actions, and some other action might appear. All the actions
    	// in Phase 1 should be completed before proceeding to Phase 2.
    	actorThreadPool.start();
    	JsonArray actions = currentJsonObject.getAsJsonArray("Phase 1");
    	CountDownLatch phase1 = new CountDownLatch(actions.size());
    	for (int i = 0; i < actions.size(); i++) {
			JsonObject data = actions.get(i).getAsJsonObject();
			addAction(data , phase1); // each action will count down phase1 when it is complete.
    	}
    	try {phase1.await();}
    	catch (InterruptedException e) {} // phase 1 is done.
    	
    	//2. Phase 2 - An array of all the open courses actions, and some other action might appear. All the actions
    	//  in Phase 2 should be completed before proceeding to Phase 3.
    	actions = currentJsonObject.getAsJsonArray("Phase 2");
    	CountDownLatch phase2 = new CountDownLatch(actions.size());
    	for (int i = 0; i < actions.size(); i++) {
			JsonObject data = actions.get(i).getAsJsonObject();
			addAction(data , phase2); // each action will count down phase2 when it is complete.
    	}
    	try {phase2.await();}
    	catch (InterruptedException e) {} // phase 2 is done.
    	
    	//2. Phase 3 - An array of all the open courses actions, and some other action might appear. All the actions
    	// in Phase 3 should be completed before proceeding to Phase 3.
    	actions = currentJsonObject.getAsJsonArray("Phase 3");
    	CountDownLatch phase3 = new CountDownLatch(actions.size());
    	for (int i = 0; i < actions.size(); i++) {
			JsonObject data = actions.get(i).getAsJsonObject();
			addAction(data , phase3); // phase3 countDownLatch currently not in use.
    	}   
    	
    	end(); // end simulator.
    }
    
	
	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorThreadPool = myActorThreadPool;
	}
	
	/**
	* shut down the simulation
	* returns list of private states
	*/
	public static HashMap<String,PrivateState> end(){
		try {
			actorThreadPool.shutdown();
			return actorThreadPool.getActorsHash();
		}
		catch(Exception e) {}
		return null;

	}
	
	/**
	*main method.
	*
	*/
	public static int main(String [] args){
		BufferedReader bufferedReader = null;
		try { bufferedReader = new BufferedReader(new FileReader(args[0]));} // WARNING : need to check this
		catch (FileNotFoundException e) { System.out.println("File Not Found at:" + args[0]); }
		currentJsonObject = new Gson().fromJson(bufferedReader, JsonObject.class);
		
		int threadsNum = currentJsonObject.get("threads").getAsInt(); //threadsNum:=how many threads to run.
		attachActorThreadPool(new ActorThreadPool(threadsNum));
		start();
		return 0;
	}
	
    /**
     * addAction - get the action data in JsonObject & submit it to actorThreadPoll according to its type.
     */
    public static void addAction(JsonObject currentAction, CountDownLatch currentPhase) {
    	String actionType = currentAction.get("Action").getAsString();
    	//TODO : finish all actions
    	switch (actionType) {
    		case "Open Course":{
    			String department = currentAction.get("Department").getAsString();
    			String course = currentAction.get("Course").getAsString();
    			int space = currentAction.get("Space").getAsInt();
                LinkedList<String> prerequisites = new LinkedList<>();
                Iterator<JsonElement> it = currentAction.get("Prerequisites").getAsJsonArray().iterator();
                while (it.hasNext())
                	prerequisites.add(it.next().getAsString()); // add each prerequisite
                //department State handle
                DepartmentPrivateState departmentState; 
				if (!actorThreadPool.getActors().containsKey(department)){
					departmentState = new DepartmentPrivateState();
					actorThreadPool.submit(null, department, departmentState);
				}else{
					departmentState = (DepartmentPrivateState)actorThreadPool.getPrivateState(department);
				}
                OpenANewCourse open = new OpenANewCourse(space, course, prerequisites,currentPhase);
                actorThreadPool.submit(open, department, departmentState);  //will call the handle function for 'open'
    		}
    		break;
    		
    		case "Add Student":{
    			String department = currentAction.get("Department").getAsString();
    			String studentID = currentAction.get("Student").getAsString();
    			
                //department State handle
                DepartmentPrivateState departmentState; 
				if (!actorThreadPool.getActors().containsKey(department)){
					departmentState = new DepartmentPrivateState();
					actorThreadPool.submit(null, department, departmentState);
				}else{
					departmentState = (DepartmentPrivateState)actorThreadPool.getPrivateState(department);
				}
				AddStudent add = new AddStudent(studentID, currentPhase);
				actorThreadPool.submit(add, department, departmentState);
    		}
    		break;
    		
    		case "Participate In Course":{
    			String studentID = currentAction.get("Student").getAsString();
    			String course = currentAction.get("Course").getAsString();
    			int grade = currentAction.get("Grade").getAsJsonArray().get(0).getAsInt();
				if (!actorThreadPool.getActors().containsKey(course)){
					return;}//there is no such course in the system
				ParticipateInCourse praticipate = new ParticipateInCourse(studentID, grade,currentPhase);
				actorThreadPool.submit(praticipate, course, actorThreadPool.getPrivateState(course));    			
    		}
    		break;
    		
    		case "Add Spaces":{
    			int num = currentAction.get("Number").getAsInt();
    			String course = currentAction.get("Course").getAsString();
    			if (!actorThreadPool.getActors().containsKey(course)){
					return;}//there is no such course in the system
    			addSpace add = new addSpace(course, num, currentPhase);
    			actorThreadPool.submit(add, course, actorThreadPool.getPrivateState(course));
    		}
    		break;
    		
    		case "Register With Preferences":{
    			//
    		}
    		break;
    		
    		case "Unregister":{
    			
    		}
    		break;
    		
    		case "End Registeration":{
    			
    		}
    		break;
    		
    		case "Administrative Check":{
    			
    		}
    		break;
    		
    		case "Close Course":{
    			
    		}
    		break;
    	}
    }
	
	/*
	 * End Of File.
	 */
	
}
