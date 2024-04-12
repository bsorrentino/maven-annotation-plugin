## A maven plugin to process compile time annotation for jdk8 and above.

<a href="http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22maven-processor-plugin%22"><img src="https://img.shields.io/maven-central/v/org.bsc.maven/maven-processor-plugin.svg">
</a>&nbsp;<img src="https://img.shields.io/github/forks/bsorrentino/maven-annotation-plugin.svg">&nbsp;
<img src="https://img.shields.io/github/stars/bsorrentino/maven-annotation-plugin.svg">&nbsp;<a href="https://github.com/bsorrentino/maven-annotation-plugin/issues"><img src="https://img.shields.io/github/issues/bsorrentino/maven-annotation-plugin.svg">
</a>&nbsp;[![Join the chat at https://gitter.im/bsorrentino/maven-annotation-plugin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/bsorrentino/maven-annotation-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


This plugin helps to use from maven the new annotation processing integrated in java compiler provided from JDK8

This plugin was born as the 'alter ego' of maven apt plugin [apt-maven-plugin](https://search.maven.org/artifact/org.codehaus.mojo/apt-maven-plugin/1.0-alpha-5/maven-plugin)

## Documentation

* [Last Version](http://bsorrentino.github.io/maven-annotation-plugin/index.html)
* [Version 2 (old)](http://bsorrentino.github.io/maven-annotation-plugin/site2/index.html)

## Related plugin

 plugin | info
--- | ---
[m2e-apt](https://github.com/jbosstools/m2e-apt) | eclipse plugin from Jboss
[m2e-annotations](https://github.com/ilx/m2e-annotations) | eclipse plugin

## Develop an annotation processor

* [ANNOTATION PROCESSING 101](http://hannesdorfmann.com/annotation-processing/annotationprocessing101)

## Releases

 Date  | Version                                                                                                   | Info
--- |-----------------------------------------------------------------------------------------------------------| ---
**Apr 12, 2024** | [Release 5.1](https://github.com/bsorrentino/maven-annotation-plugin/releases/tag/v5.1)                   | merge PR [#104](https://github.com/bsorrentino/maven-annotation-plugin/pull/104) - Fixed unchanged sources check.Thanks to [AndreaBaroncelli](https://github.com/AndreaBaroncelli)
**Sep 21, 2023** | [Release 5.0](https://github.com/bsorrentino/maven-annotation-plugin/releases/tag/v5.0)                   | fix [#102](https://github.com/bsorrentino/maven-annotation-plugin/issues/102) - Plugin validation issues on Maven 3.9
**Jul 12, 2021** | [Release 5.0-rc3](https://github.com/bsorrentino/maven-annotation-plugin/releases/tag/v5.0-rc3)           | merge PR [#96](https://github.com/bsorrentino/maven-annotation-plugin/pull/96) - Thanks to [Ulysses Rangel RIbeiro](https://github.com/ulyssesrr)
**Jul 12, 2021** | [Release 5.0-jdk8-rc3](https://github.com/bsorrentino/maven-annotation-plugin/releases/tag/v5.0-jdk8-rc3) | Maintenance release compatible with JDK8
**Jun 28, 2021** | [Release 5.0-rc2](https://github.com/bsorrentino/maven-annotation-plugin/releases/tag/v5.0-rc2)           | merge PR [#95](https://github.com/bsorrentino/maven-annotation-plugin/pull/95) - Thanks to [Ulysses Rangel RIbeiro](https://github.com/ulyssesrr)
**Jun 28, 2021** | [Release 5.0-jdk8-rc2](https://github.com/bsorrentino/maven-annotation-plugin/releases/tag/v5.0-jdk8-rc2) | Maintenance release compatible with JDK8
**Feb 04, 2021** | [Release 5.0-rc1](https://github.com/bsorrentino/maven-annotation-plugin/releases/tag/v5.0-rc1)           | fix issue [#91](https://github.com/bsorrentino/maven-annotation-plugin/issues/91) [#92](https://github.com/bsorrentino/maven-annotation-plugin/issues/92) [#93](https://github.com/bsorrentino/maven-annotation-plugin/issues/93)
**Feb 04, 2021** | [Release 5.0-jdk8-rc1](https://github.com/bsorrentino/maven-annotation-plugin/releases/tag/v5.0-jdk8-rc1) | Maintenance release compatible with JDK8

## import maven dependency ##

If you want stay tuned on each update, use the SNAPSHOT version as shown below

```
   <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <version>x.x.x-SNAPSHOT</version>

   </plugin>

```

----

[Releases History](HISTORY.md)