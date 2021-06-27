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

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.codehaus.plexus.compiler.manager.CompilerManager;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;

import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

// 3.0.5
/*
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
*/
// 3.1.0


/**
 * @author bsorrentino
 */
public abstract class AbstractAnnotationProcessorMojo extends AbstractMojo {

  private static final String SOURCE_CLASSIFIER = "sources";

  /**
   * value of -release parameter in java 9+
   *
   * @since 3.3.3
   */
  @Parameter
  private String releaseVersion;

  /**
   *
   */
  @Parameter(defaultValue = "${project}", readonly = true)
  protected MavenProject project;

  /**
   *
   */
  @Parameter(property = "plugin.artifacts", readonly = true)
  private java.util.List<Artifact> pluginArtifacts;

  /**
   * Specify the directory where to place generated source files (same behaviour of -s option)
   */
  @Parameter
  private File outputDirectory;

  /**
   * <p>
   * Classpath elements to supply as annotation processor path. If specified, the compiler will detect annotation
   * processors only in those classpath elements. If omitted, the default classpath is used to detect annotation
   * processors. The detection itself depends on the configuration of {@code processors}.
   * </p>
   * <p>
   * Each classpath element is specified using their Maven coordinates (groupId, artifactId, version, classifier,
   * type). Transitive dependencies are added automatically. Example:
   * </p>
   *
   * <pre>
   * &lt;configuration&gt;
   *   &lt;annotationProcessorPaths&gt;
   *     &lt;path&gt;
   *       &lt;groupId&gt;org.sample&lt;/groupId&gt;
   *       &lt;artifactId&gt;sample-annotation-processor&lt;/artifactId&gt;
   *       &lt;version&gt;1.2.3&lt;/version&gt;
   *     &lt;/path&gt;
   *     &lt;!-- ... more ... --&gt;
   *   &lt;/annotationProcessorPaths&gt;
   * &lt;/configuration&gt;
   * </pre>
   *
   * @since 5.0
   */
  @Parameter
  List<DependencyCoordinate> annotationProcessorPaths;

  /**
   * Annotation Processor FQN (Full Qualified Name) - when processors are not specified, the default discovery mechanism will be used
   */
  @Parameter
  private String[] processors;

  /**
   * Additional compiler arguments
   */
  @Parameter
  private String compilerArguments;

  /**
   * Additional processor options (see javax.annotation.processing.ProcessingEnvironment#getOptions()
   */
  @Parameter(alias = "options")
  private java.util.Map<String, Object> optionMap;

  /**
   * Controls whether or not the output directory is added to compilation
   */
  @Parameter
  private Boolean addOutputDirectoryToCompilationSources;

  /**
   * Indicates whether the build will continue even if there are compilation errors; defaults to true.
   */
  @Parameter(defaultValue = "true", required = true, property = "annotation.failOnError")
  private Boolean failOnError = true;

  /**
   * Indicates whether the compiler output should be visible, defaults to true.
   */
  @Parameter(defaultValue = "true", required = true, property = "annotation.outputDiagnostics")
  private boolean outputDiagnostics = true;

  /**
   * System properties set before processor invocation.
   */
  @Parameter
  private java.util.Map<String, String> systemProperties;

  /**
   * includes pattern
   */
  @Parameter
  private String[] includes;

  /**
   * excludes pattern
   */
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
  @Parameter(defaultValue = "false")
  private boolean appendSourceArtifacts = false;

  /**
   * The character set used for decoding sources
   *
   * @since 2.2.1
   */
  @Parameter(property = "project.build.sourceEncoding")
  private String encoding;

  /**
   * The entry point to Aether, i.e. the component doing all the work.
   */
  @Component
  private RepositorySystem repoSystem;

  /**
   * The current repository/network configuration of Maven.
   */
  @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
  private RepositorySystemSession repoSession;

  /**
   * The project's remote repositories to use for the resolution of plugins and their dependencies.
   */
  @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
  private List<RemoteRepository> remoteRepos;

  /**
   * List of artifacts on which perform sources scanning
   * <p>
   * Each artifact must be specified in the form <b>grouId</b>:<b>artifactId</b>.
   * If you need to include all artifacts belonging a groupId, specify as artifactId the character '*'
   *
   * <hr>
   * e.g.
   * <pre>
   *
   * org.bsc.maven:maven-confluence-plugin
   * org.bsc.maven:*
   *
   * </pre>
   *
   * @since 2.2.5
   */
  @Parameter()
  private java.util.List<String> processSourceArtifacts = Collections.emptyList();

