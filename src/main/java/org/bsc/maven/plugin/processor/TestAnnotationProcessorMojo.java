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
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author bsorrentino
 *
 * @goal process-test
 * @requiresDependencyResolution test
 * @phase generate-test-sources
 */
//@MojoGoal("process-test")
//@MojoRequiresDependencyResolution(value = "test")
//@MojoPhase("generate-test-sources")
public class TestAnnotationProcessorMojo extends AbstractAnnotationProcessorMojo
{

    /** 
     * project classpath 
     * 
     * @parameter expression = "${project.testClasspathElements}"
     * @required
     * @readonly
     */
    @SuppressWarnings("rawtypes")
    //@MojoParameter(expression = "${project.testClasspathElements}", required = true, readonly = true)
    private List classpathElements;


    /**
     * 
     * @parameter expression = "${project.build.testSourceDirectory}"
     * @required
     */
    //@MojoParameter(expression = "${project.build.testSourceDirectory}", required = true)
    private File sourceDirectory;

    /**
     * @parameter expression = "${project.build.directory}/generated-sources/apt-test"
     * @required
     */
    //@MojoParameter(expression = "${project.build.directory}/generated-sources/apt-test", required = true)
    private File defaultOutputDirectory;

    /**
     * Set the destination directory for class files (same behaviour of -d option)
     * 
     * @parameter expression="${project.build.testOutputDirectory}" 
     */
    //@MojoParameter(required = false, expression="${project.build.testOutputDirectory}", description = "Set the destination directory for class files (same behaviour of -d option)")
    private File outputClassDirectory;

    @Override
    public java.util.Set<File> getSourceDirectories()
    {
        java.util.List<String> sourceRoots = project.getCompileSourceRoots();
        java.util.Set<File> result = new java.util.HashSet<File>( sourceRoots.size() + 1);
        
        result.add( sourceDirectory );
        for( String s : sourceRoots ) {
            result.add( new File(s) );
        }
        
        return result;
    }

    protected void addCompileSourceRoot(MavenProject project, String dir)
    {
        project.addTestCompileSourceRoot(dir);
    }

    @Override
    public File getDefaultOutputDirectory()
    {
        return defaultOutputDirectory;
    }

    @Override
    protected File getOutputClassDirectory()
    {
        return outputClassDirectory;
    }


    @SuppressWarnings("unchecked")
    @Override
    protected java.util.Set<String> getClasspathElements( java.util.Set<String> result )
    {
        List<Resource> resources = project.getTestResources();

        if( resources!=null ) {
            for( Resource r : resources ) {
                result.add(r.getDirectory());
            }
        }

        result.addAll( classpathElements );

        return result;
    }


}
