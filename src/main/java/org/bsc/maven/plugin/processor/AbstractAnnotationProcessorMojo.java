/*
 *   Copyright (C) 2009 2010 2011 Bartolomeo Sorrentino <bartolomeo.sorrentino@gmail.com>
 * 
 *   This file is part of maven-annotation-plugin.
 *
 *    maven-annotation-plugin is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    maven-annotation-plugin is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with maven-annotation-plugin.  If not, see <http://www.gnu.org/licenses/>. 
 */

package org.bsc.maven.plugin.processor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.*;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;


/**
 * 
 * @author bsorrentino
 *
 */
public abstract class AbstractAnnotationProcessorMojo extends AbstractMojo
{

    interface ArtifactClosure {
       
        void execute( Artifact artifact );
    }
    
    private static final String SOURCE_CLASSIFIER = "sources";

    /**
     * 
     */
    //@MojoParameter(expression = "${project}", readonly = true, required = true)
    @Component
    protected MavenProject project;

    /**
     * 
     */
    //@MojoParameter(expression="${plugin.artifacts}", readonly = true )
    @Parameter(property="plugin.artifacts", readonly=true)
    private java.util.List<Artifact> pluginArtifacts;

    /**
     * Specify the directory where to place generated source files (same behaviour of -s option)
     * 
     */
    //@MojoParameter(required = false, description = "Specify the directory where to place generated source files (same behaviour of -s option)")
    @Parameter
    private File outputDirectory;

    /**
     * Annotation Processor FQN (Full Qualified Name) - when processors are not specified, the default discovery mechanism will be used
     * 
     */
    //@MojoParameter(required = false, description = "Annotation Processor FQN (Full Qualified Name) - when processors are not specified, the default discovery mechanism will be used")
    @Parameter
    private String[] processors;

    /**
     * Additional compiler arguments
     * 
     */
    //@MojoParameter(required = false, description = "Additional compiler arguments")
    @Parameter
    private String compilerArguments;

    /**
     * Additional processor options (see javax.annotation.processing.ProcessingEnvironment#getOptions()
     * 
     */
    @Parameter( alias = "options" )
    private java.util.Map<String,Object> optionMap;

    /**
     * Controls whether or not the output directory is added to compilation
     */
    //@MojoParameter(required = false, description = "Controls whether or not the output directory is added to compilation")
    @Parameter
    private Boolean addOutputDirectoryToCompilationSources;

    /**
     * Indicates whether the build will continue even if there are compilation errors; defaults to true.
     */
    //@MojoParameter(required = true, defaultValue = "true", expression = "${annotation.failOnError}", description = "Indicates whether the build will continue even if there are compilation errors; defaults to true.")
    @Parameter( defaultValue="true", required=true, property="annotation.failOnError" )
    private Boolean failOnError = true;

    /**
     * Indicates whether the compiler output should be visible, defaults to true.
     * 
     */
    //@MojoParameter(required = true, defaultValue = "true", expression = "${annotation.outputDiagnostics}", description = "Indicates whether the compiler output should be visible, defaults to true.")
    @Parameter( defaultValue="true", required=true, property="annotation.outputDiagnostics" )
    private boolean outputDiagnostics = true;

    /**
     * System properties set before processor invocation.
     * 
     */
    //@MojoParameter(required = false, description = "System properties set before processor invocation.")
    @Parameter
    private java.util.Map<String,String> systemProperties;
    
    /**
     * includes pattern
     */
    //@MojoParameter( description="includes pattern")
    @Parameter
    private String[] includes;
    
    /**
     * excludes pattern
     */
    //@MojoParameter( description="excludes pattern")
    @Parameter
    private String[] excludes;
    
