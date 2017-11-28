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
import org.apache.maven.project.MavenProject;
import org.apache.maven.toolchain.Toolchain;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.CompilerMessage;
import org.codehaus.plexus.compiler.CompilerResult;
import org.codehaus.plexus.compiler.manager.CompilerManager;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

class PlexusJavaCompilerWithOutput {

    static final PlexusJavaCompilerWithOutput INSTANCE = new PlexusJavaCompilerWithOutput();
    
    private PlexusJavaCompilerWithOutput() {     
    }
    
    private String getJavacExecutable(CompilerConfiguration config )
        throws java.io.IOException
    {
        if( !StringUtils.isEmpty(config.getExecutable())) {
            return config.getExecutable();
        }
        
        final String javacCommand = "javac" + ( Os.isFamily( Os.FAMILY_WINDOWS ) ? ".exe" : "" );

        String javaHome = System.getProperty( "java.home" );
        java.io.File javacExe;
        if ( Os.isName( "AIX" ) )
        {
            javacExe = new java.io.File( javaHome + java.io.File.separator + ".." + java.io.File.separator + "sh", javacCommand );
        }
        else if ( Os.isName( "Mac OS X" ) )
        {
            javacExe = new java.io.File( javaHome + java.io.File.separator + "bin", javacCommand );
        }
        else
        {
            javacExe = new java.io.File( javaHome + java.io.File.separator + ".." + java.io.File.separator + "bin", javacCommand );
        }

        // ----------------------------------------------------------------------
        // Try to find javacExe from JAVA_HOME environment variable
        // ----------------------------------------------------------------------
        if ( !javacExe.isFile() )
        {
            java.util.Properties env = CommandLineUtils.getSystemEnvVars();
            javaHome = env.getProperty( "JAVA_HOME" );
            if ( StringUtils.isEmpty( javaHome ) )
            {
                throw new java.io.IOException( "The environment variable JAVA_HOME is not correctly set." );
            }
            if ( !new java.io.File( javaHome ).isDirectory() )
            {
                throw new java.io.IOException(
                    "The environment variable JAVA_HOME=" + javaHome + " doesn't exist or is not a valid directory." );
            }

            javacExe = new java.io.File( env.getProperty( "JAVA_HOME" ) + java.io.File.separator + "bin", javacCommand );
        }

        if ( !javacExe.isFile() )
        {
            throw new java.io.IOException( "The javadoc executable '" + javacExe
                                       + "' doesn't exist or is not a file. Verify the JAVA_HOME environment variable." );
        }

        return javacExe.getAbsolutePath();
    }    
    /**
     * 
     * @param args
     * @return
     * @throws java.io.IOException 
     */
    private java.io.File createFileWithArguments( String[] args, String outputDirectory )
        throws java.io.IOException
    {
        java.io.PrintWriter writer = null;
        try
        {
            final java.io.File tempFile;
            {
                tempFile = java.io.File.createTempFile( org.codehaus.plexus.compiler.javac.JavacCompiler.class.getName(), "arguments" );
                tempFile.deleteOnExit();
            }

            writer = new java.io.PrintWriter( new java.io.FileWriter( tempFile ) );

            for ( String arg : args )
            {
                String argValue = arg.replace( java.io.File.separatorChar, '/' );

                writer.write( "\"" + argValue + "\"" );

                writer.println();
            }

            writer.flush();

            return tempFile;

        }
        finally
        {
            if ( writer != null )
            {
                writer.close();
            }
        }
    }
    
