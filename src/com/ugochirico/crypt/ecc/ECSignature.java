/*
 * Created on 10-gen-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ugochirico.crypt.ecc;

import com.ugochirico.math.*;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public class ECSignature
{
    public static final int TYPE_ECSP_DSA 	= 0;
    public static final int TYPE_ECSP_NR 	= 1;
    
    public BigInteger m_c;
    public BigInteger m_d;
    public int m_nType;
    
    
    public ECSignature(BigInteger c, BigInteger d, int nType)
    {
        m_c = c;
        m_d = d;
        m_nType = nType;
    }
}
