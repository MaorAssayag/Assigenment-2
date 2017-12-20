package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse {
	
	private Computer computers[];
	private int currentCapacity;
	
	public Warehouse(int capacity) {
		this.computers = new Computer[capacity];
		this.currentCapacity = capacity;
	}
	
	public void addComputer(Computer computer) {
		if (currentCapacity > 0) {
			this.computers[this.computers.length - currentCapacity] = computer;
			currentCapacity--;
		}
	}
	
	public Computer getComputer(String computerType) {
		for(Computer computer : this.computers) {
			if (computer.computerType.equals(computerType)){
				return computer;
			}
		}
		return null;
	}
	
	public SuspendingMutex getSuspendingMutex(String computerType) {
		for(Computer computer : this.computers) {
			if (computer.computerType.equals(computerType)){
				return computer.suspendingMutex;
			}
		}
		return null;
	}
	
	public Promise<Computer> acquireComputer(String computerType){
		return this.getComputer(computerType).getSuspendingMutex().down();
	}
	
	public void releaseComputer(String computerType){
		this.getComputer(computerType).getSuspendingMutex().up();
	}
	/*
	 * End Of File.
	 */
}
