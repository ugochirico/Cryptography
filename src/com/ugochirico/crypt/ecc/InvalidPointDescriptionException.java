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
 * Exception thrown at attempts to specify elliptic curve points with
 * an invalid or ill-defined description
 *
 * @author  Paulo S. L. M. Barreto <pbarreto@cryptix.org> */
/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public class InvalidPointDescriptionException extends GenericECException {

    protected static final String diagnostic =
        "Syntax error in curve point description";

    public InvalidPointDescriptionException() {
        super(diagnostic);
    }

    public InvalidPointDescriptionException(String detail) {
        super(diagnostic + ": " + detail);
    }
}
