/*
 *   Copyright (C) 2009 2010 2011 Bartolomeo Sorrentino <bartolomeo.sorrentino@gmail.com>
 *   SPDX-License-Identifier: MIT
 */
package org.bsc.maven.plugin.processor;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author bsorrentino
 *
 */
@Mojo(name="process",threadSafe=true,requiresDependencyResolution= ResolutionScope.COMPILE,defaultPhase= LifecyclePhase.GENERATE_SOURCES)
public class MainAnnotationProcessorMojo extends AbstractAnnotationProcessorMojo
{
    /** 
     * project classpath 
     * 
     */
    @Parameter( defaultValue="${project.compileClasspathElements}", required=true, readonly=true)
    private List<String> classpathElements;

    /** 
     * project sourceDirectory 
     * 
     */
    @Parameter( defaultValue="${project.build.sourceDirectory}", required = true)
    private File sourceDirectory;

    /**
     * default output directory
     * 
     */
    @Parameter( defaultValue="${project.build.directory}/generated-sources/apt", required = true)
    private File defaultOutputDirectory;

    /**
     * Set the destination directory for class files (same behaviour of -d option)
     * 
     */
    @Parameter( defaultValue="${project.build.outputDirectory}")
    private File outputClassDirectory;

    @Override
    protected File getOutputClassDirectory() {

        return outputClassDirectory;
    }

    @Override
    protected void addCompileSourceRoot(MavenProject project, String dir) {

        project.addCompileSourceRoot(dir);
    }

    @Override
    protected List<String> getAllCompileSourceRoots() {
        return project.getCompileSourceRoots();
    }

    @Override
    public File getDefaultOutputDirectory() {

        return defaultOutputDirectory;
    }

    @Override
    public java.util.Set<File> getSourceDirectories( final java.util.Set<File> result ) {
        result.add( sourceDirectory );
        
        return result;
    }

    @Override
    protected Set<String> getResourcesElements(Set<String> result) {
        final List<Resource> resources = project.getResources();

        if( resources!=null ) {
            for( Resource r : resources ) {
                result.add(r.getDirectory());
            }
        }

        return result;
    }

    @Override
    protected java.util.Set<String> getClasspathElements( final java.util.Set<String> result) {

        result.addAll( classpathElements );
        
        return result;
     }


}