    private CompilerResult compileOutOfProcess( CompilerConfiguration config, String executable, String[] args )
        throws CompilerException
    {
        Commandline cli = new Commandline();

        cli.setWorkingDirectory( config.getWorkingDirectory().getAbsolutePath() );

        cli.setExecutable( executable );

        try
        {
            
            final java.io.File argumentsFile = createFileWithArguments( args, config.getOutputLocation() );
            cli.addArguments(
                new String[]{ "@" + argumentsFile.getCanonicalPath().replace( java.io.File.separatorChar, '/' ) } );
               
            if ( !StringUtils.isEmpty( config.getMaxmem() ) )
            {
                cli.addArguments( new String[]{ "-J-Xmx" + config.getMaxmem() } );
            }

            if ( !StringUtils.isEmpty( config.getMeminitial() ) )
            {
                cli.addArguments( new String[]{ "-J-Xms" + config.getMeminitial() } );
            }

            for ( String key : config.getCustomCompilerArgumentsAsMap().keySet() )
            {
                if ( StringUtils.isNotEmpty( key ) && key.startsWith( "-J" ) )
                {
                    cli.addArguments( new String[]{ key } );
                }
            }
        }
        catch ( java.io.IOException e )
        {
            throw new CompilerException( "Error creating file with javac arguments", e );
        }

        final CommandLineUtils.StringStreamConsumer out = new CommandLineUtils.StringStreamConsumer();

        int returnCode;

        final java.util.List<CompilerMessage> messages = java.util.Collections.emptyList();

        try
        {
            // ==> DEBUG
            //out.consumeLine( cli.toString() );
            
            returnCode = CommandLineUtils.executeCommandLine( cli, out, out );
        }
        catch ( Exception e )
        {
            throw new CompilerException( "Error while executing the external compiler.", e );
        }

        boolean success = returnCode == 0;
        return new CompilerResult( success, messages ) {
            @Override
            public String toString() {
                return out.getOutput();
            }
        };
    }
    
    private String[] getSourceFiles( CompilerConfiguration config ) throws java.io.IOException {
        
        final java.util.Set<String> sourceFiles = 
                    new java.util.HashSet<String>();
        for( java.io.File src : config.getSourceFiles() ) {
            sourceFiles.add( src.getCanonicalPath() );
        }
        
        return sourceFiles.toArray( new String[sourceFiles.size()] );
    }
    /**
     * 
     * @param config
     * @return 
     */
    protected CompilerResult performCompile( CompilerConfiguration config ) throws CompilerException, java.io.IOException {
        
        final java.util.Set<String> sourceFiles = 
                    new java.util.HashSet<String>();
        for( java.io.File src : config.getSourceFiles() ) {
            sourceFiles.add( src.getCanonicalPath() );
        }
        
        final String[] compilerArguments = 
                org.codehaus.plexus.compiler.javac.JavacCompiler.buildCompilerArguments(config, getSourceFiles(config) );

        return compileOutOfProcess( config, getJavacExecutable(config), compilerArguments );
        
    }
}
/**
 *
 * @author softphone
 */
public class AnnotationProcessorCompiler implements JavaCompiler {
    private static final String COMPILER_TARGET = "maven.compiler.target";
    private static final String COMPILER_SOURCE = "maven.compiler.source";

    private static final String PROCESSOR_TARGET = "maven.processor.target";
    private static final String PROCESSOR_SOURCE = "maven.processor.source";

    private static final String DEFAULT_SOURCE_VERSION = "1.7";
    private static final String DEFAULT_TARGET_VERSION = DEFAULT_SOURCE_VERSION ;

    final JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
    
    final MavenProject      project;
    final MavenSession      session;
    final CompilerManager   plexusCompiler;
    final Toolchain         toolchain;
    
    public static JavaCompiler createOutProcess(    Toolchain toolchain, 
                                                    CompilerManager plexusCompiler, 
                                                    MavenProject project, 
                                                    MavenSession session ) 
    {
     
        return new AnnotationProcessorCompiler( toolchain, plexusCompiler, project, session);
    }

    public static JavaCompiler createInProcess() {
     
        return ToolProvider.getSystemJavaCompiler();
    }
    
    private static void printCommand(  final org.codehaus.plexus.compiler.Compiler javac, 
                                final CompilerConfiguration javacConf,
                                final java.io.PrintWriter out ) throws CompilerException 
    {
        out.println();
        out.println();
        out.println( "javac \\");
        for( String c : javac.createCommandLine(javacConf) ) 
            out.printf( "%s \\\n", c);
        out.println();
        out.println();
        out.println();

    }
    	

