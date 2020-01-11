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


/**
 * Exception thrown at attempts to perform arithmetic operations
 * on elements of different finite fields
 *
 * @author  Paulo S. L. M. Barreto <pbarreto@cryptix.org>
/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME
 */
public class DifferentFieldsException extends GenericECException {

    protected static final String diagnostic =
        "Cannot combine elements from distinct finite fields";

    public DifferentFieldsException() {
        super(diagnostic);
    }

    public DifferentFieldsException(String detail) {
        super(diagnostic + ": " + detail);
    }
}