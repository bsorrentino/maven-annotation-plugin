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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static java.lang.String.format;

/**
 *
 * @author softphone
 * 
 * 
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@SupportedOptions( {"subfolder", "filepath", "templateUri"})
@SupportedAnnotationTypes( { "org.bsc.maven.plugin.processor.test.GenerateClass" })
public class TestGenerateSourceProcessor extends BaseAbstractProcessor {

    void copy(java.io.InputStream source, java.io.OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }
    void writeSourceCode( Element e ) {

        final java.net.URL url =  getClass().getClassLoader().getResource("GeneratedClass_java.txt");
        try {

            final FileObject source = super.createSourceOutputFile( Paths.get("test"), Paths.get("GeneratedClass.java") );

            try( java.io.OutputStream os = source.openOutputStream(); java.io.InputStream is = url.openStream() ) {
                copy( is, os );
            }

        } catch (Exception ex) {
            error( format("error writing source file [%s]", url ), ex);
        }
    }
    /**
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) return false;

        super.elementStreamFromAnnotations( annotations, roundEnv, e -> true )
                .peek( System.out::println)
                .forEach( this::writeSourceCode );

        return true;
    }
    
 
}
