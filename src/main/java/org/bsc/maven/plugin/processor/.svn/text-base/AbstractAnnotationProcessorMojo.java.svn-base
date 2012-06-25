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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;


/**
 * 
 * @author bsorrentino
 *
 * @threadSafe
 */
public abstract class AbstractAnnotationProcessorMojo extends AbstractMojo
{

    /**
     * @parameter expression = "${project}"
     * @readonly
     * @required
     */
    //@MojoParameter(expression = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * @parameter expression="${plugin.artifacts}"
     * @readonly
     */
    //@MojoParameter(expression="${plugin.artifacts}", readonly = true )
    private java.util.List<Artifact> pluginArtifacts;

    /**
     * Specify the directory where to place generated source files (same behaviour of -s option)
     * @parameter
     * 
     */
    //@MojoParameter(required = false, description = "Specify the directory where to place generated source files (same behaviour of -s option)")
    private File outputDirectory;

    /**
     * Annotation Processor FQN (Full Qualified Name) - when processors are not specified, the default discovery mechanism will be used
     * @parameter
     * 
     */
    //@MojoParameter(required = false, description = "Annotation Processor FQN (Full Qualified Name) - when processors are not specified, the default discovery mechanism will be used")
    private String[] processors;

    /**
     * Additional compiler arguments
     * @parameter
     * 
     */
    //@MojoParameter(required = false, description = "Additional compiler arguments")
    private String compilerArguments;

    /**
     * Additional processor options (see javax.annotation.processing.ProcessingEnvironment#getOptions()
     * @parameter alias="options"
     * 
     */
    private java.util.Map<String,Object> optionMap;

    /**
     * Controls whether or not the output directory is added to compilation
     */
    //@MojoParameter(required = false, description = "Controls whether or not the output directory is added to compilation")
    private Boolean addOutputDirectoryToCompilationSources;

    /**
     * Indicates whether the build will continue even if there are compilation errors; defaults to true.
     * @parameter default-value="true"  expression = "${annotation.failOnError}"
     * @required
     */
    //@MojoParameter(required = true, defaultValue = "true", expression = "${annotation.failOnError}", description = "Indicates whether the build will continue even if there are compilation errors; defaults to true.")
    private Boolean failOnError = true;

    /**
     * Indicates whether the compiler output should be visible, defaults to true.
     * 
     * @parameter expression = "${annotation.outputDiagnostics}" default-value="true"
     * @required
     */
    //@MojoParameter(required = true, defaultValue = "true", expression = "${annotation.outputDiagnostics}", description = "Indicates whether the compiler output should be visible, defaults to true.")
    private boolean outputDiagnostics = true;

    /**
     * System properties set before processor invocation.
     * @parameter
     * 
     */
    //@MojoParameter(required = false, description = "System properties set before processor invocation.")
    private java.util.Map<String,String> systemProperties;
    
    /**
     * includes pattern
     * @parameter
     */
    //@MojoParameter( description="includes pattern")
    private String[] includes;
    
    /**
     * excludes pattern
     * @parameter
     */
    //@MojoParameter( description="excludes pattern")
    private String[] excludes;
    
    
    private ReentrantLock compileLock = new ReentrantLock();
    
    protected abstract File getSourceDirectory();
    protected abstract File getOutputClassDirectory();

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

        java.io.File sourceDir = getSourceDirectory();
        if( sourceDir==null ) {
            getLog().warn( "source directory cannot be read (null returned)! Processor task will be skipped");
            return;            
        }
        if( !sourceDir.exists() ) {
            getLog().warn( "source directory doesn't exist! Processor task will be skipped");
            return;                        
        }
        if( !sourceDir.isDirectory() ) {
            getLog().warn( "source directory is invalid! Processor task will be skipped");
            return;                        
        }
        
        final String includesString = ( includes==null || includes.length==0) ? "**/*.java" : StringUtils.join(includes, ",");
        final String excludesString = ( excludes==null || excludes.length==0) ? null : StringUtils.join(excludes, ",");

        List<File> files = FileUtils.getFiles(getSourceDirectory(), includesString, excludesString);

        Iterable< ? extends JavaFileObject> compilationUnits1 = null;

        

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
            getLog().info("No processors specified. Using default discovery mechanism.");
        }
        options.add("-d");
        options.add(getOutputClassDirectory().getPath());

        options.add("-s");
        options.add(outputDirectory.getPath());


        for (String option : options)
        {
            getLog().info("javac option: " + option);
        }

        DiagnosticListener<JavaFileObject> dl = null;
        if (outputDiagnostics)
        {
            dl = new DiagnosticListener<JavaFileObject>()
            {

                public void report(Diagnostic< ? extends JavaFileObject> diagnostic)
                {
                    getLog().info("diagnostic " + diagnostic);

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
                getLog().info( String.format("set system property : [%s] = [%s]",  e.getKey(), e.getValue() ));
                System.setProperty(e.getKey(), e.getValue());
            }

        }
        
        compileLock.lock();
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            
            if( compiler==null ) {
                getLog().error("JVM is not suitable for processing annotation! ToolProvider.getSystemJavaCompiler() is null.");
                return;
            }
            
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    
            if( files!=null && !files.isEmpty() ) {
                compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(files);
                
            }
            else {
                getLog().warn( "no source file(s) detected! Processor task will be skipped");
                return;
            }
    
    
            CompilationTask task = compiler.getTask(
                    new PrintWriter(System.out),
                    fileManager,
                    dl,
                    options, 
                    null,
                    compilationUnits1);
    
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
           compileLock.unlock(); 
        }
            
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
                    getLog().info("Adding compiler arg: " + arg);
                    options.add(arg);
                }
            }
        }
        if( optionMap!=null && !optionMap.isEmpty() ) {
            for( java.util.Map.Entry<String,Object> e : optionMap.entrySet() ) {
     
                if( !StringUtils.isEmpty(e.getKey()) && e.getValue()!=null ) {
                    String opt = String.format("-A%s=%s", e.getKey().trim(), e.getValue().toString().trim());
                    options.add( opt );
                    getLog().info("Adding compiler arg: " + opt);
                }
            }
                       
        }
    }

    private void addOutputToSourcesIfNeeded()
    {
        final Boolean add = addOutputDirectoryToCompilationSources;
        if (add == null || add.booleanValue())
        {
            getLog().info("Source directory: " + outputDirectory + " added");
            addCompileSourceRoot(project, outputDirectory.getAbsolutePath());
        }
    }

    protected abstract void addCompileSourceRoot(MavenProject project, String dir);
    public abstract File getDefaultOutputDirectory();

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


}
