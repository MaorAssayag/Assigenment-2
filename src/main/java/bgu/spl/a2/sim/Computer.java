package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer {

	String computerType;
	long failSig;
	long successSig;
	SuspendingMutex suspendingMutex = new SuspendingMutex(this);
	
	public Computer(String computerType) {
		this.computerType = computerType;
	}
	
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		for(String currentCourse: courses) {
			if (coursesGrades.get(currentCourse) == null || coursesGrades.get(currentCourse) < 56) 
				return failSig;
		}
		return successSig;
	}
	
	public void setFailSig (long failSig) {
		this.failSig = failSig;
	}
	
	public void setSuccessSig (long successSig) {
		this.successSig = successSig;
	}
	
	public SuspendingMutex getSuspendingMutex() {
		return this.suspendingMutex;
	}
	
	/*
	 * End of File.
	 */
}
