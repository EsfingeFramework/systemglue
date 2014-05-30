package net.sf.systemglue.test;

public class Timestamp {
	private long millis;

	public Timestamp(long millis) {
		this.millis = millis;
	}

	public long getId() {
		return millis;
	}	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Timestamp || obj==null){
			Timestamp otherTimestamp = ((Timestamp)obj);
			return this.millis==otherTimestamp.getId();
		}
		return false;		
	}
	
}
