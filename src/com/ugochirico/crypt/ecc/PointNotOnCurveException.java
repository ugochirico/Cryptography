/* $Id$
 *
 * Copyright (C) 1995-1999 Systemics Ltd.
 * on behalf of the Cryptix Development Team. All rights reserved.
 * 
 * Use, modification, copying and distribution of this software is subject to
 * the terms and conditions of the Cryptix General Licence. You should have 
 * received a copy of the Cryptix General License along with this library; 
 * if not, you can download a copy from http://www.cryptix.org/ .
 */
package com.ugochirico.crypt.ecc;

/**
 * Exception thrown at attempts to relate a point to a curve
 * on which that point does not lie
 *
 * @author  Paulo S. L. M. Barreto <pbarreto@cryptix.org> */
/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public class PointNotOnCurveException extends GenericECException {

    protected static final String diagnostic =
        "The given point does not belong to the given elliptic curve";

    public PointNotOnCurveException() {
        super(diagnostic);
    }

    public PointNotOnCurveException(String detail) {
        super(diagnostic + ": " + detail);
    }
}
