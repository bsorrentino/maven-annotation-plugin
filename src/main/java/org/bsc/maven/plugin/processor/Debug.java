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
import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.project.MavenProject;

/**
 * Debug helpers
 */
class Debug
{
    private final MavenProject project;

    public Debug(MavenProject project)
    {
        if (project == null)
        {
            throw new IllegalArgumentException("Argument 'project' cannot be null");
        }
        this.project = project;
    }

    public <T> void println(String name, Collection<T> e)
    {
        System.out.println(name);
        if (null == e)
        {
            return;
        }

        for (T a : e)
        {
            System.out.printf("\t[%s] %s\n", a.getClass().getName(), a.toString());
        }

    }

    public void printDeps(String name, Collection<org.apache.maven.model.Dependency> dependencies)
    {
        System.out.println(name);
        for (org.apache.maven.model.Dependency d : dependencies)
        {

            System.out.printf("dependency [%s]\n", d.toString());

            String versionlessKey = ArtifactUtils.versionlessKey(d.getGroupId(), d.getArtifactId());

            Artifact artifact = (Artifact)project.getArtifactMap().get(versionlessKey);

            if (null != artifact)
            {
                File file = artifact.getFile();
                System.out.printf("artifact [%s]\n", file.getPath());
            }
        }
    }

    public void printDebugInfo() throws Exception //DependencyResolutionRequiredException
    {
        //println("project.getCompileClasspathElements", project.getCompileClasspathElements());
        println("project.getCompileArtifacts", project.getCompileArtifacts());
        println("project.getCompileDependencies", project.getCompileDependencies());
        println("project.getDependencyArtifacts", project.getDependencyArtifacts());
        println("project.getArtifactMap", project.getArtifactMap().keySet());
        println("project.getArtifacts", project.getArtifacts());
        printDeps("project.getRuntimeDependencies", project.getRuntimeDependencies());
        printDeps("project.getDependencies", project.getDependencies());
    }

}
