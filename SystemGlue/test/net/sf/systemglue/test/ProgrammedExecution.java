package net.sf.systemglue.test;

import java.util.HashMap;
import java.util.Map;

public class ProgrammedExecution {
	private static Map<Long,Timestamp> execucoes = new HashMap<Long, Timestamp>();
	public static final long INTERVAL = 15;
	
	public ProgrammedExecution(){}
	
	public void run(long id){		
		execucoes.put(id, new Timestamp(System.currentTimeMillis()));
		System.err.println("executing ProgrammedExecution, run() "+ id);
	}
	
	public static Timestamp timestamp(Long id){
		return execucoes.get(id);
	}
}
