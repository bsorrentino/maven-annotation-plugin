/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bsc.function;

/**
 *
 * @author bsorrentino
 * @param <T>
 */
public interface Consumer<T> {
    
    void accept(T t);
}
