/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Optional.empty;

/**
 *
 * @author bsorrentino
 * 
 * 
 */
public abstract class BaseAbstractProcessor extends AbstractProcessor {

    protected void info(String fmt, Object... args) {
        final String msg = java.lang.String.format(fmt, (Object[]) args);
        processingEnv.getMessager().printMessage(Kind.NOTE, msg);
    }

    protected void warn(String fmt, Object... args) {
        final String msg = java.lang.String.format(fmt, (Object[]) args);
        processingEnv.getMessager().printMessage(Kind.WARNING, msg);
    }

    protected void warn(String msg, Throwable t) {
        processingEnv.getMessager().printMessage(Kind.WARNING, msg);
        t.printStackTrace(System.err);
    }

    protected void error(String fmt, Object... args) {
        final String msg = java.lang.String.format(fmt, (Object[]) args);
        processingEnv.getMessager().printMessage(Kind.ERROR, msg);
    }

    protected void error(String msg, Throwable t) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg);
        t.printStackTrace(System.err);
    }

        
    /**
     * 
     * @return 
     */
    protected java.util.Map<String,String> getOptions() {
        return Optional.ofNullable(processingEnv.getOptions())
                    .orElseGet(  () -> Collections.emptyMap() );
    }

    /**
     * 
     * @param filter
     * @return
     */
    public Stream<? extends Element> elementStreamFromAnnotations( 
                java.util.Set<? extends TypeElement> annotations, 
                RoundEnvironment roundEnv,
                Predicate<? super TypeElement> filter ) 
    {
        if( annotations == null ) {
            throw new IllegalArgumentException( "annotations is null!");
        }
        if( roundEnv == null ) {
            throw new IllegalArgumentException( "roundEnv is null!");
        }
        if( filter == null ) {
            throw new IllegalArgumentException( "filter is null!");
        }
    	
        return annotations.stream()
                .filter( filter )
                .flatMap((e) -> roundEnv.getElementsAnnotatedWith(e).stream() );
                
    }

    /**
     * 
     * @param am
     * @param supplier
     * @return
     */
    protected <R extends java.util.Map<String,Object>> R toMapObject( AnnotationMirror am, java.util.function.Supplier<R> supplier ) {

		final Collector<java.util.Map.Entry<? extends Element, ? extends AnnotationValue>, R, R> c = 
				Collector.of( 
					supplier, 
					( map, entry ) -> 
						map.put( entry.getKey().getSimpleName().toString(), entry.getValue().getValue()),
					( v1, v2 ) -> v1
					);
					
	    final R result = am.getElementValues()
			.entrySet()
			.stream()
			.collect( c );
	    
	    return result;
    
    }

    /**
     *
     * @param moduleAndPkg
     * @param relativeName
     * @return
     * @throws IOException
     */
    protected FileObject getResourceFormClassPath(
            String moduleAndPkg,
            String relativeName ) throws IOException
    {
        return processingEnv.getFiler()
                .getResource(StandardLocation.CLASS_PATH, moduleAndPkg, relativeName);
    }

    /**
     * 
     * @param subfolder subfolder 
     * @param filePath relative path 
     * @return
     * @throws IOException 
     */
    protected FileObject createSourceOutputFile( 
                Path subfolder, 
                Path filePath ) throws IOException 
    {

    	return processingEnv.getFiler()
                .createResource( StandardLocation.SOURCE_OUTPUT,
                                subfolder.toString(),
                                filePath.toString(),
                                null);

    }
    

    /**
     * 
     * @param e
     * @return
     * @throws ClassNotFoundException
     */
    protected Class<?> getClassFromElement( Element e ) throws ClassNotFoundException 
    {
        if( null==e ) throw new IllegalArgumentException("e is null!");
        
    	if( ElementKind.CLASS!=e.getKind() ) {
            throw new IllegalArgumentException( String.format("element [%s] is not a class!", e));
        }
    	
    	TypeElement te = (TypeElement) e;
    	
    	info( "loading class [%s]", te.getQualifiedName().toString());
    	
    	return Class.forName(te.getQualifiedName().toString());
    	
    }
    
    /**
     * 
     * @param typeElement
     * @return 
     */
    protected String getFullClassName( Element typeElement ) {
     
        if( typeElement instanceof TypeElement  ) {
            
            return ((TypeElement)typeElement).getQualifiedName().toString();
        }
        
        return typeElement.getSimpleName().toString();
    }
    
		


}