  /**
   * Set this to true to skip annotation processing.
   *
   * @since 3.1.0
   */
  @Parameter(defaultValue = "false", property = "skipAnnotationProcessing")
  protected boolean skip;

  /**
   * Allows running the compiler in a separate process.
   * If false it uses the built in compiler, while if true it will use an executable.
   * <p>
   * to set source and target use
   * <pre>
   *  maven.processor.source
   *  maven.processor.target
   * </pre>
   *
   * @since 3.3
   */
  @Parameter(defaultValue = "false", property = "fork")
  protected boolean fork;

  /**
   * Set this to true to skip annotation processing when there are no changes in the source files
   * compared to the generated files.
   *
   * @since 4.3
   */
  @Parameter(defaultValue = "false", property = "skipSourcesUnchangedAnnotationProcessing")
  protected boolean skipSourcesUnchanged;

  /**
   * Maven Session
   *
   * @since 3.3
   */
  @Parameter(defaultValue = "${session}", readonly = true)
  protected MavenSession session;

  /**
   * Plexus compiler manager.
   *
   * @since 3.3
   */
  @Component
  protected CompilerManager compilerManager;

  /**
   * @since 3.3
   */
  @Component
  private ToolchainManager toolchainManager;

  /**
   * for execution synchronization
   */
  private static final Lock syncExecutionLock = new ReentrantLock();


  /**
   * @return supported source directories
   */
  protected abstract java.util.Set<File> getSourceDirectories(java.util.Set<File> result);

  /**
   * @return output folder
   */
  protected abstract File getOutputClassDirectory();

  /**
   * @param project
   * @param dir
   */
  protected abstract void addCompileSourceRoot(MavenProject project, String dir);

  /**
   * @return
   */
  public abstract File getDefaultOutputDirectory();

  /**
   * @return
   */
  private Charset getCharsetFromEncoding() {

    return ofNullable(encoding).map(enc -> {
      try {
        return Charset.forName(encoding);
      } catch (IllegalCharsetNameException ex1) {
        getLog().warn(format("the given charset name [%s] is illegal!. default is used", encoding));
      } catch (UnsupportedCharsetException ex2) {
        getLog().warn(format("the given charset name [%s] is unsupported!. default is used", encoding));
      }
      return Charset.defaultCharset();
    }).orElseGet(() -> Charset.defaultCharset());

  }

  private String buildProcessor() {
    if (processors == null || processors.length == 0) {
      return null;
    }

    StringBuilder result = new StringBuilder();

    int i;

    for (i = 0; i < processors.length - 1; ++i) {
      result.append(processors[i]).append(',');
    }

    result.append(processors[i]);

    return result.toString();
  }

  protected abstract java.util.Set<String> getResourcesElements(java.util.Set<String> result);

  protected abstract java.util.Set<String> getClasspathElements(java.util.Set<String> result);

  protected abstract java.util.List<String> getAllCompileSourceRoots();

  private String buildCompileSourcepath(Consumer<String> onSuccess) {

    final java.util.List<String> roots = getAllCompileSourceRoots();

    if (roots == null || roots.isEmpty()) {
      return null;
    }

    final String result = StringUtils.join(roots.iterator(), File.pathSeparator);

    onSuccess.accept(result);

    return result;
  }

  private String buildCompileClasspath() {

    final java.util.Set<String> pathElements = new java.util.LinkedHashSet<>();

    getResourcesElements(pathElements);

    getClasspathElements(pathElements);

    if (pluginArtifacts != null) {

      for (Artifact a : pluginArtifacts) {

        if ("compile".equalsIgnoreCase(a.getScope()) || "runtime".equalsIgnoreCase(a.getScope())) {

          java.io.File f = a.getFile();

          if (f != null) pathElements.add(a.getFile().getAbsolutePath());

        }

      }
    }


    final StringBuilder result = new StringBuilder();

    for (String elem : pathElements) {
      result.append(elem).append(File.pathSeparator);
    }
    return result.toString();
  }

  private String buildModulePath() {

    return getClasspathElements(new java.util.LinkedHashSet<>())
        .stream()
        .collect(joining(File.pathSeparator));
  }

