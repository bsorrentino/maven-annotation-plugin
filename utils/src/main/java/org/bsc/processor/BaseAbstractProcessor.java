/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.processor;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;


import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.processing.RoundEnvironment;
import javax.tools.JavaFileManager;


/**
 *
 * @author bsorrentino
 * 
 * 
 */
//@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class BaseAbstractProcessor extends AbstractProcessor {

    protected void info( String msg ) {
        processingEnv.getMessager().printMessage(Kind.NOTE, msg );
    }

    protected void warn( String msg ) {
        //logger.warning(msg);
        processingEnv.getMessager().printMessage(Kind.WARNING, msg );
    }

    protected void warn( String msg, Throwable t ) {
        //logger.log(Level.WARNING, msg, t );
        processingEnv.getMessager().printMessage(Kind.WARNING, msg );
        t.printStackTrace(System.err);
    }

    protected void error( String msg ) {
        //logger.severe(msg);
        processingEnv.getMessager().printMessage(Kind.ERROR, msg );
    }

    protected void error( String msg, Throwable t ) {
        //logger.log(Level.SEVERE, msg, t );
        processingEnv.getMessager().printMessage(Kind.ERROR, msg );
        t.printStackTrace(System.err);
    }
    
    final Pattern p = Pattern.compile("(.*)[\\.]((?:\\w+)\\.gwt\\.xml)");

    /**
     * 
     * @param fqn
     * @return 
     */
    protected java.net.URL getResourceFromClassPath( String fqn )
    {
        return getResourceFromClassPath(fqn, getClass().getClassLoader());
    }
            
    /**
     * 
     * @param fqn
     * @param cl
     * @return 
     */
    protected java.net.URL getResourceFromClassPath( String fqn, ClassLoader cl )
    {
        if( fqn == null ) {
            throw new IllegalArgumentException( "fqn is null!");
        }
        if( cl == null ) {
            throw new IllegalArgumentException( "class loader is null!");
        }
        
        Matcher m = p.matcher(fqn);
        
        if( !m.matches() ) {
        	throw new IllegalArgumentException(String.format("parameter '%s' doesn't contain a valid fqn", fqn));
        }
          
        final String packageName = m.group(1);
        final String resource = m.group(2);

        info( String.format("packageName=[%s]\nresource=[%s]\n", packageName, resource));

        final String res;
        if( packageName==null || packageName.isEmpty() ) {
            res = resource;
        }
        else {
            res = packageName.replace('.', '/').concat("/").concat(resource);        
        }
        
        final java.net.URL url = cl.getResource( res  );

        return url;
			

    }
 
    /**
     * 
     */
    public static interface Predicate {
        
        /**
         * 
         * @param source
         * @param elements
         * @return 
         */
        boolean execute( TypeElement source, java.util.Set<? extends Element> elements );
    }
    
    
    protected void getElementsAnnotatedWith( 
            java.util.Set<? extends TypeElement> annotations, 
            RoundEnvironment roundEnv,
            Predicate p )
    {
        if( annotations == null ) {
            throw new IllegalArgumentException( "parameter annotations is null!");
        }
        if( roundEnv == null ) {
            throw new IllegalArgumentException( "parameter RoundEnvironment is null!");
        }
        if( p == null ) {
            throw new IllegalArgumentException( "paraemeter Predicate is null!");
        }
        
        for( TypeElement te : annotations ) {
            
            final java.util.Set<? extends Element> elems = 
                    roundEnv.getElementsAnnotatedWith(te);
            
            if( !p.execute(te, elems) ) {
                break;
            }
        }
    }
    /**
     * 
     * @param location
     * @return 
     */
    protected FileObject getResourceFormLocation(
            final JavaFileManager.Location location,
            final String packageName,
            final String resource ) throws FileNotFoundException,IOException 
    {
        if( location == null ) {
            throw new IllegalArgumentException( "location is null!");
        }
        if( packageName == null ) {
            throw new IllegalArgumentException( "packageName loader is null!");
        }
        if( resource == null ) {
            throw new IllegalArgumentException( "resource loader is null!");
        }
        
        final Filer filer = processingEnv.getFiler();
        
        FileObject f = filer.getResource(
                            location, 
                            packageName, 
                            resource);

        java.io.InputStream is = f.openInputStream();

        if( is==null ) {
            warn( String.format("resource [%s] not found!", resource) );
            return null;
        }
        
        is.close();
        
        return f;
    }
    
    
    /**
     * 
     * @param subfolder subfolder 
     * @param filePath relative path 
     * @return
     * @throws IOException 
     */
    protected FileObject createSourceOutputFile( 
                String subfolder, 
                String filePath ) throws IOException 
    {

        final Filer filer = processingEnv.getFiler();
        
    	Element e = null;
    	FileObject res = filer.createResource(
                            StandardLocation.SOURCE_OUTPUT, 
                            subfolder, 
                            filePath, 
                            e);
        return res;
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
    	
    	info( String.format("loading class [%s]", te.getQualifiedName().toString()));
    	
    	return Class.forName(te.getQualifiedName().toString());
    	
    }
    
    /**
     * 
     * @return com.sun.source.util.Trees
     */
    /*
    protected com.sun.source.util.Trees newTreesInstance() {

        return com.sun.source.util.Trees.instance(processingEnv);
        
    };
    */
    
    /**
     * 
     * @return 
     */
    protected java.util.Map<String,String> getOptions() 
    {
        java.util.Map<String,String> optionMap = processingEnv.getOptions();
        
        if(optionMap==null) {
            optionMap = Collections.emptyMap();
        }
        return optionMap ;
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