   private AnnotationProcessorCompiler( Toolchain toolchain, 
                                         CompilerManager plexusCompiler, 
                                         MavenProject project, 
                                         MavenSession session ) 
    {
    
        this.project = project;
        this.session = session;
        this.plexusCompiler = plexusCompiler;
        this.toolchain = toolchain;
                
    }
    
    
    private void execute(   final Iterable<String> options, 
                            final Iterable<? extends JavaFileObject> compilationUnits,
                            final Writer w ) throws Exception 
    {
        final java.io.PrintWriter out =  ((w instanceof java.io.PrintWriter) ? 
                    ((java.io.PrintWriter)w) : new java.io.PrintWriter(w));
        
        final CompilerConfiguration javacConf = new CompilerConfiguration();

        final java.util.Iterator<String> ii = options.iterator();
      
        while( ii.hasNext() ) {
            final String option = ii.next();
            
            if( "-cp".equals(option)) {
                javacConf.addClasspathEntry(ii.next());
            }
            else if( "-sourcepath".equals(option) ) {
                String [] sourceLocations = ii.next().split(java.util.regex.Pattern.quote(java.io.File.pathSeparator));
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
            else if( "--release".equals(option) ) {
                javacConf.setReleaseVersion(ii.next());
            }
            else /*if( option.startsWith("-A") ) */  { // view pull #70
                // Just pass through any other arguments
                javacConf.addCompilerCustomArgument(option, "");   
            }
        }
        final java.util.Properties props = project.getProperties();

        final String sourceVersion = props.getProperty(PROCESSOR_SOURCE,props.getProperty(COMPILER_SOURCE, DEFAULT_SOURCE_VERSION));
        final String targetVersion = props.getProperty(PROCESSOR_TARGET,props.getProperty(COMPILER_TARGET, DEFAULT_TARGET_VERSION));

        javacConf.setSourceVersion(sourceVersion );
        javacConf.setTargetVersion(targetVersion);
        javacConf.setWorkingDirectory(project.getBasedir());

        final java.util.Set<java.io.File> sourceFiles = 
                new java.util.HashSet<java.io.File>();
        for( JavaFileObject src : compilationUnits ) {
            sourceFiles.add( new java.io.File( src.toUri() ) );
        }

        javacConf.setSourceFiles(sourceFiles);
        javacConf.setDebug(false);
        javacConf.setFork(true);
        javacConf.setVerbose(false);
            
        if( toolchain != null ) {
            final String executable = toolchain.findTool( "javac");
            //out.print( "==> TOOLCHAIN EXECUTABLE: "); out.println( executable );
            javacConf.setExecutable(executable);
        }
        
        CompilerResult result;
        
        // USING STANDARD PLEXUS
        /*
        final org.codehaus.plexus.systemJavaCompiler.Compiler javac = plexusCompiler.getCompiler("javac");       
        //printCommand(javac, javacConf, out );
        result = javac.performCompile( javacConf );
        for( CompilerMessage m : result.getCompilerMessages()) 
            out.println( m.getMessage() );
        */
        
        // USING CUSTOM PLEXUS
        
        result = PlexusJavaCompilerWithOutput.INSTANCE.performCompile(javacConf);
        
        out.println( result.toString() ); out.flush();
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
                try {
                    execute(options, compilationUnits, out);
                    return true;
                } catch (final Exception ex) {
                    diagnosticListener.report( new Diagnostic<JavaFileObject>() {
                        @Override
                        public Diagnostic.Kind getKind() {
                            return Diagnostic.Kind.ERROR;
                        }

                        @Override
                        public String getMessage(Locale locale) {
                            return ex.getLocalizedMessage();
                        }
                        
                        public String toString() {
                            return ex.getLocalizedMessage();
                        }
                        
                        @Override
                        public JavaFileObject getSource() { return null;}

                        @Override
                        public long getPosition() { return -1; }

                        @Override
                        public long getStartPosition() { return -1; }

                        @Override
                        public long getEndPosition() { return -1; }

                        @Override
                        public long getLineNumber() { return -1; }

                        @Override
                        public long getColumnNumber() { return -1; }

                        @Override
                        public String getCode() { return null; }

                    });
                    return false;
                }
            }
        };
    }

    @Override
    public StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> diagnosticListener, Locale locale, Charset charset) {
        return systemJavaCompiler.getStandardFileManager(diagnosticListener, locale, charset);
    }

    @Override
    public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<SourceVersion> getSourceVersions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int isSupportedOption(String option) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