  /**
   *
   */
  @Override
  public void execute() throws MojoExecutionException {
    if (skip) {
      getLog().info("skipped");
      return;
    }

    if ("pom".equalsIgnoreCase(project.getPackaging())) // Issue 17
    {
      return;
    }

    syncExecutionLock.lock();

    try {
      executeWithExceptionsHandled();
    } catch (Exception e1) {
      super.getLog().error("error on execute: use -X to have details ");
      super.getLog().debug(e1);
      if (failOnError) {
        throw new MojoExecutionException("Error executing", e1);
      }
    } finally {
      syncExecutionLock.unlock();
    }

  }

  /**
   * TODO remove the part with ToolchainManager lookup once we depend on
   * 3.0.9 (have it as prerequisite). Define as regular component field then.
   *
   * @param jdkToolchain
   */
  private Toolchain getToolchain(final Map<String, String> jdkToolchain) {
    Toolchain tc = null;

    if (jdkToolchain != null && !jdkToolchain.isEmpty()) {
      // Maven 3.3.1 has plugin execution scoped Toolchain Support
      try {
        final Method getToolchainsMethod =
            toolchainManager.getClass().getMethod("getToolchains",
                MavenSession.class,
                String.class,
                Map.class);

        @SuppressWarnings("unchecked") final List<Toolchain> tcs =
            (List<Toolchain>) getToolchainsMethod.invoke(toolchainManager,
                session,
                "jdk",
                jdkToolchain);

        if (tcs != null && tcs.size() > 0) {
          tc = tcs.get(0);
        }
      } catch (Exception e) {
        // ignore
      }
    }

    if (tc == null) {
      tc = toolchainManager.getToolchainFromBuildContext("jdk", session);
    }

    return tc;
  }

  private List<String> prepareOptions(JavaCompiler compiler) throws MojoExecutionException {

    final List<String> options = new ArrayList<>(10);

    final String compileClassPath = buildCompileClasspath();

    final String processor = buildProcessor();

    options.add("-cp");
    options.add(compileClassPath);

    if (compiler.isSupportedOption("--module-path") == 1) {
      options.add("--module-path");
      options.add(buildModulePath());
    }

    buildCompileSourcepath(sourcepath -> {
      options.add("-sourcepath");
      options.add(sourcepath);
    });

    options.add("-proc:only");

    Optional<String> processorPath = this.buildProcessorPath();
    processorPath.ifPresent(value -> {
      options.add("-processorpath");
      options.add(value);
    });

    addCompilerArguments(options);

    if (processor != null) {
      options.add("-processor");
      options.add(processor);
    } else {
      getLog().warn("No processors specified. Using default discovery mechanism.");
    }
    options.add("-d");
    options.add(getOutputClassDirectory().getPath());

    options.add("-s");
    options.add(outputDirectory.getPath());

    ofNullable(releaseVersion).ifPresent(release -> {
      options.add("--release");
      options.add(releaseVersion);
    });

    ofNullable(project.getProperties()).ifPresent(properties -> {

      ofNullable(properties.getProperty("maven.compiler.source")).ifPresent(source -> {
        options.add("-source");
        options.add(source);
      });
      ofNullable(properties.getProperty("maven.compiler.target")).ifPresent(target -> {
        options.add("-target");
        options.add(target);
      });
    });

    ofNullable(encoding).ifPresent(enc -> {
      options.add("-encoding");
      options.add(getCharsetFromEncoding().name());
    });

    if (getLog().isDebugEnabled()) {
      for (String option : options) {
        getLog().debug(format("javac option: %s", option));
      }
    }

    return options;

  }

  private boolean isSourcesUnchanged(List<JavaFileObject> allSources) throws IOException {
    if (!areSourceFilesSameAsPreviousRun(allSources))
      return false;

    long maxSourceDate = allSources.stream()
        .map(JavaFileObject::getLastModified)
        .max(Long::compare)
        .orElse(Long.MIN_VALUE);

    // use atomic long for effectively final wrapper around long variable
    final AtomicLong maxOutputDate = new AtomicLong(Long.MIN_VALUE);

    Files.walkFileTree(outputDirectory.toPath(), new SimpleFileVisitor<java.nio.file.Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
          throws IOException {
        if (Files.isRegularFile(file)) {
          maxOutputDate.updateAndGet(t -> Math.max(t, file.toFile().lastModified()));
        }
        return FileVisitResult.CONTINUE;
      }
    });

    if (getLog().isDebugEnabled()) {
      getLog().debug("max source file date: " + maxSourceDate + ", max output date: " + maxOutputDate
          .get());
    }

