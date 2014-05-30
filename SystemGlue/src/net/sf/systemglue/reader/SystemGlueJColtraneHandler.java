package net.sf.systemglue.reader;

import java.lang.reflect.Method;


import net.sf.jColtrane.annotations.args.Attribute;
import net.sf.jColtrane.annotations.methods.BeforeElement;
import net.sf.jColtrane.annotations.methods.EndElement;
import net.sf.jColtrane.annotations.methods.StartElement;
import net.sf.jColtrane.handler.ContextVariables;
import net.sf.systemglue.exception.SystemGlueConfigurationException;
import net.sf.systemglue.finder.CreateObjectFinder;
import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.metadata.AnnotationMetadataContainer;
import net.sf.systemglue.metadata.MetadataContainer;
import net.sf.systemglue.metadata.MetadataRepository;
import net.sf.systemglue.metadata.MethodMetadataContainer;
import net.sf.systemglue.processor.MethodExecutor;
import net.sf.systemglue.utils.BeanUtils;

public class SystemGlueJColtraneHandler extends MetadataReader{
	
	@StartElement(tag="class")
	public void locateClass(@Attribute("name") String clazz, ContextVariables cv){
		Class c = BeanUtils.getClass(clazz);
		cv.getGeneralUseMap().put("currentclass", c);
	}
	
	@StartElement(tag="method")
	public void locateMethod(@Attribute("name") String name, @Attribute("params") String params, ContextVariables cv){
		Class c = (Class) cv.getGeneralUseMap().get("currentclass");
		for(Method m : c.getMethods()){
			if(m.getName().equals(name)){
				String methParams = "";
				for(Class paramClass : m.getParameterTypes()){
					if(!methParams.equals("")){
						methParams += ",";
					}
					methParams += paramClass.getName();
				}
				if(params == null || params.equals(methParams)){
					cv.getGeneralUseMap().put("metadata", MetadataRepository.getMetadata(c, m));
					return;
				}
			}
		}
	}
	
	@EndElement(tag="method")
	public void terminateMethod(ContextVariables cv){
		cv.getGeneralUseMap().remove("metadata");
	}
	
	@EndElement(tag="class")
	public void terminateClass(ContextVariables cv){
		cv.getGeneralUseMap().remove("currentclass");
	}
	
	@StartElement(tag="executeafter")
	public void addExecuteAfter(ContextVariables cv, 
			@Attribute("class") String clazz, 
			@Attribute("method") String method,
			@Attribute("async") boolean async,
			@Attribute("rule") String rule,
			@Attribute("finder") String finderClazz){
		
		Class c = BeanUtils.getClass(clazz);
		ObjectFinder finder = null;
		if(finderClazz == null)
			finder = new CreateObjectFinder();
		else
			finder = BeanUtils.createClassInstance(finderClazz, ObjectFinder.class);
		
		MethodExecutor me = createMethodExecutor(c, method, finder, rule, async);
		
		MetadataContainer mc = (MetadataContainer)cv.getGeneralUseMap().get("metadata");
		mc.addExecuteAfter(me);
	}
	
	@StartElement(tag="executebefore")
	public void addExecuteBefore(ContextVariables cv, 
			@Attribute("class") String clazz, 
			@Attribute("method") String method,
			@Attribute("async") boolean async,
			@Attribute("rule") String rule,
			@Attribute("finder") String finderClazz){
		
		Class c = BeanUtils.getClass(clazz);
		ObjectFinder finder = null;
		if(finderClazz == null)
			finder = new CreateObjectFinder();
		else
			finder = BeanUtils.createClassInstance(finderClazz, ObjectFinder.class);
		
		MethodExecutor me = createMethodExecutor(c, method, finder, rule, async);
		
		MetadataContainer mc = (MetadataContainer)cv.getGeneralUseMap().get("metadata");
		mc.addExecuteBefore(me);
	}
	

	@StartElement(tag="annotation")
	public void configureAnnotation(@Attribute("name") String clazz, ContextVariables cv){
		Class c = BeanUtils.getClass(clazz);
		cv.getGeneralUseMap().put("currentannotation", c);
		cv.getGeneralUseMap().put("metadata", new AnnotationMetadataContainer());
	}
	
	@EndElement(tag="annotation")
	public void addAnnotationConfig(ContextVariables cv){
		Class c = (Class) cv.getGeneralUseMap().get("currentannotation");
		AnnotationMetadataContainer amc = (AnnotationMetadataContainer)cv.getGeneralUseMap().get("metadata");
		if(MetadataRepository.containsAnnot(c)){
			throw new SystemGlueConfigurationException("This annotation was already configured in other XML file");
		}
		MetadataRepository.addAnnotContainer(c, amc);
		cv.getGeneralUseMap().remove("currentannotation");
		cv.getGeneralUseMap().remove("metadata");
	}
	

}
