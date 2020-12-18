/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bsc.maven.plugin.processor;

import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 *
 * @author softphone
 */
public class ProcessorTest {
 
    @Test
    public void compareFile() {
    
        final java.io.File f = new java.io.File( "target/test-classes");
        final java.io.File f2 = new java.io.File( "target/classes");
        
        assertFalse( f.equals(f2));

        final java.io.File f3 = new java.io.File( "target/classes");

        assertTrue( f3.equals(f2));
        
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
        
        
        assertEquals( 1, fileSet.size() );

        fileSet.add( f2 );
        fileSet.add(f3);
        
        assertEquals( 2, fileSet.size() );
        
    }

    @Test
    public void testEncoding() {

        Charset.availableCharsets().entrySet().forEach( e -> {
            System.out.printf( "encoding { key:%s, name:%s, aliases:%s }\n", e.getKey(), e.getValue().name(), e.getValue().aliases());
        });

        final Charset utf8 = Charset.forName("utf8");
        assertEquals( "UTF-8", utf8.name() );
    }


}