    return maxSourceDate <= maxOutputDate.get();
  }

  /**
   * Checks the list of {@code allSources} against the stored list of source files in a previous run.
   *
   * @param allSources
   * @return {@code true} when the filenames of the previous run matches exactly with the current run.
   * @throws IOException
   */
  private boolean areSourceFilesSameAsPreviousRun(List<JavaFileObject> allSources) throws IOException {
    Path sourceFileList = outputDirectory.toPath().resolve(".maven-processor-source-files.txt");
    try {
      if (!Files.exists(sourceFileList)) {
        getLog().debug("File with previous sources " + sourceFileList + " not found, treating as first run");
        return false;
      }

      Set<String> previousSourceFiles = new HashSet<>(Files.readAllLines(sourceFileList));
      Set<String> currentSourceFiles = allSources.stream().map(JavaFileObject::getName).collect(Collectors.toSet());
      if (getLog().isDebugEnabled()) {
        final String removedSourceFiles = previousSourceFiles.stream()
            .filter(f -> !currentSourceFiles.contains(f))
            .collect(joining("\n"));
        getLog().debug(format("removed source files:\n%s", removedSourceFiles));

        final String newSourceFiles = currentSourceFiles.stream()
            .filter(f -> !previousSourceFiles.contains(f))
            .collect(joining("\n"));
        getLog().debug(format("new source files:\n%s", newSourceFiles));
      }
      return previousSourceFiles.equals(currentSourceFiles);
    } finally {
      outputDirectory.mkdirs();
      Files.write(sourceFileList, allSources.stream().map(JavaFileObject::getName).collect(Collectors.toSet()));
    }
  }

  private void executeWithExceptionsHandled() throws Exception {
    if (outputDirectory == null) {
      outputDirectory = getDefaultOutputDirectory();
    }

    ensureOutputDirectoryExists();
    addOutputToSourcesIfNeeded();

    // new Debug(project).printDebugInfo();

    final String includesString = (includes == null || includes.length == 0) ? "**/*.java" : StringUtils.join(includes, ",");
    final String excludesString = (excludes == null || excludes.length == 0) ? null : StringUtils.join(excludes, ",");

    java.util.Set<File> sourceDirs = getSourceDirectories(new java.util.HashSet<>(5));

    if (addCompileSourceRoots) {
      final java.util.List<String> sourceRoots = project.getCompileSourceRoots();
      if (sourceRoots != null) {
        for (String s : sourceRoots) {
          sourceDirs.add(new File(s));
        }
      }
    }

    if (additionalSourceDirectories != null && !additionalSourceDirectories.isEmpty()) {
      sourceDirs.addAll(additionalSourceDirectories);
    }

    if (sourceDirs == null) {
      throw new IllegalStateException("getSourceDirectories is null!");
    }

    final List<File> files = new java.util.ArrayList<>();

    for (File sourceDir : sourceDirs) {

      if (sourceDir == null) {
        getLog().warn("source directory is null! Processor task will be skipped!");
        continue;
      }

      getLog().debug(format("processing source directory [%s]", sourceDir.getPath()));

      if (!sourceDir.exists()) {
        getLog().warn(format("source directory [%s] doesn't exist! Processor task will be skipped!", sourceDir.getPath()));
        continue;
      }
      if (!sourceDir.isDirectory()) {
        getLog().warn(format("source directory [%s] is invalid! Processor task will be skipped!", sourceDir.getPath()));
        continue;
      }

      files.addAll(FileUtils.getFiles(sourceDir, includesString, excludesString));
    }

    final DiagnosticListener<JavaFileObject> dl = diagnostic -> {

      if (!outputDiagnostics) {
        return;
      }

      final Kind kind = diagnostic.getKind();

      if (null != kind)
        switch (kind) {
          case ERROR:
            getLog().error(format("diagnostic: %s", diagnostic));
            break;
          case MANDATORY_WARNING:
          case WARNING:
            getLog().warn(format("diagnostic: %s", diagnostic));
            break;
          case NOTE:
            getLog().info(format("diagnostic: %s", diagnostic));
            break;
          case OTHER:
            getLog().info(format("diagnostic: %s", diagnostic));
            break;
          default:
            break;
        }

    };

    if (systemProperties != null) {
      java.util.Set<Map.Entry<String, String>> pSet = systemProperties.entrySet();

      for (Map.Entry<String, String> e : pSet) {
        getLog().debug(format("set system property : [%s] = [%s]", e.getKey(), e.getValue()));
        System.setProperty(e.getKey(), e.getValue());
      }

    }


    final java.util.Map<String, String> jdkToolchain =
        java.util.Collections.emptyMap();

    final Toolchain tc = getToolchain(jdkToolchain);

    // If toolchain is set force fork compilation
    fork = (tc != null);

    if (fork) {
      getLog().debug("PROCESSOR COMPILER FORKED!");
    }

    //
    // add to allSource the files coming out from source archives
    //
    final java.util.List<JavaFileObject> allSources = new java.util.ArrayList<>();

    final UnzipService unzip = UnzipService.newInstance(getLog());

    if (fork) {
      processSourceArtifacts(artifact -> unzip.extractSourcesFromArtifactToTempDirectory(artifact, allSources,
          Paths.get(project.getBuild().getDirectory(), "extracted-sources")));
    } else {
      processSourceArtifacts(artifact -> unzip.extractSourcesFromArtifact(artifact, allSources));
    }

    //compileLock.lock();

    try {

      final JavaCompiler compiler = (fork) ?
          AnnotationProcessorCompiler.createOutProcess(
              tc,
              compilerManager,
              project,
              session) :
          AnnotationProcessorCompiler.createInProcess();


      if (compiler == null) {
        getLog().error("JVM is not suitable for processing annotation! ToolProvider.getSystemJavaCompiler() is null.");
        return;
      }

      final StandardJavaFileManager fileManager =
          compiler.getStandardFileManager(null,
              null,
              getCharsetFromEncoding());

      if (files != null && !files.isEmpty()) {

        for (JavaFileObject f : fileManager.getJavaFileObjectsFromFiles(files)) {
          allSources.add(f);
        }
        ;

      }

      if (allSources.isEmpty()) {
        getLog().warn("no source file(s) detected! Processor task will be skipped");
        return;
      }

      if (skipSourcesUnchanged && isSourcesUnchanged(allSources)) {
        getLog().info("no source file(s) change(s) detected! Processor task will be skipped");
        return;
      }
      final java.util.List<String> options = prepareOptions(compiler);

      final CompilationTask task = compiler.getTask(
          new PrintWriter(System.out),
          fileManager,
          dl,
          options,
          null,
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
      if (!task.call()) {
        throw new Exception("error during compilation");
      }
    } finally {
      //compileLock.unlock();
    }

  }

  private List<File> scanSourceDirectorySources(File sourceDir) throws IOException {
    if (sourceDir == null) {
      getLog().warn("source directory cannot be read (null returned)! Processor task will be skipped");
      return null;
    }
    if (!sourceDir.exists()) {
      getLog().warn("source directory doesn't exist! Processor task will be skipped");
      return null;
    }
    if (!sourceDir.isDirectory()) {
      getLog().warn("source directory is invalid! Processor task will be skipped");
      return null;
    }

    final String includesString = (includes == null || includes.length == 0) ? "**/*.java" : StringUtils.join(includes, ",");
    final String excludesString = (excludes == null || excludes.length == 0) ? null : StringUtils.join(excludes, ",");

    final List<File> files = FileUtils.getFiles(sourceDir, includesString, excludesString);
    return files;
  }

  private void addCompilerArguments(List<String> options) {
    if (!StringUtils.isEmpty(compilerArguments)) {
      for (String arg : compilerArguments.split(" ")) {
        if (!StringUtils.isEmpty(arg)) {
          arg = arg.trim();
          getLog().debug(format("Adding compiler arg: %s", arg));
          options.add(arg);
        }
      }
    }
    if (optionMap != null && !optionMap.isEmpty()) {
      for (java.util.Map.Entry<String, Object> e : optionMap.entrySet()) {

        if (!StringUtils.isEmpty(e.getKey()) && e.getValue() != null) {
          String opt = format("-A%s=%s", e.getKey().trim(), e.getValue().toString().trim());
          options.add(opt);
          getLog().debug(format("Adding compiler arg: %s", opt));
        }
      }

    }
  }

  private void addOutputToSourcesIfNeeded() {
    final Boolean add = addOutputDirectoryToCompilationSources;
    if (add == null || add.booleanValue()) {
      getLog().debug(format("Source directory: %s added", outputDirectory));
      addCompileSourceRoot(project, outputDirectory.getAbsolutePath());
    }
  }

  private void ensureOutputDirectoryExists() {
    final File f = outputDirectory;
    if (!f.exists()) {
      f.mkdirs();
    }
    if (!getOutputClassDirectory().exists()) {
      getOutputClassDirectory().mkdirs();
    }
  }

  private boolean matchArtifact(Artifact dep/*, ArtifactFilter filter*/) {

    if (processSourceArtifacts == null || processSourceArtifacts.isEmpty()) {
      return false;
    }

    for (String a : processSourceArtifacts) {

      if (a == null || a.isEmpty()) continue;

      final String[] token = a.split(":");

      final boolean matchGroupId = dep.getGroupId().equals(token[0]);

      if (!matchGroupId) continue;

      if (token.length == 1) return true;

      if (token[1].equals("*")) return true;

      return dep.getArtifactId().equals(token[1]);

    }
    return false;
  }

  private Optional<Artifact> resolveSourceArtifact(Artifact dep) throws ArtifactResolutionException {

    if (!matchArtifact(dep)) {
      return empty();
    }

    final ArtifactTypeRegistry typeReg = repoSession.getArtifactTypeRegistry();

    final DefaultArtifact artifact =
        new DefaultArtifact(dep.getGroupId(),
            dep.getArtifactId(),
            SOURCE_CLASSIFIER,
            null,
            dep.getVersion(),
            typeReg.get(dep.getType()));

    final ArtifactRequest request = new ArtifactRequest();
    request.setArtifact(artifact);
    request.setRepositories(remoteRepos);

    getLog().debug(format("Resolving artifact %s from %s", artifact, remoteRepos));

    final ArtifactResult result = repoSystem.resolveArtifact(repoSession, request);

    return ofNullable(RepositoryUtils.toArtifact(result.getArtifact()));
  }

  private void processSourceArtifacts(Consumer<Artifact> closure) {

    final java.util.Set<Artifact> depArtifacts = this.project.getDependencyArtifacts();
    if (depArtifacts != null) {

      for (Artifact dep : depArtifacts) {

        if (dep.hasClassifier() && SOURCE_CLASSIFIER.equals(dep.getClassifier())) {

          if (appendSourceArtifacts) {
            closure.accept(dep);
          }
          //getLog().debug("Append source artifact to classpath: " + dep.getGroupId() + ":" + dep.getArtifactId());
          //this.sourceArtifacts.add(dep.getFile());
        } else {
          try {
            resolveSourceArtifact(dep).ifPresent(closure::accept);

          } catch (ArtifactResolutionException ex) {
            getLog().warn(format(" sources for artifact [%s] not found!", dep.toString()));
            getLog().debug(ex);

          }
        }
      }
    }
  }

  private Optional<List<String>> resolveProcessorPathEntries() throws MojoExecutionException {
    if (this.annotationProcessorPaths == null || this.annotationProcessorPaths.isEmpty()) {
      return Optional.empty();
    }

    try {
      Set<Dependency> requiredDependencies = new LinkedHashSet<>();

      for (DependencyCoordinate coord : this.annotationProcessorPaths) {
        //@formatter:off
        DefaultArtifact artifact = new DefaultArtifact(
            coord.getGroupId(),
            coord.getArtifactId(),
            coord.getClassifier(),
            coord.getType(),
            coord.getVersion());
        //@formatter:on

        requiredDependencies.add(new Dependency(artifact, Artifact.SCOPE_RUNTIME));
      }
      CollectRequest collectRequest = new CollectRequest(requiredDependencies.iterator().next(),
          new ArrayList<>(requiredDependencies), this.remoteRepos);
      DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, null);

      DependencyResult resolutionResult = this.repoSystem.resolveDependencies(this.repoSession,
          dependencyRequest);

      List<String> artifactPaths = new ArrayList<>(resolutionResult.getArtifactResults().size());
      for (ArtifactResult artifactResult : resolutionResult.getArtifactResults()) {
        artifactPaths.add(artifactResult.getArtifact().getFile().getAbsolutePath());
      }

      return Optional.of(artifactPaths);
    } catch (Exception e) {
      throw new MojoExecutionException(
          "Resolution of annotationProcessorPath dependencies failed: " + e.getLocalizedMessage(), e);
    }
  }

  private Optional<String> buildProcessorPath() throws MojoExecutionException {
    Optional<List<String>> processorPathEntries = this.resolveProcessorPathEntries();
    return processorPathEntries.map(value -> StringUtils.join(value.iterator(), File.pathSeparator));
  }
}
