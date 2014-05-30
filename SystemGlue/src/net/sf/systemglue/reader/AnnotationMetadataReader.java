package net.sf.systemglue.reader;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import net.sf.systemglue.annotations.DelegateReader;
import net.sf.systemglue.annotations.VectorialAnnotation;
import net.sf.systemglue.metadata.AnnotationMetadataContainer;
import net.sf.systemglue.metadata.MetadataRepository;
import net.sf.systemglue.metadata.MethodMetadataContainer;

public class AnnotationMetadataReader extends MetadataReader {
	
	public MethodMetadataContainer getMethodMetadataContainer(Method m){
		MethodMetadataContainer container = new MethodMetadataContainer();
		
		ConfigReader configReader = new ConfigReader();
		configReader.readConfig(container,m);
		readAnnotations(m, container);
		
		return container;
	}
	
	private void readAnnotations(AnnotatedElement ae, MethodMetadataContainer container){
		for(Annotation a : ae.getAnnotations()){
			Class annotationType = a.annotationType();			
			processElementAnnotated(a, annotationType, container);
		}
	}

	private void processElementAnnotated(Annotation a, Class annotationType, MethodMetadataContainer container) {
		if(annotationType.isAnnotationPresent(DelegateReader.class)){
			DelegateReader delegateReader = (DelegateReader) annotationType.getAnnotation(DelegateReader.class);
			Class<? extends AnnotationReader> reader = delegateReader.value();
			try {
				AnnotationReader annotationReader = reader.newInstance();
				annotationReader.readAnnotation(a, container);
			} catch (InstantiationException e) {
				throw new RuntimeException("Unable to create Reader", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Unable to access Reader", e);
			}
		}else if(annotationType.isAnnotationPresent(VectorialAnnotation.class)){
			Method field;
			try {
				field = annotationType.getMethod("value", null);
				if(field != null){
					Object[] values = (Object[])field.invoke(a);
					for(int i = 0;i<values.length;i++){
						Annotation annotationElement = (Annotation)values[i];
						Class annotElem = annotationElement.annotationType();
						//recursive call to get the actual reader
						processElementAnnotated(annotationElement, annotElem, container);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Couldn't read VectorialAnnotation",e);
			}				
		}else if(!a.annotationType().getName().startsWith("java.lang.annotation")){
			container.addAnnotation(a.annotationType());
			readAnnotations(a.annotationType(), container);
			if(MetadataRepository.containsAnnot(a.annotationType())){
				AnnotationMetadataContainer amc = MetadataRepository.getAnnotContainer(a.annotationType());
				container.readAnnotationContainer(amc);
			}
		}
		
	}

}