    /**
     * additional source directories for the annotation processors.
     */
    @Parameter
    private java.util.List<File> additionalSourceDirectories;
    
    
    /**
     * if true add to the source directory of the annotation processor all compile source roots detected int the project
     * This is useful when we plan to use build-helper-maven-plugin
     * 
     * @since 2.1.1
     */
    @Parameter(defaultValue = "false")
    private boolean addCompileSourceRoots = false;
    
    
    /**
     * append source artifacts to sources list
     * 
     * @since 2.2.0
     */
    @Parameter( defaultValue = "false")
    private boolean appendSourceArtifacts = false;
    
    /**
     * The character set used for decoding sources
     *
     * @since 2.2.1
     */
    @Parameter(property = "project.build.sourceEncoding", readonly = true, required = true)
    private String encoding;
	
    /**
     * for execution synchronization
     */
    private static final Lock syncExecutionLock = new ReentrantLock();
    

    /**
     * 
     * @return supported source directories
     */
    protected abstract java.util.Set<File> getSourceDirectories( java.util.Set<File> result );
    
    /**
     * 
     * @return output folder
     */
    protected abstract File getOutputClassDirectory();

    /**
     * 
     * @param project
     * @param dir 
     */
    protected abstract void addCompileSourceRoot(MavenProject project, String dir);
    
    /**
     * 
     * @return 
     */
    public abstract File getDefaultOutputDirectory();


    private String buildProcessor()
    {
        if (processors == null || processors.length == 0)
        {
            return null;
        }

        StringBuilder result = new StringBuilder();

        int i = 0;

        for (i = 0; i < processors.length - 1; ++i)
        {
            result.append(processors[i]).append(',');
        }

        result.append(processors[i]);

        return result.toString();
    }

    protected abstract java.util.Set<String> getClasspathElements( java.util.Set<String> result );

    private String buildCompileClasspath()
    {
        
        java.util.Set<String> pathElements = new java.util.LinkedHashSet<String>();
            
        if( pluginArtifacts!=null  ) {

            for( Artifact a : pluginArtifacts ) {
                
                if( "compile".equalsIgnoreCase(a.getScope()) || "runtime".equalsIgnoreCase(a.getScope()) ) {
                    
                    java.io.File f = a.getFile();
                    
                    if( f!=null ) pathElements.add( a.getFile().getAbsolutePath() );
                }
            
            }
        }
        
        getClasspathElements(pathElements);
        
        StringBuilder result = new StringBuilder();
        
        for( String elem : pathElements ) {
            result.append(elem).append(File.pathSeparator);
        }
        return result.toString();
    }


    /**
     * 
     */
    public void execute() throws MojoExecutionException
    {
        if ("pom".equalsIgnoreCase(project.getPackaging())) // Issue 17
        {
            return;
        }

        syncExecutionLock.lock();
        
        try
        {
            executeWithExceptionsHandled();
        }
        catch (Exception e1)
        {
            super.getLog().error("error on execute: " + e1.getMessage());
            if (failOnError)
            {
                throw new MojoExecutionException("Error executing", e1);
            }
        }
        finally {
          syncExecutionLock.unlock();  
        }

    }

