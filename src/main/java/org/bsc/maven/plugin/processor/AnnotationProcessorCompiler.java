/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bsc.maven.plugin.processor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.manager.CompilerManager;

/**
 *
 * @author softphone
 */
public class AnnotationProcessorCompiler implements JavaCompiler {

    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    
    final MavenProject project;
    final MavenSession session;
    final BuildPluginManager pluginManager;
    final CompilerManager plexusCompiler;
    
    AnnotationProcessorCompiler( CompilerManager plexusCompiler, MavenProject project, MavenSession session, BuildPluginManager pluginManager ) {
    
        this.project = project;
        this.session = session;
        this.pluginManager = pluginManager;
        this.plexusCompiler = plexusCompiler;
    }

/*    
    private String find( final Iterable<String> options, String key, String def ) {

        for( String option : options ) {
            if( option.equals(key)) {
                return 
            }
        }
        
    }
*/
    private void executePlugin(final Iterable<String> options) throws Exception {
        
        final CompilerConfiguration javacConf = new CompilerConfiguration();

        final java.util.Iterator<String> ii = options.iterator();
      
        while( ii.hasNext() ) {
            
            final String option = ii.next();
            
            if( "-cp".equals(option)) {
                javacConf.addClasspathEntry(ii.next());
            }
            else if( "-sourcepath".equals(option) ) {
                String [] sourceLocations = ii.next().split(":");
                for( String path : sourceLocations ) {
                    
                    final java.io.File dir = new java.io.File( path );
                    if( dir.exists() )
                        javacConf.addSourceLocation(path);
                
                }
                //javacConf.addCompilerCustomArgument(option, ii.next());
            }
            else if( "-proc:only".equals(option) ) {
                javacConf.setProc("only");
            }
            else if( "-processor".equals(option) ) {
                
                final String processors[] = ii.next().split(",");
                
                javacConf.setAnnotationProcessors( processors );
            }
            else if( "-d".equals(option) ) {
                javacConf.setOutputLocation(ii.next());
            }
            else if( "-s".equals(option) ) {
                javacConf.setGeneratedSourcesDirectory( new java.io.File(ii.next()));
            }
            
            javacConf.setSourceVersion("1.6");
            javacConf.setTargetVersion("1.6");
            
        }

        javacConf.setFork(false);
               
        final org.codehaus.plexus.compiler.Compiler javac = plexusCompiler.getCompiler("javac");   
        
        String[] cli = javac.createCommandLine(javacConf);
        
        for( String c : cli ) {
            System.out.printf( "CLI: [%s]\n", c);
        }
        javac.performCompile( javacConf );
        
    }
    
    @Override
    public CompilationTask getTask(
            final Writer out, 
            final JavaFileManager fileManager, 
            final DiagnosticListener<? super JavaFileObject> diagnosticListener, 
            final Iterable<String> options, 
            final Iterable<String> classes, 
            final Iterable<? extends JavaFileObject> compilationUnits) 
    {

        return new CompilationTask() {
            @Override
            public void setProcessors(Iterable<? extends Processor> processors) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setLocale(Locale locale) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Boolean call() {
                System.out.println("OPTIONS");
                for( String option : options ) {
                    System.out.printf("OPTION:\t[%s]\n", option);            
                }
                System.out.println("CLASSES");
                if( classes != null ) {
                    for( String clazz : classes ) {
                        System.out.printf("CLASS:\t[%s]\n", clazz);            
                    }
                }
                System.out.println("COMPILATION UNITS");
                if( compilationUnits != null ) {
                    for( JavaFileObject cu : compilationUnits ) {
                        System.out.printf("CUNIT:\t[%s]\n", cu);            
                    }
                }

                try {
                    executePlugin(options);
                    return true;
                } catch (final Exception ex) {
                    diagnosticListener.report( new Diagnostic<JavaFileObject>() {
                        @Override
                        public Diagnostic.Kind getKind() {
                            return Diagnostic.Kind.ERROR;
                        }

                        @Override
                        public JavaFileObject getSource() {
                            return null;
                        }

                        @Override
                        public long getPosition() {
                            return -1;
                        }

                        @Override
                        public long getStartPosition() {
                            return -1;
                        }

                        @Override
                        public long getEndPosition() {
                            return -1;
                        }

                        @Override
                        public long getLineNumber() {
                            return -1;
                        }

                        @Override
                        public long getColumnNumber() {
                            return -1;
                        }

                        @Override
                        public String getCode() {
                            return null;
                        }

                        @Override
                        public String getMessage(Locale locale) {
                            return ex.getLocalizedMessage();
                        }
                        
                        public String toString() {
                            return ex.getLocalizedMessage();
                        }
                        
                    });
                    return false;
                }
            }
        };
    }

    @Override
    public StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> diagnosticListener, Locale locale, Charset charset) {
        return compiler.getStandardFileManager(diagnosticListener, locale, charset);
    }

    @Override
    public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<SourceVersion> getSourceVersions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int isSupportedOption(String option) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
