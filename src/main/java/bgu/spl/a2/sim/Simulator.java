/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;

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
    	//0. get the computers set and add them to a new warehouse.
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
    	//             in Phase 1 should be completed before proceeding to Phase 2.
    	actorThreadPool.start();
    	JsonArray actions = currentJsonObject.getAsJsonArray("Phase 1");
    	for (int i = 0; i < actions.size(); i++) {
			JsonObject data = actions.get(i).getAsJsonObject();
			addAction(data);
    	}

    
    }
    
    /**
     * addAction - get the action data in JsonObject & submit it to actorThreadPoll according to its type.
     */
    public static void addAction(JsonObject currentAction) {
    	String actionType = currentAction.get("Action").getAsString();
    	switch (actionType) {
    		case "Open Course":{
    			
    			
    			
    		}
    		break;
    		case "Add Student":{
    			
    		}
    		break;
    		case "Participate In Course":{
    			
    		}
    		break;
    		case "Register With Preferences":{
    			
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
	
	/*
	 * End Of File.
	 */
	
}
