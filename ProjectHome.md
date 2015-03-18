A maven plugin to process annotation for jdk6 at compile time

This plugin helps to use from maven the new annotation processing provided by JDK6 integrated in java compiler

This plugin could be considered the 'alter ego' of maven apt plugin http://mojo.codehaus.org/apt-maven-plugin/

## [Plugin Documentation](http://bsc-documentation-repo.googlecode.com/svn/trunk/maven-annotation-plugin/site/index.html) ##

## Related plugin ##
| [m2e-apt](https://github.com/jbosstools/m2e-apt) | eclipse plugin from Jboss |
|:-------------------------------------------------|:--------------------------|
| [m2e-annotations](https://github.com/ilx/m2e-annotations) | eclipse plugin |

## Releases ##
|Jun 28,2014 | **Release 3.1.0-beta1**. | Available on **[MAVEN CENTRAL REPO](http://search.maven.org/#artifactdetails%7Corg.bsc.maven%7Cmaven-processor-plugin%7C3.1.0-beta1%7Cmaven-plugin)** |
|:-----------|:-------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------|

> This plugin use maven 3.1.0 runtime

  * [Issue 56](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=56) - diagnostic message from NOTE to INFO

|Jun 14,2013 | **Release 2.2.4**. | Available on **[MAVEN CENTRAL REPO](http://search.maven.org/#artifactdetails%7Corg.bsc.maven%7Cmaven-processor-plugin%7C2.2.4%7Cmaven-plugin)** |
|:-----------|:-------------------|:------------------------------------------------------------------------------------------------------------------------------------------------|

  * [Issue 54](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=54) - diagnostic messages mapping

|May 22,2013 | **Release 2.2.3**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 53](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=53) - encoding issue

|May 20,2013 | **Release 2.2.2**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 48](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=48) - quite mode. Thanks to [Michael Bayne](https://plus.google.com/117324666946590926145/posts) for patch

|Apr 6,2013 | **Release 2.2.1**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 51](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=51) - fix encoding issue. Thanks to [velo.br](https://code.google.com/u/102940695378864761236/) for patch

|Apr 4,2013 | **Release 2.2.0**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 50](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=50) - support for scanning maven source artifacts. Thanks to [ike.braun](https://code.google.com/u/118168999421226108052/) for patch


|Jan 30,2013 | **Release 2.1.1**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 47](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=47) - "duplicate class" error

|Nov 6,2012 | **Release 2.1.0**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 46](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=46) - regression bug
  * [Issue 44](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=44) - move code to use maven 3 annotation

|Oct 18,2012 | **Release 2.1.0-beta1**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------------|:---------------------------------------------------------------------|

  * [Issue 44](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=44) - move code to use maven 3 annotation

|Oct 09,2012 | **Release 2.0.8**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 43](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=43) - @parameter for addOutputDirectoryToCompilationSources

|Sep 01,2012 | **Release 2.0.7**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 42](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=42) - Support multiple source directories. Patch from [reydelamirienda](http://code.google.com/u/103602787751821521391/)
  * Now the plugin is compatible with [build-helper-maven-plugin](http://mojo.codehaus.org/build-helper-maven-plugin/)

|Aug 07,2012 | **Release 2.0.6**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * [Issue 36](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=36) - Eclipse JVM issue
  * [Issue 41](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=41) - Multithread

|Sep 13,2011 | **Release 2.0.5**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * fixed [issue 35](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=35) -  enhance options support

|Aug 11,2011 | **Release 2.0.4**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * fixed [issue 24](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=24) -  add support of options

|Jul 6,2011 | **Release 2.0.3**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:----------|:-------------------|:---------------------------------------------------------------------|

  * fixed [issue 29](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=29) -  Don't Swallow Cause. patch from [eric.dalquist](http://code.google.com/u/eric.dalquist/)
  * fixed [issue 30](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=30) -  Be more lenient of missing source directories
  * fixed [issue 31](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=31) -  plugin respect classpath/dependency order


|Apr 18,2011 | **Release 2.0.2**. | Available on **[MAVEN CENTRAL REPO](http://repo2.maven.org/maven2)** |
|:-----------|:-------------------|:---------------------------------------------------------------------|

  * fixed [issue 23](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=23) -  Publish to Maven Central - Thank you to Igor Vaynberg for support

|Feb 25,2011 | **Release 2.0.1**. | Available on **[INTERNAL MAVEN REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------------|

  * fixed [issue 28](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=28) -  add incudes/excludes feature

|Dec 05,2010 | **Release 2.0.0**. | Available on **[JAVA.NET REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------|

  * fixed [issue 26](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=26) -  Tested over maven3 release

|Aug 27,2010 | **Release 1.3.7**. | Available on **[JAVA.NET REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------|

  * fixed [issue 25](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=25)- skip processing whether no source files found

|Jun 17,2010 | **Release 1.3.6**. | Available on **[JAVA.NET REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------|

  * fixed [issue 22](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=22)- support system properties

|May 18,2010 | **Release 1.3.5**. | Available on **[JAVA.NET REPOSITORY](http://maven-annotation-plugin.googlecode.com/svn/docs/usage.html)** |
|:-----------|:-------------------|:----------------------------------------------------------------------------------------------------------|

  * fixed [issue 12](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=12)- support output class folder directly in configuration

|May 4,2010 | **Release 1.3.4**. | Available from maven  |
|:----------|:-------------------|:----------------------|

**fixed [issue 20](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=20)- add plugin dependencies to processor classpath**

|Apr 23,2010 | **Release 1.3.3**. | Available from maven  |
|:-----------|:-------------------|:----------------------|

**fixed [issue 19](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=19) - possible disable diagnostic output**

|Apr 11,2010 | **Release 1.3.2**. | Available from maven  |
|:-----------|:-------------------|:----------------------|

**fixed [issue 17](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=17) skip when packaging is pom (useful to add plugin declaration in parent pom)**

|Feb 04,2010 | **Release 1.3.1**. | Available from maven |
|:-----------|:-------------------|:---------------------|

**fixed [issue 15](https://code.google.com/p/maven-annotation-plugin/issues/detail?id=15) add failOnError parameter**


## import maven dependency ##

If you want stay tuned on each update, use the SNAPSHOT version as shown below

```
   <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <version>2.1.1-SNAPSHOT</version>

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