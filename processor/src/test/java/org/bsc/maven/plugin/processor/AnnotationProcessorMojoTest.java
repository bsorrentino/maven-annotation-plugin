package org.bsc.maven.plugin.processor;


import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.*;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class AnnotationProcessorMojoTest {

  //final Path localRepoDir = Paths.get( System.getProperty("user.home"), ".m2", "repository");
  final Path localRepoDir = Paths.get( "src", "test", "resources", "localRepo");

  @Rule
  public MojoRule mojoRule = new MojoRule();

  @Rule
  public TestResources resources = new TestResources();

  final Path outputPath = Paths.get("target", "classes");

  /**
   *
   * @param baseDir
   * @param goal
   * @return
   *
   * @ref https://stackoverflow.com/a/42216471/521197
   */
   <T extends Mojo> T lookupConfiguredMojo( Path baseDir,  String goal ) throws Exception {

     final Path localRepoDir = Paths.get( System.getProperty("user.home"), ".m2", "repository");

     final MavenProject project = mojoRule.readMavenProject(baseDir.toFile());

     // Generate session
     final MavenSession session = mojoRule.newMavenSession(project);

     // add localRepo - framework doesn't do this on its own
     final org.apache.maven.artifact.repository.ArtifactRepository localRepo =
           new org.apache.maven.artifact.repository.MavenArtifactRepository("local",
             localRepoDir.toString(),
             new org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout(),
             new org.apache.maven.artifact.repository.ArtifactRepositoryPolicy( true,
                     org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                     org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE ),
             new org.apache.maven.artifact.repository.ArtifactRepositoryPolicy( true,
                     org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                     org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE )

     );

     session.getRequest().setLocalRepository(localRepo);

     // Generate Execution and Mojo for testing
     final MojoExecution execution = mojoRule.newMojoExecution("process");

     return (T)mojoRule.lookupConfiguredMojo(session, execution);

   }

  Path artifactPath( String artifact ) {
    return Paths.get( localRepoDir.toString(), artifact);
  }

  /**
   *
   */
  class TestLocalRepositoryManager implements org.eclipse.aether.repository.LocalRepositoryManager {

    final org.eclipse.aether.repository.LocalRepository localRepo;


    public TestLocalRepositoryManager() {
      localRepo = new org.eclipse.aether.repository.LocalRepository(localRepoDir.toString());
    }

    @Override
    public LocalRepository getRepository() {
      return localRepo;
    }

    @Override
    public String getPathForLocalArtifact(Artifact artifact) {
      return localRepoDir.toString();
    }

    @Override
    public String getPathForRemoteArtifact(Artifact artifact, RemoteRepository remoteRepository, String s) {
      throw new UnsupportedOperationException( format("getPathForRemoteArtifact(%s)", artifact));
    }

    @Override
    public String getPathForLocalMetadata(Metadata metadata) {
      throw new UnsupportedOperationException( format("getPathForLocalMetadata(%s)", metadata));
    }

    @Override
    public String getPathForRemoteMetadata(Metadata metadata, RemoteRepository remoteRepository, String s) {
      throw new UnsupportedOperationException( format("getPathForRemoteMetadata(%s)", metadata));
    }

    @Override
    public LocalArtifactResult find(RepositorySystemSession repositorySystemSession, LocalArtifactRequest localArtifactRequest) {

      final Artifact artifact  = localArtifactRequest.getArtifact();

      final LocalArtifactResult result = new LocalArtifactResult(localArtifactRequest);
      result.setAvailable(true);

      if( "jar".equals(artifact.getExtension()) ) {

        final File file = artifactPath(
                format("%s-%s.%s", artifact.getArtifactId(), artifact.getVersion(), artifact.getExtension()))
                .toFile();

        assertTrue(file.exists());

        result.setFile(file);
      }

      return result;
    }

    @Override
    public LocalMetadataResult find(RepositorySystemSession repositorySystemSession, LocalMetadataRequest localMetadataRequest) {
      throw new UnsupportedOperationException(format("find(RepositorySystemSession,LocalMetadataRequest:%s)", localMetadataRequest));
    }

    @Override
    public void add(RepositorySystemSession repositorySystemSession, LocalArtifactRegistration localArtifactRegistration) {
      throw new UnsupportedOperationException(format("add(RepositorySystemSession,LocalArtifactRegistration%s)",localArtifactRegistration));
    }

    @Override
    public void add(RepositorySystemSession repositorySystemSession, LocalMetadataRegistration localMetadataRegistration) {
      throw new UnsupportedOperationException(format("add(RepositorySystemSession,LocalMetadataRegistration:%s)", localMetadataRegistration));
    }
  }

  /**
   *
   * @param baseDir
   * @param goal
   * @return
   *
   * @ref https://stackoverflow.com/a/42216471/521197
   */
  MainAnnotationProcessorMojo lookupConfiguredMojoUsingAether( Path baseDir,  String goal ) throws Exception {

    final MavenProject project = mojoRule.readMavenProject(baseDir.toFile());

    // Generate session
    final MavenSession session = mojoRule.newMavenSession(project);

    // Generate Execution and Mojo for testing
    final MojoExecution execution = mojoRule.newMojoExecution(goal);

    final MainAnnotationProcessorMojo mojo =
            (MainAnnotationProcessorMojo) mojoRule.lookupConfiguredMojo(session, execution);

    org.eclipse.aether.DefaultRepositorySystemSession repoSession =
            (org.eclipse.aether.DefaultRepositorySystemSession)mojo.repoSession;

    org.eclipse.aether.repository.LocalRepositoryManager localRepoManager =
            repoSession.getLocalRepositoryManager();

    if( localRepoManager == null ) {

      repoSession.setLocalRepositoryManager( new TestLocalRepositoryManager() );
    }

    return mojo;

  }

  @Test
  public void testPR45() throws Exception {
    final File pom = Paths.get(outputPath.toString(), "pr45", "pom.xml").toFile();

    assertNotNull(pom);

    final Path baseDir = Paths.get(outputPath.toString(), "pr45");

    assertNotNull(baseDir);
    assertTrue( baseDir.toFile().exists() );
    assertTrue( baseDir.toFile().isDirectory() );

    final MainAnnotationProcessorMojo myMojo = lookupConfiguredMojoUsingAether(baseDir, "process");

    myMojo.execute();

    assertNotNull( myMojo.annotationProcessorPaths );
    assertEquals( 1, myMojo.annotationProcessorPaths.size() );

    final DependencyCoordinate coord = myMojo.annotationProcessorPaths.get(0);

    assertNotNull( coord );
    assertEquals( "org.mapstruct", coord.getGroupId() );
    assertEquals( "mapstruct-processor", coord.getArtifactId() );
    assertEquals( "1.4.2.Final", coord.getVersion() );

    assertNotNull( "repoSystem not initialized", myMojo.repoSystem );
    assertNotNull( "repoSession not initialized", myMojo.repoSession );
    assertNotNull( "remoteRepos not initialized", myMojo.remoteRepos );


    final Optional<String> processorPath = myMojo.buildProcessorPath();

    assertTrue( processorPath.isPresent() );
    assertEquals( artifactPath( "mapstruct-processor-1.4.2.Final.jar").toAbsolutePath().toString(), processorPath.get() );

  }
}
