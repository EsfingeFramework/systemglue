package net.sf.systemglue.utils;

import java.lang.reflect.Method;
import java.util.Map;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import org.jboss.el.ExpressionFactoryImpl;
import org.jboss.el.ValueExpressionLiteral;
import org.jboss.el.lang.EvaluationContext;
import org.jboss.el.lang.FunctionMapperImpl;
import org.jboss.el.lang.VariableMapperImpl;

public class ELUtils {
	
	private static FunctionMapper buildFunctionMapper(Map<String, Method> functionMethodMap){
		FunctionMapperImpl mapper = new FunctionMapperImpl();
		for(String functionName : functionMethodMap.keySet()){
			mapper.addFunction("", functionName, functionMethodMap.get(functionName));
		}
		return mapper;
	}
	
	private static VariableMapper buildVariableMapper(Map<String, Object> attributeMap){
		VariableMapperImpl mapper = new VariableMapperImpl();
		for(String attributeName : attributeMap.keySet()){
			Class clazz = Object.class;
			if(attributeMap.get(attributeName) != null)
				clazz = attributeMap.get(attributeName).getClass();
			ValueExpressionLiteral expression = new ValueExpressionLiteral(attributeMap.get(attributeName), clazz);
			mapper.setVariable(attributeName, expression);
		}
		
		return mapper;
	}
	
	public static EvaluationContext buildEvaluationContext(Map<String, Method> functionMethodMap, 
			Map<String, Object> attributeMap){
		
		VariableMapper vMapper = buildVariableMapper(attributeMap);
		FunctionMapper fMapper = buildFunctionMapper(functionMethodMap);
		
		SystemGlueELContext context = new SystemGlueELContext(fMapper, vMapper, new BeanELResolver(), new ArrayELResolver(), new ListELResolver(), new MapELResolver());
		
		return new EvaluationContext(context, fMapper,vMapper);
	}
	
	public static Boolean evaluateBooleanExpression(EvaluationContext elContext, String expression){
		ValueExpression result =  new ExpressionFactoryImpl().createValueExpression(
				elContext, expression, Boolean.class);
		
		return (Boolean) result.getValue(elContext);
	}

}
