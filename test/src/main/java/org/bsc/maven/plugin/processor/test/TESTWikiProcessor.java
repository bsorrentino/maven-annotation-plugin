/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.maven.plugin.processor.test;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 *
 * @author softphone
 * 
 * 
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes( "*" )
//@SupportedOptions( {"subfolder", "filepath", "templateUri"})
//@SupportedAnnotationTypes( {"javax.ws.rs.GET", "javax.ws.rs.PUT", "javax.ws.rs.POST", "javax.ws.rs.DELETE"})
public class TESTWikiProcessor extends AbstractProcessor {

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

     /**
     * 
     * @param filer
     * @return
     * @throws IOException
     */
    protected FileObject getResourceFormClassPath(Filer filer, final String resource, final String packageName) throws IOException {
        FileObject f = filer.getResource(StandardLocation.CLASS_PATH, packageName, resource);

        //java.io.Reader r = f.openReader(true);  // ignoreEncodingErrors 
        java.io.InputStream is = f.openInputStream();

        if( is==null ) {
            warn( String.format("resource [%s] not found!", resource) );
            return null;
        }
        
        return f;
    }
    
    
    /**
     * 
     * @param subfolder subfolder (e.g. confluence)
     * @param filePath relative path (e.g. children/file.wiki)
     * @return
     * @throws IOException 
     */
    protected FileObject getOutputFile( Filer filer, String subfolder, String filePath ) throws IOException {
        
    	Element e = null;
    	FileObject res = 
        		filer.createResource(StandardLocation.SOURCE_OUTPUT, 
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
    protected Class<?> getClassFromElement( Element e ) throws ClassNotFoundException {
    	if( null==e ) throw new IllegalArgumentException("e is null!");
    	if( ElementKind.CLASS!=e.getKind() ) throw new IllegalArgumentException( String.format("element [%s] is not a class!", e));
    	
    	TypeElement te = (TypeElement) e;
    	
    	info( String.format("loading class [%s]", te.getQualifiedName().toString()));
    	
    	return Class.forName(te.getQualifiedName().toString());
    	
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver())      return false;

        
        final java.util.Map<String,String> optionMap = processingEnv.getOptions();
        
        System.out.println( "====> PROCESSOR RUN");
        for( java.util.Map.Entry<String,String> k : optionMap.entrySet() )
            System.out.printf( "\t[%s] = [%s]\n", k.getKey(), k.getValue());
            
        for( TypeElement e : annotations ) {

        for (Element re : roundEnv.getElementsAnnotatedWith(e)) {

                if( re.getKind()==ElementKind.METHOD) {

                    info( String.format("[%s], Element [%s] is [%s] ", re.getEnclosingElement(), re.getKind(), re.getSimpleName()));

                }
            }
        }

        //final Filer filer = processingEnv.getFiler();

        //FileObject res = getOutputFile(filer, subfolder, filePath);
            
        //java.io.Writer w = res.openWriter();
            
        //w.close();
	
        return true;
    }
    
 
}
