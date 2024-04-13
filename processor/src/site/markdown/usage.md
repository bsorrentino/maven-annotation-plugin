

## Repository

  To use this plugin you have to include the following repository declaration in your POM

```xml
<pluginRepositories>

    <!-- IF YOU WANT STAY TUNED ON UPDATE REMOVE COMMENT -->

    <pluginRepository>
		<id>sonatype-repo</id>
		<url>https://oss.sonatype.org/content/repositories/snapshots</url>
		<releases>
			<enabled>false</enabled>
		</releases>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
    </pluginRepository>

</pluginRepositories>
```

## Usage

### Example 1

Sources will be generated into `target/generated-sources/apt/main/java`
Test sources into `target/generated-sources/apt/test/java`
Both directories will be added to the compilation path

```xml
<build>
  <plugins>
    <!-- Run annotation processors on src/main/java sources -->
    <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <executions>
        <execution>
          <id>process</id>
          <goals>
            <goal>process</goal>
          </goals>
          <phase>generate-sources</phase>
        </execution>
      </executions>
    </plugin>
    <!-- Run annotation processors on src/test/java sources -->
    <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <executions>
        <execution>
          <id>process-test</id>
          <goals>
            <goal>process-test</goal>
          </goals>
          <phase>generate-test-sources</phase>
        </execution>
      </executions>
    </plugin>
    <!-- Disable annotation processors during normal compilation -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <configuration>
        <compilerArgument>-proc:none</compilerArgument>
      </configuration>
    </plugin>

  </plugins>
</build>

```

###  Example 2

Generating sources into `src/main/generated` directory. This strategy is good if you wish to check in your generated sources into your SCM.
Run `mvn generate-sources` to generate the sources.

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <executions>
        <execution>
          <id>process</id>
          <goals>
            <goal>process</goal>
          </goals>
          <phase>generate-sources</phase>
          <configuration>
            <!-- source output directory -->
            <outputDirectory>src/main/generated</outputDirectory>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugin>
</build>
```

###  Example 3

Running specific annotation processors only.

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <executions>
        <execution>
          <id>process</id>
          <goals>
            <goal>process</goal>
          </goals>
          <phase>generate-sources</phase>
          <configuration>

            <processors>
               <!-- list of processors to use -->
               <processor>org.bsc.apt.BeanInfoAnnotationProcessor</processor>
            </processors>

          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugin>
</build>
```

###  Example 4

Passing options to processors

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <executions>
        <execution>
          <id>process</id>
          <goals>
            <goal>process</goal>
          </goals>
          <phase>generate-sources</phase>
          <configuration>
           <!-- STANDARD WAY -->
            <compilerArguments>-Amyoption=TRUE</outputDirectory>

           <!-- NEW FEATURE FROM VERSION 2.0.4-->
           <options>
           	<myoption>TRUE</myoption>
           </options>

          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugin>
</build>
```

### Example 5

Set System Properties

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <executions>
        <execution>
          <id>process</id>
          <goals>
            <goal>process</goal>
          </goals>
          <phase>generate-sources</phase>
          <configuration>

           <systemProperties>
            <log4j.ignoreTCL>true</log4j.ignoreTCL>
           </systemProperties>

          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugin>
</build>
```

### Example 6

use toolchain

```xml
<build>
  <plugins>

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-toolchains-plugin</artifactId>
        <executions>
            <execution>
                <goals>
                    <goal>toolchain</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <toolchains>
                <jdk>
                    <version>1.6</version>
                    <vendor>sun</vendor>
                </jdk>
            </toolchains>
        </configuration>
    </plugin>

    <plugin>
      <groupId>org.bsc.maven</groupId>
      <artifactId>maven-processor-plugin</artifactId>
      <executions>
        <execution>
          <id>process</id>
          <goals>
            <goal>process</goal>
          </goals>
          <phase>generate-sources</phase>
          <configuration>
            <processors>
               <processor>org.bsc.apt.BeanInfoAnnotationProcessor</processor>
            </processors>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugin>
</build>
```


