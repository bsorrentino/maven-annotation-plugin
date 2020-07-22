/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.maven.plugin.processor.test;

import org.bsc.processor.BaseAbstractProcessor;

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
//@SupportedSourceVersion(SourceVersion.RELEASE_9)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes( "*" )
//@SupportedOptions( {"subfolder", "filepath", "templateUri"})
//@SupportedAnnotationTypes( {"javax.ws.rs.GET", "javax.ws.rs.PUT", "javax.ws.rs.POST", "javax.ws.rs.DELETE"})
public class TESTWikiProcessor extends BaseAbstractProcessor {

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
