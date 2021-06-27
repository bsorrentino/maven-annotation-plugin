package org.bsc.maven.plugin.processor;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class AnnotationProcessorMojoTest {

  @Rule
  public MojoRule mojoRule = new MojoRule();

  @Rule
  public TestResources resources = new TestResources();

  final Path outputPath = Paths.get("target", "classes");

  @Test
  public void testPR45() throws Exception {
    final File pom = Paths.get(outputPath.toString(), "pr45", "pom.xml").toFile();

    assertNotNull(pom);

    final File baseDir = Paths.get(outputPath.toString(), "pr45").toFile();
    assertNotNull(baseDir);
    assertTrue( baseDir.exists() );
    assertTrue( baseDir.isDirectory() );

    final Mojo mojo = mojoRule.lookupConfiguredMojo(baseDir, "process");

    assertTrue(mojo instanceof MainAnnotationProcessorMojo);

    final MainAnnotationProcessorMojo myMojo = (MainAnnotationProcessorMojo) mojo;

    myMojo.execute();

    assertNotNull( myMojo.annotationProcessorPaths );
    assertEquals( 1, myMojo.annotationProcessorPaths.size() );

    final DependencyCoordinate coord = myMojo.annotationProcessorPaths.get(0);

    assertNotNull( coord );
    assertEquals( "org.mapstruct", coord.getGroupId() );
    assertEquals( "mapstruct-processor", coord.getArtifactId() );
    assertEquals( "1.0", coord.getVersion() );


  }
}