    @SuppressWarnings("unchecked")
    private void executeWithExceptionsHandled() throws Exception
    {
        if (outputDirectory == null)
        {
            outputDirectory = getDefaultOutputDirectory();
        }

        ensureOutputDirectoryExists();
        addOutputToSourcesIfNeeded();

        // new Debug(project).printDebugInfo();

        final String includesString = ( includes==null || includes.length==0) ? "**/*.java" : StringUtils.join(includes, ",");
        final String excludesString = ( excludes==null || excludes.length==0) ? null : StringUtils.join(excludes, ",");

        java.util.Set<File> sourceDirs = getSourceDirectories(new java.util.HashSet<File>( 5 ));
        
        if( addCompileSourceRoots ) {
            final java.util.List<String> sourceRoots = project.getCompileSourceRoots();
            if( sourceRoots != null ) {
                
                for( String s : sourceRoots ) {         
                    sourceDirs.add( new File(s) );
                }
            }

        }

        if( additionalSourceDirectories != null && !additionalSourceDirectories.isEmpty() ) {
            sourceDirs.addAll( additionalSourceDirectories );
        }
        
        
        if( sourceDirs == null ) {
            throw new IllegalStateException("getSourceDirectories is null!");
        }
        
        
        List<File> files = new java.util.ArrayList<File>();
        
        for( File sourceDir : sourceDirs ) {
            
            if( sourceDir==null ) {
                getLog().warn( "source directory is null! Processor task will be skipped!" );
                continue;            
            }
            
            getLog().debug( String.format( "processing source directory [%s]", sourceDir.getPath()) );
            
            if( !sourceDir.exists() ) {
                getLog().warn( String.format("source directory [%s] doesn't exist! Processor task will be skipped!", sourceDir.getPath()));
                continue;                        
            }
            if( !sourceDir.isDirectory() ) {
                getLog().warn( String.format("source directory [%s] is invalid! Processor task will be skipped!", sourceDir.getPath()));
                continue;                        
            }
        

            files.addAll( FileUtils.getFiles(sourceDir, includesString, excludesString) );
        }
       

        String compileClassPath = buildCompileClasspath();

        String processor = buildProcessor();

        List<String> options = new ArrayList<String>(10);

        options.add("-cp");
        options.add(compileClassPath);
        options.add("-proc:only");

        addCompilerArguments(options);

        if (processor != null)
        {
            options.add("-processor");
            options.add(processor);
        }
        else
        {
            getLog().warn("No processors specified. Using default discovery mechanism.");
        }
        options.add("-d");
        options.add(getOutputClassDirectory().getPath());

        options.add("-s");
        options.add(outputDirectory.getPath());


        if( getLog().isDebugEnabled() ) {
            for (String option : options) {
                getLog().debug("javac option: " + option);
            }
        }

        DiagnosticListener<JavaFileObject> dl = null;
        if (outputDiagnostics)
        {
            dl = new DiagnosticListener<JavaFileObject>()
            {

                public void report(Diagnostic< ? extends JavaFileObject> diagnostic)
                {
                    getLog().debug("diagnostic " + diagnostic);

                }

            };
        }
        else
        {
            dl = new DiagnosticListener<JavaFileObject>()
            {

                public void report(Diagnostic< ? extends JavaFileObject> diagnostic)
                {
                }

            };
        }

        if (systemProperties != null)
        {
            java.util.Set< Map.Entry<String,String>> pSet = systemProperties.entrySet();
            
            for ( Map.Entry<String,String> e : pSet ) 
            {
                getLog().debug( String.format("set system property : [%s] = [%s]",  e.getKey(), e.getValue() ));
                System.setProperty(e.getKey(), e.getValue());
            }

        }

        //
        // add to allSource the files coming out from source archives
        // 
        final List<JavaFileObject> allSources = new java.util.ArrayList<JavaFileObject>();
        
        processSourceArtifacts( new ArtifactClosure() {

            public void execute(Artifact artifact) {
                try {
                    
                    java.io.File f = artifact.getFile();

                    ZipFile zipFile = new ZipFile(f);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    int sourceCount = 0;

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry) entries.nextElement();

                        if (entry.getName().endsWith(".java")) {
                            ++sourceCount;
                            allSources.add(ZipFileObject.create(zipFile, entry));

                        }
                    }

                    getLog().debug(String.format("** Discovered %d java sources in %s", sourceCount, f.getAbsolutePath()));
                    
                } catch (Exception ex) {
                    getLog().warn(String.format("Problem reading source archive [%s]", artifact.getFile().getPath()));
                }
            }
        });
        
        //compileLock.lock();
        try {
            final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            
            if( compiler==null ) {
                getLog().error("JVM is not suitable for processing annotation! ToolProvider.getSystemJavaCompiler() is null.");
                return;
            }
            
            final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, Charset.forName(encoding));
    
            if( files!=null && !files.isEmpty() ) {
                
        
                for( JavaFileObject f : fileManager.getJavaFileObjectsFromFiles(files) ) {
                    
                    allSources.add(f);
                };
                
                
                
            }
            
            
            
            if( allSources.isEmpty() ) {
                getLog().warn( "no source file(s) detected! Processor task will be skipped");
                return;
            }
    
            final Iterable<String> classes = null;
            
            CompilationTask task = compiler.getTask(
                    new PrintWriter(System.out),
                    fileManager,
                    dl,
                    options, 
                    classes,
                    allSources);
    
            /*
             * //Create a list to hold annotation processors LinkedList<Processor> processors = new
             * LinkedList<Processor>();
             * 
             * //Add an annotation processor to the list processors.add(p);
             * 
             * //Set the annotation processor to the compiler task task.setProcessors(processors);
             */

            // Perform the compilation task.
            if (!task.call())
            {
    
                throw new Exception("error during compilation");
            }
        }
        finally {
           //compileLock.unlock(); 
        }
            
    }

    private List<File> scanSourceDirectorySources(File sourceDir) throws IOException {
        if( sourceDir==null ) {
            getLog().warn( "source directory cannot be read (null returned)! Processor task will be skipped");
            return null;
        }
        if( !sourceDir.exists() ) {
            getLog().warn( "source directory doesn't exist! Processor task will be skipped");
            return null;
        }
        if( !sourceDir.isDirectory() ) {
            getLog().warn( "source directory is invalid! Processor task will be skipped");
            return null;
        }

        final String includesString = ( includes==null || includes.length==0) ? "**/*.java" : StringUtils.join(includes, ",");
        final String excludesString = ( excludes==null || excludes.length==0) ? null : StringUtils.join(excludes, ",");

        List<File> files = FileUtils.getFiles(sourceDir, includesString, excludesString);
        return files;
    }

    private void addCompilerArguments(List<String> options)
    {
        if (!StringUtils.isEmpty(compilerArguments))
        {
            for (String arg : compilerArguments.split(" "))
            {
                if (!StringUtils.isEmpty(arg))
                {
                    arg = arg.trim();
                    getLog().debug("Adding compiler arg: " + arg);
                    options.add(arg);
                }
            }
        }
        if( optionMap!=null && !optionMap.isEmpty() ) {
            for( java.util.Map.Entry<String,Object> e : optionMap.entrySet() ) {
     
                if( !StringUtils.isEmpty(e.getKey()) && e.getValue()!=null ) {
                    String opt = String.format("-A%s=%s", e.getKey().trim(), e.getValue().toString().trim());
                    options.add( opt );
                    getLog().debug("Adding compiler arg: " + opt);
                }
            }
                       
        }
    }

    private void addOutputToSourcesIfNeeded()
    {
        final Boolean add = addOutputDirectoryToCompilationSources;
        if (add == null || add.booleanValue())
        {
            getLog().debug("Source directory: " + outputDirectory + " added");
            addCompileSourceRoot(project, outputDirectory.getAbsolutePath());
        }
    }

    private void ensureOutputDirectoryExists()
    {
        final File f = outputDirectory;
        if (!f.exists())
        {
            f.mkdirs();
        }
        if( !getOutputClassDirectory().exists()) {
            getOutputClassDirectory().mkdirs();
        }
    }


    private void processSourceArtifacts( ArtifactClosure closure ) {
        if( ! appendSourceArtifacts ) {
            return;
        }
        for (Artifact dep : this.project.getDependencyArtifacts()) {
            if ((dep.hasClassifier()) && (dep.getClassifier().equals(SOURCE_CLASSIFIER))) {
                
                closure.execute(dep);
                //getLog().debug("Append source artifact to classpath: " + dep.getGroupId() + ":" + dep.getArtifactId());
                //this.sourceArtifacts.add(dep.getFile());
            }
        }
    }

}
