package org.bsc.maven.plugin.processor.test;

@GenerateClass
public class SampleClass {

    final public String id;
    final public String name;

    public SampleClass(String id, String name ) {
        this.name = name;
        this.id = id;
    }
}
