/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bsc.maven.plugin.processor;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.hamcrest.core.*;
/**
 *
 * @author softphone
 */
public class ProcessorTest {
 
    @Test
    public void compareFile() {
    
        final java.io.File f = new java.io.File( "target/test-classes");
        final java.io.File f2 = new java.io.File( "target/classes");
        
        Assert.assertThat( f.equals(f2), Is.is(false));

        final java.io.File f3 = new java.io.File( "target/classes");

        Assert.assertThat( f3.equals(f2), Is.is(true));
        
    }
    
    @Test
    public void testDuplicatePath() {
       
        String homeFolder = System.getProperty("user.home");
        
        java.io.File f1 = new java.io.File( homeFolder );
        
        java.io.File f2 = new java.io.File( homeFolder );
        
        java.io.File f3 = new java.io.File( homeFolder, ".m2" );

        java.util.Set<java.io.File> fileSet = new java.util.HashSet<java.io.File>();
        
        
        fileSet.add( f1 );
        fileSet.add( f2 );
        
        
        Assert.assertThat( fileSet.size(), IsEqual.equalTo(1) );

        fileSet.add( f2 );
        fileSet.add(f3);
        
        Assert.assertThat( fileSet.size(), IsEqual.equalTo(2) );
        
    }
}
