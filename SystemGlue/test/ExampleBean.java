
public class ExampleBean {
	
	public ExampleBean(String text, int number) {
		super();
		this.text = text;
		this.number = number;
	}
	private String text;
	private int number;
	
	
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	

}
