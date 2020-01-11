/*
 * @(#)Random.java	1.39 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.ugochirico.util;

/**
 * An instance of this class is used to generate a stream of 
 * pseudorandom numbers. The class uses a 48-bit seed, which is 
 * modified using a linear congruential formula. (See Donald Knuth, 
 * <i>The Art of Computer Programming, Volume 2</i>, Section 3.2.1.) 
 * <p>
 * If two instances of <code>Random</code> are created with the same 
 * seed, and the same sequence of method calls is made for each, they 
 * will generate and return identical sequences of numbers. In order to 
 * guarantee this property, particular algorithms are specified for the 
 * class <tt>Random</tt>. Java implementations must use all the algorithms 
 * shown here for the class <tt>Random</tt>, for the sake of absolute 
 * portability of Java code. However, subclasses of class <tt>Random</tt> 
 * are permitted to use other algorithms, so long as they adhere to the 
 * general contracts for all the methods.
 * <p>
 * The algorithms implemented by class <tt>Random</tt> use a 
 * <tt>protected</tt> utility method that on each invocation can supply 
 * up to 32 pseudorandomly generated bits.
 * <p>
 * Many applications will find the <code>random</code> method in 
 * class <code>Math</code> simpler to use.
 *
 * @author  Frank Yellin
 * @version 1.39, 01/23/03
 * @see     java.lang.Math#random()
 * @since   JDK1.0
 */
public class Random extends java.util.Random
{
    private static final int BITS_PER_BYTE = 8;
    private static final int BYTES_PER_INT = 4;
    
    /**
     * Generates random bytes and places them into a user-supplied 
     * byte array.  The number of random bytes produced is equal to 
     * the length of the byte array.
     * 
     * @param bytes  the non-null byte array in which to put the 
     *               random bytes.
     * @since   JDK1.1
     */
    public void nextBytes(byte[] bytes) 
    {
        int numRequested = bytes.length;
    
        int numGot = 0, rnd = 0;
    
        while (true) 
        {
            for (int i = 0; i < BYTES_PER_INT; i++) 
            {
            if (numGot == numRequested)
                return;
    
            rnd = (i==0 ? next(BITS_PER_BYTE * BYTES_PER_INT)
                        : rnd >> BITS_PER_BYTE);
            bytes[numGot++] = (byte)rnd;
            }
        }
    }
}     
