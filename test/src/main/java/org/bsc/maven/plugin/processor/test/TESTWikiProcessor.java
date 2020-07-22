/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.maven.plugin.processor.test;

import org.bsc.processor.BaseAbstractProcessor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author softphone
 * 
 * 
 */
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedSourceVersion(SourceVersion.RELEASE_9)
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
        final FileObject f = filer.getResource(StandardLocation.CLASS_PATH, packageName, resource);

        //java.io.Reader r = f.openReader(true);  // ignoreEncodingErrors 
        try(java.io.InputStream is = f.openInputStream()) {

            if (is == null) {
                warn("resource [%s] not found!", resource);
                return null;
            }
        }
        return f;
    }

    /**
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver())      return false;

        final java.util.Map<String,String> optionMap = processingEnv.getOptions();
        
        info( "====> PROCESSOR RUN");
        optionMap.entrySet().forEach( k -> info( "\t[%s] = [%s]", k.getKey(), k.getValue()));

        for( TypeElement e : annotations ) {

            for (Element re : roundEnv.getElementsAnnotatedWith(e)) {

                if( re.getKind()==ElementKind.METHOD) {

                    info( "[%s], Element [%s] is [%s]", re.getEnclosingElement(), re.getKind(), re.getSimpleName() );

                }
            }
        }

        return true;
    }
    
 
}
