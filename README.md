## A maven plugin to process compile time annotation for jdk6 and above.

<a href="http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22maven-processor-plugin%22"><img src="https://img.shields.io/maven-central/v/org.bsc.maven/maven-processor-plugin.svg">
</a>&nbsp;<img src="https://img.shields.io/github/forks/bsorrentino/maven-annotation-plugin.svg">&nbsp;
<img src="https://img.shields.io/github/stars/bsorrentino/maven-annotation-plugin.svg">&nbsp;<a href="https://github.com/bsorrentino/maven-annotation-plugin/issues"><img src="https://img.shields.io/github/issues/bsorrentino/maven-annotation-plugin.svg">
</a>&nbsp;

This plugin helps to use from maven the new annotation processing integrated in java compiler provided from JDK6

This plugin could be considered the 'alter ego' of maven apt plugin http://mojo.codehaus.org/apt-maven-plugin/

### Documentation

* [Version 3](http://bsorrentino.github.io/maven-annotation-plugin/index.html)

* [Version 2 (old)](http://bsorrentino.github.io/maven-annotation-plugin/site2/index.html)

## Related plugin
[m2e-apt](https://github.com/jbosstools/m2e-apt) | eclipse plugin from Jboss
----|----
[m2e-annotations](https://github.com/ilx/m2e-annotations) | eclipse plugin

## Develop an annotation processor

* [ANNOTATION PROCESSING 101](http://hannesdorfmann.com/annotation-processing/annotationprocessing101)

## Releases

currently | **Release 3.3.3-SNAPSHOT** | available from  **[MAVEN CENTRAL REPO](https://oss.sonatype.org/content/repositories/snapshots/org/bsc/maven/maven-processor-plugin/3.3.3-SNAPSHOT/)** |
---- | ---- | ----

Sep 7,2017 | **Release 3.3.2**. | Available on **[MAVEN CENTRAL REPO](http://search.maven.org/#artifactdetails%7Corg.bsc.maven%7Cmaven-processor-plugin%7C3.3.2%7Cmaven-plugin)** |
----|----|----

* [Issue 69](https://github.com/bsorrentino/maven-annotation-plugin/issues/69) - Java 9 support
* [Pull request 70](https://github.com/bsorrentino/maven-annotation-plugin/pull/70) - Pass through additional compiler arguments

	> Thanks to [beikov](https://github.com/beikov) for contribution

Apr 11,2017 | **Release 3.3.1**. | Available on **[MAVEN CENTRAL REPO](http://search.maven.org/#artifactdetails%7Corg.bsc.maven%7Cmaven-processor-plugin%7C3.3.1%7Cmaven-plugin)** |
----|----|----

* [Issue 66](https://github.com/bsorrentino/maven-annotation-plugin/issues/66) - source 1.8 ignored
* [Issue 67](https://github.com/bsorrentino/maven-annotation-plugin/issues/67) - options are not taking in consideration

Apr 10,2017 | **Release 3.3**. | Available on **[MAVEN CENTRAL REPO](http://search.maven.org/#artifactdetails%7Corg.bsc.maven%7Cmaven-processor-plugin%7C3.3%7Cmaven-plugin)** |
----|----|----

* [Issue 64](https://github.com/bsorrentino/maven-annotation-plugin/issues/64) - Add option to `fork` for JDK9 support
* [Issue 65](https://github.com/bsorrentino/maven-annotation-plugin/issues/65) - Add support for `maven-toolchains-plugin`

|Oct 7,2016 | **Release 3.2.0**. | Available on **[MAVEN CENTRAL REPO](http://search.maven.org/#artifactdetails%7Corg.bsc.maven%7Cmaven-processor-plugin%7C3.2.0%7Cmaven-plugin)** |
|:-----------|:-------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------|

  * [Issue 63](https://github.com/bsorrentino/maven-annotation-plugin/issues/63) - -sourcepath is not configured

|Feb 2,2016 | **Release 3.1.0**. | Available on **[MAVEN CENTRAL REPO](http://search.maven.org/#artifactdetails%7Corg.bsc.maven%7Cmaven-processor-plugin%7C3.1.0%7Cmaven-plugin)** |
|:-----------|:-------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------|

  * [pull request #61](https://github.com/bsorrentino/maven-annotation-plugin/pull/61) fixing [issue #59](https://github.com/bsorrentino/maven-annotation-plugin/issues/59) - Add skip property

  > Thanks to [borisbrodski](https://github.com/borisbrodski) for contribution

|Jun 28,2014 | **Release 3.1.0-beta1**. | Available on **[MAVEN CENTRAL REPO](http://search.maven.org/#artifactdetails%7Corg.bsc.maven%7Cmaven-processor-plugin%7C3.1.0-beta1%7Cmaven-plugin)** |
|:-----------|:-------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------|

> This plugin use maven 3.1.0 runtime

  * [Issue 56](https://github.com/bsorrentino/maven-annotation-plugin/issues/56) - diagnostic message from NOTE to INFO

|Jun 14,2013 | **Release 2.2.4**. | Available on **[MAVEN CENTRAL REPO](http://search.maven.org/#artifactdetails%7Corg.bsc.maven%7Cmaven-processor-plugin%7C2.2.4%7Cmaven-plugin)** |
|:-----------|:-------------------|:------------------------------------------------------------------------------------------------------------------------------------------------|

  * [Issue 54](https://github.com/bsorrentino/maven-annotation-plugin/issues/54) - diagnostic messages mapping

|May 22,2013 | **Release 2.2.3**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 53](https://github.com/bsorrentino/maven-annotation-plugin/issues/53) - encoding issue

|May 20,2013 | **Release 2.2.2**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 48](https://github.com/bsorrentino/maven-annotation-plugin/issues/48) - quite mode. Thanks to [Michael Bayne](https://plus.google.com/117324666946590926145/posts) for patch

|Apr 6,2013 | **Release 2.2.1**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 51](https://github.com/bsorrentino/maven-annotation-plugin/issues/51) - fix encoding issue. Thanks to [velo.br](https://code.google.com/u/102940695378864761236/) for patch

|Apr 4,2013 | **Release 2.2.0**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 50](https://github.com/bsorrentino/maven-annotation-plugin/issues/50) - support for scanning maven source artifacts. Thanks to [ike.braun](https://code.google.com/u/118168999421226108052/) for patch


|Jan 30,2013 | **Release 2.1.1**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 47](https://github.com/bsorrentino/maven-annotation-plugin/issues/47) - "duplicate class" error

|Nov 6,2012 | **Release 2.1.0**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 46](https://github.com/bsorrentino/maven-annotation-plugin/issues/46) - regression bug
  * [Issue 44](https://github.com/bsorrentino/maven-annotation-plugin/issues/44) - move code to use maven 3 annotation

|Oct 18,2012 | **Release 2.1.0-beta1**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------------|:---------------------------------------------------------------------|

  * [Issue 44](https://github.com/bsorrentino/maven-annotation-plugin/issues/44) - move code to use maven 3 annotation

|Oct 09,2012 | **Release 2.0.8**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 43](https://github.com/bsorrentino/maven-annotation-plugin/issues/43) - @parameter for addOutputDirectoryToCompilationSources

|Sep 01,2012 | **Release 2.0.7**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 42](https://github.com/bsorrentino/maven-annotation-plugin/issues/42) - Support multiple source directories. Patch from [reydelamirienda](http://code.google.com/u/103602787751821521391/)
  * Now the plugin is compatible with [build-helper-maven-plugin](http://mojo.codehaus.org/build-helper-maven-plugin/)

|Aug 07,2012 | **Release 2.0.6**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 36](https://github.com/bsorrentino/maven-annotation-plugin/issues/36) - Eclipse JVM issue
  * [Issue 41](https://github.com/bsorrentino/maven-annotation-plugin/issues/41) - Multithread

|Sep 13,2011 | **Release 2.0.5**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * fixed [issue 35](https://github.com/bsorrentino/maven-annotation-plugin/issues/35) -  enhance options support

|Aug 11,2011 | **Release 2.0.4**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * fixed [issue 24](https://github.com/bsorrentino/maven-annotation-plugin/issues/24) -  add support of options

|Jul 6,2011 | **Release 2.0.3**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:----------|:-------------------|:---------------------------------------------------------------------|

  * fixed [issue 29](https://github.com/bsorrentino/maven-annotation-plugin/issues/29) -  Don't Swallow Cause. patch from [eric.dalquist](http://code.google.com/u/eric.dalquist/)
  * fixed [issue 30](https://github.com/bsorrentino/maven-annotation-plugin/issues/30) -  Be more lenient of missing source directories
  * fixed [issue 31](https://github.com/bsorrentino/maven-annotation-plugin/issues/31) -  plugin respect classpath/dependency order


|Apr 18,2011 | **Release 2.0.2**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * fixed [issue 23](https://github.com/bsorrentino/maven-annotation-plugin/issues/23) -  Publish to Maven Central - Thank you to Igor Vaynberg for support

|Feb 25,2011 | **Release 2.0.1**. | Available on **[INTERNAL MAVEN REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------------|

  * fixed [issue 28](https://github.com/bsorrentino/maven-annotation-plugin/issues/28) -  add incudes/excludes feature

|Dec 05,2010 | **Release 2.0.0**. | Available on **[JAVA.NET REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------|

  * fixed [issue 26](https://github.com/bsorrentino/maven-annotation-plugin/issues/26) -  Tested over maven3 release

|Aug 27,2010 | **Release 1.3.7**. | Available on **[JAVA.NET REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------|

  * fixed [issue 25](https://github.com/bsorrentino/maven-annotation-plugin/issues/25)- skip processing whether no source files found

|Jun 17,2010 | **Release 1.3.6**. | Available on **[JAVA.NET REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------|

  * fixed [issue 22](https://github.com/bsorrentino/maven-annotation-plugin/issues/22)- support system properties

|May 18,2010 | **Release 1.3.5**. | Available on **[JAVA.NET REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------|

  * fixed [issue 12](https://github.com/bsorrentino/maven-annotation-plugin/issues/12)- support output class folder directly in configuration

|May 4,2010 | **Release 1.3.4**. | Available from maven  |
|:----------|:-------------------|:----------------------|

**fixed [issue 20](https://github.com/bsorrentino/maven-annotation-plugin/issues/20)- add plugin dependencies to processor classpath**

|Apr 23,2010 | **Release 1.3.3**. | Available from maven  |
|:-----------|:-------------------|:----------------------|

**fixed [issue 19](https://github.com/bsorrentino/maven-annotation-plugin/issues/19) - possible disable diagnostic output**

|Apr 11,2010 | **Release 1.3.2**. | Available from maven  |
|:-----------|:-------------------|:----------------------|

**fixed [issue 17](https://github.com/bsorrentino/maven-annotation-plugin/issues/17) skip when packaging is pom (useful to add plugin declaration in parent pom)**

|Feb 04,2010 | **Release 1.3.1**. | Available from maven |
|:-----------|:-------------------|:---------------------|

**fixed [issue 15](https://github.com/bsorrentino/maven-annotation-plugin/issues/15) add failOnError parameter**


## import maven dependency ##

If you want stay tuned on each update, use the SNAPSHOT version as shown below

```
   <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <version>x.x.x-SNAPSHOT</version>

   </plugin>

```

## News ##

| From release 2.0.2 this plugin is available from  **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:----------------------------------------------------------------------------------------------------------|

|Created branch 1.3.x to continue to support maven2.x - Trunk has been moved to release 2.x that will support maven3 features|
|:---------------------------------------------------------------------------------------------------------------------------|

## Old Releases ##
|Jan 22,2010 | **Release 1.3**. | Available from maven|
|:-----------|:-----------------|:--------------------|
|Nov 25,2009 | **Release 1.2**. | Available from maven|
|Nov 08,2009 | **Release 1.1**. | Available from maven|
|Nov 08,2009 | **Release 1.0**. | Available from maven|
