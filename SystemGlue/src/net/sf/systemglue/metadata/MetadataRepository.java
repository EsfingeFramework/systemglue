package net.sf.systemglue.metadata;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sf.jColtrane.handler.JColtraneXMLHandler;
import net.sf.systemglue.exception.SystemGlueConfigurationException;
import net.sf.systemglue.reader.AnnotationMetadataReader;
import net.sf.systemglue.reader.SystemGlueJColtraneHandler;
import net.sf.systemglue.utils.ConfigUtils;

import org.xml.sax.InputSource;

public class MetadataRepository {
	
	private Map<Class,Map<Method, MethodMetadataContainer>> cache = new HashMap<Class,Map<Method, MethodMetadataContainer>>();
	
	private Map<Class,AnnotationMetadataContainer> annotationConfig = new HashMap<Class, AnnotationMetadataContainer>();
	
	public MethodMetadataContainer retrieveMetadata(Class c, Method m){
		if(cache.containsKey(c) && cache.get(c).containsKey(m)){
			return cache.get(c).get(m);
		}
		AnnotationMetadataReader reader = new AnnotationMetadataReader();
		MethodMetadataContainer mmc = reader.getMethodMetadataContainer(m);
		if(!cache.containsKey(c)){
			cache.put(c, new HashMap<Method, MethodMetadataContainer>());
		}
		cache.get(c).put(m, mmc);
		return mmc;
	}
	
	public boolean containsAnnotation(Class annotationClass){
		return annotationConfig.containsKey(annotationClass);
	}
	
	public AnnotationMetadataContainer getAnnotationContainer(Class annotationClass){
		return annotationConfig.get(annotationClass);
	}
	
	public void addAnnotationContainer(Class annotationClass, AnnotationMetadataContainer container){
		annotationConfig.put(annotationClass, container);
	}
	
	
	private static MetadataRepository instance = new MetadataRepository();

	public static MetadataRepository getInstance() {
		return instance;
	}

	public static void setInstance(MetadataRepository instance) {
		MetadataRepository.instance = instance;
	}
	
	public static MethodMetadataContainer getMetadata(Class c, Method m){
		return instance.retrieveMetadata(c, m);
	}
	
	public static void loadXMLFile(String path){
		loadXMLStream(ConfigUtils.getResourceAsStream(path));
	}
	
	public static void loadXMLStream(InputStream in){
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			InputSource input = new InputSource(in);
			parser.parse(input, new JColtraneXMLHandler(new SystemGlueJColtraneHandler()));
		} catch (Exception e) {
			throw new SystemGlueConfigurationException("Problems loading XML file", e);
		}
	}
	
	
	public static boolean containsAnnot(Class annotationClass){
		return getInstance().annotationConfig.containsKey(annotationClass);
	}
	
	public static AnnotationMetadataContainer getAnnotContainer(Class annotationClass){
		return getInstance().annotationConfig.get(annotationClass);
	}
	
	public static void addAnnotContainer(Class annotationClass, AnnotationMetadataContainer container){
		getInstance().annotationConfig.put(annotationClass, container);
		for(Class c : getInstance().cache.keySet()){
			for(Method m : getInstance().cache.get(c).keySet()){
				getInstance().cache.get(c).get(m).fireAnnotationConfigured(annotationClass, container);
			}
		}
	}

}
