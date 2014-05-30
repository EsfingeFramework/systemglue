package net.sf.systemglue.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreateSchedule {
	private GregorianCalendar calendar;
	private int interval = 0;

	private int hour(){		
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		return h;
	}
	
	private int minute(){		
		int m = calendar.get(Calendar.MINUTE);
		return m;
	}
	
	private int second(){
		int s = calendar.get(Calendar.SECOND);
		return s;
	}
	private int day(){
		int d = calendar.get(Calendar.DAY_OF_MONTH);
		return d;
	}
	private int month(){
		int m = calendar.get(Calendar.MONTH);
		return m;
	}
	private int year(){
		int m = calendar.get(Calendar.YEAR);
		return m;
	}	
	
	public String getStartExecution() {
		return day()+","+month()+","+year()+","+hour()+","+minute()+","+second();
	}

	public CreateSchedule(int secondsInterval) {
		this.interval = secondsInterval;		
		calendar = new GregorianCalendar();
		calendar.add(Calendar.SECOND,interval);
	}
}