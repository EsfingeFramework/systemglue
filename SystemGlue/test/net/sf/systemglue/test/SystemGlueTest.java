package net.sf.systemglue.test;

import static net.sf.classmock.ClassMockUtils.invoke;
import static net.sf.systemglue.test.ChangePropertyAction.changeProperty;
import static net.sf.systemglue.test.PropertyValueMatcher.propertyEquals;
import static net.sf.systemglue.test.WaitAndChangeAction.waitAndChange;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import net.sf.classmock.Annotation;
import net.sf.classmock.ClassMock;
import net.sf.systemglue.ProxyFactory;
import net.sf.systemglue.annotations.Execute;
import net.sf.systemglue.annotations.ExecutionMoment;
import net.sf.systemglue.annotations.Executions;
import net.sf.systemglue.annotations.Param;
import net.sf.systemglue.annotations.ReturnName;
import net.sf.systemglue.metadata.MetadataRepository;
import net.sf.systemglue.utils.BeanUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SystemGlueTest {
	
	Mockery context = new JUnit4Mockery(){{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    private Object mock;
    
    private ClassMock baseClass;
	
	private OtherClass mockCalled;
	
	private Object mockBase;
	
	private Object proxyBase;
	
    
    @Before
    public void createBaseClass(){
    	ProxyFactory.setProxyImplementation(ProxyFactory.REFLECTION);
    	baseClass  = new ClassMock("ApplicationClass",true);
    	baseClass.addAbstractMethod(void.class,"execute");
    	baseClass.addAbstractMethod(int.class,"executeWithParam",int.class);
    	baseClass.addAbstractMethod(void.class,"executeWithBean",MockBean.class);
    	mockCalled = context.mock(OtherClass.class);
    	MockFinder.addMock(OtherClass.class, mockCalled);
    }
    
    @Test
    public void executeBefore() throws Throwable{

    	addAnnotation(Execute.class, OtherClass.class, "toBeExecuted", "execute",ExecutionMoment.BEFORE);
    	
    	createMock();
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
        	one(mockCalled).toBeExecuted();inSequence(sequence);
        	invoke(one (mockBase),"execute");inSequence(sequence);
        }});
    	
		invoke(proxyBase,"execute");
    }


    
    @Test
    public void executeAfter() throws Throwable{

    	addAnnotation(Execute.class, OtherClass.class, "toBeExecuted", "execute",ExecutionMoment.AFTER);
    	
    	createMock();
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
    		invoke(one (mockBase),"execute");inSequence(sequence);
        	one(mockCalled).toBeExecuted();inSequence(sequence);
        }});
    	
		invoke(proxyBase,"execute");
    }
    
    @Test
    public void executeBeforeAndAfter() throws Throwable{
    	
    	Annotation a1 = createAnnotation(Execute.class, OtherClass.class, "toBeExecuted", ExecutionMoment.AFTER);
    	Annotation a2 = createAnnotation(Execute.class, OtherClass.class, "toBeExecuted", ExecutionMoment.BEFORE);
    	baseClass.addMethodAnnotation("execute", Executions.class, new Annotation[]{a1,a2});
    	
    	createMock();
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
        	one(mockCalled).toBeExecuted();inSequence(sequence);
        	invoke(one (mockBase),"execute");inSequence(sequence);
        	one(mockCalled).toBeExecuted();inSequence(sequence);
        }});
    	
		invoke(proxyBase,"execute");
    }
    
    
    @Test
    public void ruleExecution() throws Throwable{

    	//Add parameter name in parameter
    	baseClass.addMethodParamAnnotation(0, "executeWithParam",Param.class);
    	baseClass.addMethodParamAnnotationProperty(0, "executeWithParam",Param.class, "value", "num");
    	
    	Annotation a1 = createAnnotationWithRule(Execute.class, OtherClass.class, "toBeExecuted", ExecutionMoment.AFTER, "num > 10");
    	Annotation a2 = createAnnotationWithRule(Execute.class, OtherClass.class, "otherExecution", ExecutionMoment.BEFORE, "num < 10");
    	baseClass.addMethodAnnotation("executeWithParam", Executions.class, new Annotation[]{a1,a2});

    	createMock();
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
    		never(mockCalled).otherExecution();
    		invoke(one (mockBase),"executeWithParam", 15);inSequence(sequence);
        	one(mockCalled).toBeExecuted();inSequence(sequence);
        }});
    	
		invoke(proxyBase,"executeWithParam",15);
    }
    
    @Test
    public void parameterMapping() throws Throwable{

    	//Add parameter name in parameter and return name
    	baseClass.addMethodParamAnnotation(0, "executeWithParam",Param.class);
    	baseClass.addMethodParamAnnotationProperty(0, "executeWithParam",Param.class, "value", "num");
    	baseClass.addMethodAnnotation("executeWithParam", ReturnName.class, "returnNumber");
    	
    	//Add annotations
    	
    	Annotation a1 = createAnnotation(Execute.class, OtherClass.class, "paramEnd", ExecutionMoment.AFTER);
    	Annotation a2 = createAnnotation(Execute.class, OtherClass.class, "paramInit", ExecutionMoment.BEFORE);
    	baseClass.addMethodAnnotation("executeWithParam", Executions.class, new Annotation[]{a1,a2});

    	createMock();
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
    		one(mockCalled).paramInit(15);inSequence(sequence);will(returnValue(10));
    		invoke(one (mockBase),"executeWithParam", 10);inSequence(sequence);will(returnValue(5));
        	one(mockCalled).paramEnd(5);inSequence(sequence);
        }});
    	
		invoke(proxyBase,"executeWithParam",15);
    }
    
    @Test
    public void domainAnnotation() throws Throwable{

    	baseClass.addMethodAnnotation("execute", DomainAnnotation.class);
    	
    	createMock();
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
        	one(mockCalled).otherExecution();inSequence(sequence);
        	invoke(one (mockBase),"execute");inSequence(sequence);
        	one(mockCalled).toBeExecuted();inSequence(sequence);
        }});
    	
		invoke(proxyBase,"execute");
    }
        
    @Test
    public void XMLConfiguration() throws Throwable{
    	
    	baseClass.setRealName("ApplicationClass");
    	
    	String xml = "<systemglue>"
                    +"  <class name='ApplicationClass'>"
                    +"    <method name='execute'>"
                    +"       <executebefore class='net.sf.systemglue.test.OtherClass' "
                    +"                     method='otherExecution' finder='net.sf.systemglue.test.MockFinder'/>"
                    +"       <executeafter class='net.sf.systemglue.test.OtherClass' "
                    +"                     method='toBeExecuted' finder='net.sf.systemglue.test.MockFinder'/>"
                    +"    </method>"
                    +"  </class>"
                    +"</systemglue>";
    	
    	createMock();
    	
    	MetadataRepository.loadXMLStream(new ByteArrayInputStream(xml.getBytes()));
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
        	one(mockCalled).otherExecution();inSequence(sequence);
        	invoke(one (mockBase),"execute");inSequence(sequence);
        	one(mockCalled).toBeExecuted();inSequence(sequence);
        }});
    	
		invoke(proxyBase,"execute");
    }
        
    @Test
    public void XMLConfigurationWithAnnotation() throws Throwable{
    	
    	String xml = "<systemglue>"
                    +"  <annotation name='net.sf.systemglue.test.XMLDomainAnnotation'>"
                    +"       <executebefore class='net.sf.systemglue.test.OtherClass' "
                    +"                     method='otherExecution' finder='net.sf.systemglue.test.MockFinder'/>"
                    +"       <executeafter class='net.sf.systemglue.test.OtherClass' "
                    +"                     method='toBeExecuted' finder='net.sf.systemglue.test.MockFinder'/>"
                    +"  </annotation>"
                    +"</systemglue>";
    	    	
    	baseClass.addMethodAnnotation("execute", DomainAnnotation.class);
    	
    	createMock();
    	
    	MetadataRepository.loadXMLStream(new ByteArrayInputStream(xml.getBytes()));
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
        	one(mockCalled).otherExecution();inSequence(sequence);
        	invoke(one (mockBase),"execute");inSequence(sequence);
        	one(mockCalled).toBeExecuted();inSequence(sequence);
        }});
    	
		invoke(proxyBase,"execute");
    }
        
    @Test
    public void executeAsync() throws Throwable{

    	addAnnotation(Execute.class, OtherClass.class, "toBeExecuted", "execute",ExecutionMoment.AFTER);
    	baseClass.addMethodAnnotationProperty("execute", Execute.class, "async", true);
    	
    	createMock();
    	
    	context.checking(new Expectations() {{
    		invoke(one (mockBase),"execute");
        	one(mockCalled).toBeExecuted();will(waitAndChange());
        }});
    	
		invoke(proxyBase,"execute");
		
		assertEquals(0, WaitAndChangeAction.value);
		Thread.sleep(3000);
		assertEquals(1, WaitAndChangeAction.value);
    }
    
    @Test
    public void changeParameterProperties() throws Throwable{

    	Annotation a1 = createAnnotation(Execute.class, OtherClass.class, "moreExecuteBean", ExecutionMoment.AFTER);
    	Annotation a2 = createAnnotation(Execute.class, OtherClass.class, "executeBean", ExecutionMoment.BEFORE);
    	baseClass.addMethodAnnotation("executeWithBean", Executions.class, new Annotation[]{a1,a2});
    	
    	createMock();
    	
    	final MockBean mb = new MockBean("name",13);
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
        	one(mockCalled).executeBean(mb);
        	     inSequence(sequence);will(changeProperty("prop2", 23));
        	invoke(one (mockBase),"executeWithBean",with(propertyEquals(MockBean.class,"prop2",23)));
        	     inSequence(sequence);will(changeProperty("prop1", "otherName"));
        	one(mockCalled).moreExecuteBean(with(propertyEquals(MockBean.class,"prop1","otherName")));
        	     inSequence(sequence);
        }});
    	
		invoke(proxyBase,"executeWithBean", mb);
		
		assertEquals("otherName",mb.getProp1());
		assertEquals(23,mb.getProp2());
    }
    
    @Test
    public void passingParameterProperties() throws Throwable{

    	addAnnotation(Execute.class, OtherClass.class, "executeBeanProps", "executeWithBean",ExecutionMoment.AFTER);
    	baseClass.addMethodParamAnnotation(0, "executeWithBean",Param.class);
    	baseClass.addMethodParamAnnotationProperty(0, "executeWithBean",Param.class, "value", "bean");
    	
    	createMock();
    	
    	final MockBean mb = new MockBean("name",13);
    	
    	final Sequence sequence = context.sequence("sequence");
    	
    	context.checking(new Expectations() {{
        	invoke(one (mockBase),"executeWithBean",mb);
        	     inSequence(sequence);
        	one(mockCalled).executeBeanProps(13, "name");
        	     inSequence(sequence);
        }});
    	
		invoke(proxyBase,"executeWithBean", mb);
    }
    
    
	private void addAnnotation(Class annotation, Class clazz, String callMethod, String inMethod, ExecutionMoment when) {
		baseClass.addMethodAnnotation(inMethod, annotation)
    			.addMethodAnnotationProperty(inMethod, annotation, "clazz", clazz)
				.addMethodAnnotationProperty(inMethod, annotation, "method", callMethod)
				.addMethodAnnotationProperty(inMethod, annotation, "when", when)
				.addMethodAnnotationProperty(inMethod, annotation, "finder", MockFinder.class);
		
		
	}
	
	private Annotation createAnnotation(Class annotation, Class clazz,String callMethod, ExecutionMoment when) {
		Annotation an = new Annotation(annotation);
		an.addProperty("clazz", clazz);
		an.addProperty("method", callMethod);
		an.addProperty("when", when);
		an.addProperty("finder", MockFinder.class);
		return an;
	}
	
	private Annotation createAnnotationWithRule(Class annotation, Class clazz,String callMethod, ExecutionMoment when, String rule) {
		Annotation an = createAnnotation(annotation, clazz, callMethod, when);
		an.addProperty("rule",rule);
		return an;
	}
	
	private void createMock(){
		Class base = baseClass.createClass();
		BeanUtils.addDynamicClass(base.getName(), base);
    	mockBase = context.mock(base);
    	proxyBase = ProxyFactory.createProxy(mockBase, base);
	}

}
