import java.lang.reflect.Method;
import java.util.HashMap;

import org.jboss.el.lang.EvaluationContext;

import net.sf.systemglue.utils.ELUtils;


public class JBossELTest {


	public static void main(String[] args) {
		HashMap<String, Object> attributeMap = new HashMap<String, Object>();
		attributeMap.put("bean", new ExampleBean("text",23));
				
		EvaluationContext ec = ELUtils.buildEvaluationContext(new HashMap<String, Method>(), attributeMap);
		
		boolean bool = ELUtils.evaluateBooleanExpression(ec, "#{bean.text eq 'text'}");
		
		System.out.println(bool);
		
	}

}
