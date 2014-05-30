package net.sf.systemglue.test;

public class MockBean {
	
	private String prop1;
	private int prop2;
	
	public MockBean() {
	}
	
	public MockBean(String prop1, int prop2) {
		this.prop1 = prop1;
		this.prop2 = prop2;
	}

	public String getProp1() {
		return prop1;
	}
	public void setProp1(String prop1) {
		this.prop1 = prop1;
	}
	public int getProp2() {
		return prop2;
	}
	public void setProp2(int prop2) {
		this.prop2 = prop2;
	}
}
