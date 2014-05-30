package net.sf.systemglue.test;

import net.sf.systemglue.annotations.Param;
import net.sf.systemglue.annotations.ReturnName;

public class OtherClass{
	
	public void toBeExecuted(){}
	
	public void otherExecution(){}
	
	@ReturnName("num")
	public int paramInit(@Param("num") int num){return 0;}
	
	public void paramEnd(@Param("returnNumber") int num){}
	
	public void executeBean(MockBean bean){};
	
	public void moreExecuteBean(MockBean bean){};
	
	public void executeBeanProps(@Param("bean.prop2") int i, @Param("bean.prop1") String str){};
	
	
}